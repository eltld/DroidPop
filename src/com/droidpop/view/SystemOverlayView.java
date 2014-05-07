package com.droidpop.view;

import me.wtao.os.UiThreadHandler;
import me.wtao.utils.Log;
import me.wtao.view.FloatingView;
import me.wtao.view.PointerFactory;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.PointerIcon;
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
	private static SystemOverlayView sInstance;
	
	private final ViewGroup mContentView;
	
	private final GestureDetector mDetector;
	
	private final TouchPointer[] mTouchPointers;
	private Boolean mTouchable;
	private boolean mShowTouches;
	private boolean mShowTouchesChecked;
	
	public static SystemOverlayView getInstance(Context context) {
		if(sInstance == null) {
			sInstance = new SystemOverlayView(context.getApplicationContext());
		}
		
		return sInstance;
	}

	private SystemOverlayView(Context context) {
		super(context);
		
		mScreenMetrics.setPhysicalScreenMode();
		
		mContentView = new AbsoluteLayout(context); // deprecated but best layout here
		FloatingView.LayoutParams flp = new FloatingView.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		addView(mContentView, flp);
		
		mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			
			private int mTaps = 0;
			
			@Override
			public boolean onSingleTapUp(MotionEvent event) {
				++mTaps;
				
				if (mTaps == 1) {
					DroidPop.debug("trigger touchable");
					enableTouchable(); // bug when long press, we can fix it but not now
				} else if (mTaps == 2) {
					DroidPop.debug("double tap, and trigger not touchable");
					mTaps = 0; // double tap confirmed
				}
				
				return false; // other events should be preceded by
			}
			
			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {
				DroidPop.debug("trigger not touchable");
				disableTouchable();
				mTaps = 0; // single tap confirmed
				
				return true; // not handle other events
			}
			
			@Override
			public boolean onDoubleTap(MotionEvent event) {
				DroidPop.debug("trigger OCR & translate action");
				disableTouchable();
				
				return handleTouchEvent(event);
			}
		});
		
//		mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//
//			@Override
//			public boolean onSingleTapConfirmed(MotionEvent event) {
//				return handleTouchEvent(event);
//			}
//
//			@Override
//			public boolean onDoubleTap(MotionEvent event) {
//				return handleTouchEvent(event);
//			}
//			
//		});
		
		PointerFactory factory = new PointerFactory(context);
		mTouchPointers = new TouchPointer[MAX_MULTI_TOUCH_POINT_SUPPORTED];
		for (int i = 0; i != MAX_MULTI_TOUCH_POINT_SUPPORTED; ++i) {
			PointerIcon icon = factory.createDefaultPointer();
			Bitmap bitmap = icon.getBitmap();
			PointF hotspotPoint = new PointF(
					icon.getHotSpotX(),
					icon.getHotSpotY());
			mTouchPointers[i] = new TouchPointer(context, hotspotPoint);
			mTouchPointers[i].setImageBitmap(bitmap);
			addTouchPointer(mTouchPointers[i]);
		}

		mTouchable = false;
		
		mShowTouchesChecked = PreferenceSettingsManager
				.isDeveloperOptionEnabled(DeveloperOption.SHOW_TOUCHES);
		if (!mShowTouchesChecked) {
			showTouchPointer(true);
		}
	}
	
	public void enableTouchable() {
		if(!hasAttachedToWindow()) {
			attachedToWindow();
		}
		
		synchronized (mTouchable) {
			if(mTouchable) {
				return;
			}
			
			mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
			sWindowManager.updateViewLayout(this, mWindowParams);
			mTouchable = true;
		}
	}

	public void disableTouchable() {
		synchronized (mTouchable) {
//			if(!mTouchable) {
//				return;
//			}
			
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
			sWindowManager.updateViewLayout(this, mWindowParams);
			mTouchable = false;
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
	public void onScreenTouch(final MotionEvent event) {
		synchronized (mTouchable) {
			if (mTouchable) {
				DroidPop.debug("has been touched, not handle again.");
				return;
			}
		}
		
		UiThreadHandler handler = new UiThreadHandler();
		handler.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				DroidPop.log(DroidPop.LEVEL_VERBOSE, Log.shortFor(event));
				mDetector.onTouchEvent(event);
			}

		});

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
		
		DroidPop.log(DroidPop.LEVEL_VERBOSE, Log.shortFor(event));
		return mDetector.onTouchEvent(event);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		requestMessure();
		if (hasAttachedToWindow()) {
			sWindowManager.updateViewLayout(this, mWindowParams);
		}

		super.onLayout(changed, l, t, r, mScreenMetrics.getResolutionY());
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
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				PointerCoords pointerCoords = new PointerCoords();
				Point point = new Point();
				final int cnt = Math.min(event.getPointerCount(),
						MAX_MULTI_TOUCH_POINT_SUPPORTED);
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
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void addTouchPointer(TouchPointer pointer) {
		AbsoluteLayout.LayoutParams alp = new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
				0, 0);
		
		pointer.setX(-pointer.getHotSpotX());
		pointer.setY(-pointer.getHotSpotY());
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
		mScreenMetrics.messure();
		mWindowParams.height = mScreenMetrics.getResolutionY();
	}
	
	private class TouchPointer extends ImageView {

		private final PointF mHotspotPoint;
		private final AnimatorSet mBlinkAnimator;
		
		public TouchPointer(Context context) {
			this(context, null);
		}

		public TouchPointer(Context context, PointF hotspotPoint) {
			super(context);
			
			mHotspotPoint = hotspotPoint;

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
		
		public float getHotSpotX() {
			if(mHotspotPoint != null) {
				return mHotspotPoint.x;
			} else {
				return getMeasuredWidth() / 2.f;
			}
		}
		
		public float getHotSpotY() {
			if(mHotspotPoint != null) {
				return mHotspotPoint.y;
			} else {
				return getMeasuredHeight() / 2.f;
			}
		}

		public void show() {
			AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) getLayoutParams();
			final float x = params.x - getHotSpotX();
			final float y = params.y - getHotSpotY();
			DroidPop.log(DroidPop.LEVEL_VERBOSE, "Rect(", x, ", ", y, " - ",
					x + getMeasuredWidth(), ", ",
					y + getMeasuredHeight(), ")");
			
			if(mBlinkAnimator.isRunning()) {
				mBlinkAnimator.cancel();
			}
			
			mBlinkAnimator.start();
		}

	}
	
}
