package com.droidpop.view;

import me.wtao.utils.ScreenMetrics;
import me.wtao.view.FloatingView;
import me.wtao.view.PointerFactory;
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

import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCoordsManager;

public class SystemOverlayView extends FloatingView implements
		OnScreenTouchListener {

	private static final int MAX_MULTI_TOUCH_POINT_SUPPORTED = 10;
	
	private final ViewGroup mContentView;
	private final ImageView[] mTouchPointers;
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
		
		PointerFactory factory = new PointerFactory(context);
		mTouchPointers = new ImageView[MAX_MULTI_TOUCH_POINT_SUPPORTED];
		for (int i = 0; i != MAX_MULTI_TOUCH_POINT_SUPPORTED; ++i) {
			PointerIcon icon = factory.createDefaultPointer();
			Bitmap bitmap = icon.getBitmap();
			Point hotspotPoint = new Point(
					(int)(icon.getHotSpotX() + 0.5f),
					(int)(icon.getHotSpotY() + 0.5f));
			mTouchPointers[i] = new ImageView(context);
			mTouchPointers[i].setImageBitmap(bitmap);
			addTouchPointer(mTouchPointers[i], hotspotPoint);
		}

		mTouchable = false;
		
		if (!isDevOptionShowTouchesChecked()) {
			showTouchPointer(true);
		}
	}

	public void showTouchPointer(boolean customed) {
		if (!customed) {
			// TODO: check developer options 'show touches' setting

			mShowTouchesChecked = false;
			if (mShowTouchesChecked) {
				DroidPop.debug("show system touches");
				mShowTouches = false; 
			}
		}
		
		// if failed, show custom touches
		if (customed || !mShowTouchesChecked) {
			DroidPop.debug("show custom touches");
			
			DroidPop app = DroidPop.getApplication();
			ScreenCoordsManager mgr = (ScreenCoordsManager) app
					.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
			mgr.addOnScreenTouchListener(this);

			mShowTouches = true;
		}
	}

	public void hideTouchPointer() {
		mShowTouches = false;
		// TODO: remove from ScreenCoordsManager
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

	private boolean handleTouchEvent(MotionEvent event) {
		if(mShowTouchesChecked
				|| !(mShowTouches || mTouchable)) {
			final String empty = "";
			DroidPop.debug("won't handle, cause",
					(mShowTouchesChecked ? " developer options \'show touches\' checked; " : empty),
					(!mShowTouches ? " show touches not enabled;" : empty),
					(!mTouchable ? " not touchable" : empty));
			return false;
		}
		
		PointerCoords pointerCoords = new PointerCoords();
		Point point = new Point();
		for(int i = 0; i != event.getPointerCount(); ++i) {
			event.getPointerCoords(i, pointerCoords);
			point.x = (int) (pointerCoords.x + 0.5f);
			point.y = (int) (pointerCoords.y + 0.5f);
			showTouchPointer(i, point);
		}
		
		return true;
	}
	
	private void addTouchPointer(View pointer, Point hotspotPoint) {
		AbsoluteLayout.LayoutParams alp = new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
				0, 0);

		pointer.bringToFront();
		pointer.setVisibility(VISIBLE);
		
		mContentView.addView(pointer, alp);
	}
	
	private void showTouchPointer(int pointerIndex, Point point) {
		View pointer = mTouchPointers[pointerIndex];
		AbsoluteLayout.LayoutParams alp = (AbsoluteLayout.LayoutParams) pointer
				.getLayoutParams();
		alp.x = point.x;
		alp.y = point.y;
		mContentView.updateViewLayout(pointer, alp);
	}

	private boolean isDevOptionShowTouchesChecked() {
		return false;
	}

	private void requestMessure() {
		ScreenMetrics metrics = new ScreenMetrics(this);
		metrics.setPhysicalScreenMode();
		metrics.requestMessure();

		mWindowParams.height = ScreenMetrics.getResolutionY();
	}
	
}
