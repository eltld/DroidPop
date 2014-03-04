package com.droidpop.view;

import me.wtao.os.UiThreadHandler;
import me.wtao.utils.ScreenMetrics;
import me.wtao.view.FloatingView;
import me.wtao.view.PointerFactory;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.app.PreferenceSettingsManager;
import com.droidpop.app.PreferenceSettingsManager.DeveloperOption;

public class SystemOverlayView extends FloatingView implements
		OnScreenTouchListener {

	/**
	 * only support one double-click translate
	 */
	private static final int MAX_MULTI_TOUCH_POINT_SUPPORTED = 1;
	
	private final ViewGroup mContentView;
	private final TouchPointer[] mTouchPointers;
	private Boolean mTouchable;
	private boolean mShowTouches;
	private boolean mShowTouchesChecked;

	public SystemOverlayView(Context context) {
		super(context);
		
		mContentView = new AbsoluteLayout(context); // deprecated but best layout here
		FloatingView.LayoutParams flp = new FloatingView.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		addView(mContentView, flp);
		
//		// used to test
//		setBackgroundResource(R.color.transparent_blue_light);
//		mContentView.setBackgroundResource(R.color.transparent_blue_light);
		
		PointerFactory factory = new PointerFactory(context);
		mTouchPointers = new TouchPointer[MAX_MULTI_TOUCH_POINT_SUPPORTED];
		for (int i = 0; i != MAX_MULTI_TOUCH_POINT_SUPPORTED; ++i) {
			PointerIcon icon = factory.createDefaultPointer();
			Bitmap bitmap = icon.getBitmap();
			Point hotspotPoint = new Point(
					(int)(icon.getHotSpotX() + 0.5f),
					(int)(icon.getHotSpotY() + 0.5f));
			mTouchPointers[i] = new TouchPointer(context);
			mTouchPointers[i].setImageBitmap(bitmap);
			addTouchPointer(mTouchPointers[i], hotspotPoint);
		}

		mTouchable = false;
		
		mShowTouchesChecked = PreferenceSettingsManager
				.isDeveloperOptionEnabled(DeveloperOption.SHOW_TOUCHES);
		if (!mShowTouchesChecked) {
			showTouchPointer(true);
		}
	}
	
	public boolean isShowTouches() {
		return (!mShowTouchesChecked && (mShowTouches || mTouchable));
	}

	public void showTouchPointer(boolean customed) {
		if (!customed) {
			// check developer options 'show touches' setting
			mShowTouchesChecked = PreferenceSettingsManager
					.enableDeveloperOption(DeveloperOption.SHOW_TOUCHES);
			
			if (mShowTouchesChecked) {
				DroidPop.debug("show system touches");
				mShowTouches = false; 
			}
		}
		
		// if failed, show custom touches
		if (customed || !mShowTouchesChecked) {
			DroidPop.debug("show custom touches");
			mShowTouches = true;
			mShowTouchesChecked = !PreferenceSettingsManager
					.disableDeveloperOption(DeveloperOption.SHOW_TOUCHES);
		}
	}

	public void hideTouchPointer() {
		mShowTouches = false;
	}
	
	@Override
	public void onScreenTouch(MotionEvent event) {
		synchronized (mTouchable) {
			if (mTouchable) {
				DroidPop.debug("has been touched, not handle again.");
				return;
			}
		}
		
		handleTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized(mTouchable) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				DroidPop.debug("has been touched, nomally not.");
				mTouchable = true;
				break;
			case MotionEvent.ACTION_UP:
				mTouchable = false;
				break;
			}
		}
		
		return handleTouchEvent(event);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		requestMessure();
		if (hasAttachedToWindow()) {
			sWindowManager.updateViewLayout(this, mWindowParams);
		}

		super.onLayout(changed, l, t, r, ScreenMetrics.getResolutionY());
	}

	@Override
	protected void onInitializeWindowLayoutParams() {
		super.onInitializeWindowLayoutParams();

		mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

		mWindowParams.x = 0;
		mWindowParams.y = 0;
		mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;

		requestMessure();
	}

	private boolean handleTouchEvent(final MotionEvent event) {
		if(!isShowTouches()) {
			final String empty = "";
			DroidPop.debug("won't handle, cause",
					(mShowTouchesChecked ? " developer options \'show touches\' checked; " : empty),
					(!mShowTouches ? " show touches not enabled;" : empty),
					(!mTouchable ? " not touchable" : empty));
			return false;
		}
		
		try {
			UiThreadHandler handler = new UiThreadHandler();
			handler.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN:
						PointerCoords pointerCoords = new PointerCoords();
						Point point = new Point();
						final int cnt = Math.min(event.getPointerCount(), MAX_MULTI_TOUCH_POINT_SUPPORTED);
						for (int i = 0; i != cnt; ++i) {
							event.getPointerCoords(i, pointerCoords);
							point.x = (int) (pointerCoords.x + 0.5f);
							point.y = (int) (pointerCoords.y + 0.5f);
							showTouchPointer(i, point);
						}
						break;

					default:
						break;
					}
				}
				
			});

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void addTouchPointer(View pointer, Point hotspotPoint) {
		AbsoluteLayout.LayoutParams alp = new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
				0, 0);

		pointer.setX(-hotspotPoint.x);
		pointer.setY(-hotspotPoint.y);
		pointer.setVisibility(INVISIBLE);
		pointer.bringToFront();
		
		mContentView.addView(pointer, alp);
	}
	
	private void showTouchPointer(int pointerIndex, Point point) {
		TouchPointer pointer = mTouchPointers[pointerIndex];
		AbsoluteLayout.LayoutParams alp = (AbsoluteLayout.LayoutParams) pointer
				.getLayoutParams();
		alp.x = point.x;
		alp.y = point.y;
		mContentView.updateViewLayout(pointer, alp);
		pointer.show();
	}

	private void requestMessure() {
		ScreenMetrics metrics = new ScreenMetrics(this);
		metrics.setPhysicalScreenMode();
		metrics.requestMessure();

		mWindowParams.height = ScreenMetrics.getResolutionY();
	}
	
	private class TouchPointer extends ImageView {

		private final AnimatorSet mBlinkAnimator;

		public TouchPointer(Context context) {
			super(context);

			mBlinkAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(
					context, R.animator.blink);
			mBlinkAnimator.setTarget(this);
			mBlinkAnimator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					setVisibility(VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					// never reached
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					setVisibility(INVISIBLE);
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					setVisibility(INVISIBLE);
				}
			});
		}

		public void show() {
			// TODO: sth. wrong
			AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) getLayoutParams();
			final int x = (int) (params.x + getX());
			final int y = (int) (params.y + getY());
			DroidPop.debug("Rect(", x, ", ", y, " - ",
					x + getMeasuredWidth(), ", ",
					y + getMeasuredHeight(), ")");
			
			if(mBlinkAnimator.isRunning()) {
				mBlinkAnimator.cancel();
			}
			
			mBlinkAnimator.start();
		}

	}
	
}
