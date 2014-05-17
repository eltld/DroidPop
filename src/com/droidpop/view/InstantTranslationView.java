package com.droidpop.view;

import me.wtao.animation.AnimatorFactory;
import me.wtao.view.FloatingView;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.droidpop.R;
import com.droidpop.app.PreferenceSettingsManager;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;

public class InstantTranslationView extends FloatingView {
	
	protected static final long FADE_IN_DURATION = 500;
	protected static final long FADE_OUT_DURATION = 500;
	
	protected static ObjectAnimator sFadeinAnim;
	protected static ObjectAnimator sFadeoutAnim;

	private final PointF mPrevLocation = new PointF();
	
	private RelativeLayout mAnimLayout;
	private ViewGroup mNavView;
	private ViewGroup mContentView;
	
	private EditText mWordEntryEditor;
	
	public InstantTranslationView(Context context) {
		super(context);
		init(context);
	}

	public InstantTranslationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InstantTranslationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE:
			final int deltaX = (int) (event.getRawX() - mPrevLocation.x);
			final int deltaY = (int) (event.getRawY() - mPrevLocation.y);
			
			mWindowParams.x += deltaX;
			mWindowParams.y += deltaY;
			sWindowManager.updateViewLayout(this, mWindowParams);
			
			break;
		}
		
		mPrevLocation.x = event.getRawX();
		mPrevLocation.y = event.getRawY();
		
		return true;
	}
	
	public void setWordEntryQuery(String query) {
		mWordEntryEditor.setText(query);
	}

	/**
	 * 
	 * @param anchor
	 *            the popup's anchor point
	 */
	public void popUp(Point anchor) {
		if(!hasAttachedToWindow()) {
			attachedToWindow();
		}
		
		mWindowParams.x = anchor.x;
		mWindowParams.y = anchor.y;
		sWindowManager.updateViewLayout(this, mWindowParams);

		show();
	}
	
	@Override
	public void show() {
		if(!hasAttachedToWindow()) {
			attachedToWindow();
		}
		
		if(null == sFadeinAnim) {
			synchronized (this) {
				if(null == sFadeinAnim) {
					sFadeinAnim = ObjectAnimator.ofFloat(mAnimLayout, "alpha", 0.f, 1.f);
					sFadeinAnim.setDuration(FADE_IN_DURATION);
					sFadeinAnim.setInterpolator(new AccelerateInterpolator());
					
					sFadeinAnim.addListener(new AnimatorFactory.SimpleAnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							InstantTranslationView.super.show();
						}
					});
				}
			}
		}
		
		if (getVisibility() != VISIBLE) {
			sFadeinAnim.start();
		}
	}

	@Override
	public void hide() {
		hide(false);
	}
	
	@Override
	public void dismiss() {
		// keep the current position
		PreferenceSettingsManager.prepare();
		PreferenceSettingsManager.add(
				PreferenceSettingsManager.LIGHT_TRANSLATION_WINDOW_POSITION_X,
				mWindowParams.x);
		PreferenceSettingsManager.add(
				PreferenceSettingsManager.LIGHT_TRANSLATION_WINDOW_POSITION_Y,
				mWindowParams.y);
		PreferenceSettingsManager.batchCommit();
		
		if (hasAttachedToWindow() && getVisibility() == VISIBLE) {
			hide(true);
		} else {
			super.dismiss();
		}
	}

	@Override
	protected void onInitializeWindowLayoutParams() {
		super.onInitializeWindowLayoutParams();

		mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

		mWindowParams.x = (Integer) PreferenceSettingsManager
				.get(PreferenceSettingsManager.LIGHT_TRANSLATION_WINDOW_POSITION_X);
		mWindowParams.y = (Integer) PreferenceSettingsManager
				.get(PreferenceSettingsManager.LIGHT_TRANSLATION_WINDOW_POSITION_Y);
		
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
	}
	
	@Override
	protected final View setContentView(int layoutId) {
		View v = LayoutInflater.from(getContext()).inflate(layoutId, null);
		
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mContentView.addView(v, flp);
		
		return v;
	}
	
	private void init(Context context) {
		initBaseLayout(context);
		
		View navCloseView = mNavView.findViewById(R.id.nav_close);
		navCloseView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		mWordEntryEditor = (EditText) mNavView.findViewById(R.id.word_entry_editor);
		FontFactory factory = new FontFactory(getContext());
		mWordEntryEditor.setTypeface(factory.buildFont(Font.EXISTENCE));
	}
	
	private void initBaseLayout(Context context) {
		mAnimLayout = new RelativeLayout(context);
		addView(mAnimLayout);
		
		LayoutInflater inflater = LayoutInflater.from(context);

		mNavView = (ViewGroup) inflater.inflate(
				R.layout.layout_instant_translation_nav_view, null);
		RelativeLayout.LayoutParams paramOfNav = new RelativeLayout.LayoutParams(
				384,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		paramOfNav.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mAnimLayout.addView(mNavView, paramOfNav);

		mContentView = new FrameLayout(context);
		RelativeLayout.LayoutParams paramOfContent = new LayoutParams(
				384,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		paramOfContent.addRule(RelativeLayout.BELOW, R.id.instant_translation_nav);
		mAnimLayout.addView(mContentView, paramOfContent);
	}
	
	private void enableWordEntryEditor(boolean editable) {
		if (editable) {
			final int text = InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL;
			final int textAutoComplete = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
			final int textAutoCorrect = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
			mWordEntryEditor.setInputType(text | textAutoComplete
					| textAutoCorrect);
		} else {
			final int none = 0;
			mWordEntryEditor.setInputType(none);
		}
	}
	
	private void hide(final boolean closed) {
		if(null == sFadeoutAnim) {
			synchronized (this) {
				if(null == sFadeoutAnim) {
					sFadeoutAnim = ObjectAnimator.ofFloat(mAnimLayout, "alpha", 0.f);
					sFadeoutAnim.setDuration(FADE_OUT_DURATION);
					sFadeoutAnim.setInterpolator(new DecelerateInterpolator());
					
					sFadeoutAnim.addListener(new AnimatorFactory.SimpleAnimatorListener() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if(closed) {
								InstantTranslationView.super.dismiss();
							} else {
								InstantTranslationView.super.hide();
							}
						}
					});
				}
			}
		}
		
		sFadeoutAnim.start();
	}

}
