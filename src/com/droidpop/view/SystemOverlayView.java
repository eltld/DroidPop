package com.droidpop.view;

import me.wtao.utils.ScreenMetrics;
import me.wtao.view.FloatingView;
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
	private boolean mShowTouches;

	public SystemOverlayView(Context context) {
		super(context);
		
		mContentView = new AbsoluteLayout(context); // deprecated but best layout here
		FloatingView.LayoutParams flp = new FloatingView.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		addView(mContentView, flp);
		
		mTouchPointers = new ImageView[MAX_MULTI_TOUCH_POINT_SUPPORTED];
		for (int i = 0; i != MAX_MULTI_TOUCH_POINT_SUPPORTED; ++i) {
			PointerIcon icon = PointerIcon.getDefaultIcon(context);
			Bitmap bitmap = icon.getBitmap();
			Point hotspotPoint = new Point(
					(int)(icon.getHotSpotX() + 0.5f),
					(int)(icon.getHotSpotY() + 0.5f));
			mTouchPointers[i] = new ImageView(context);
			mTouchPointers[i].setImageBitmap(bitmap);
			addTouchPointer(mTouchPointers[i], hotspotPoint);
		}

		if (isDevOptionShowTouchesChecked()) {
			showTouchPointer(true);
		}
	}

	public void showTouchPointer(boolean customed) {
		if (customed) {
			DroidPop app = DroidPop.getApplication();
			ScreenCoordsManager mgr = (ScreenCoordsManager) app
					.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
			mgr.addOnScreenTouchListener(this);

			mShowTouches = true;
		} else {
			// TODO: check developer options 'show touches' setting
			
			mShowTouches = false; // show system default touches
		}
	}

	public void hideTouchPointer() {
		mShowTouches = false;
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
		handleTouchEvent(event, false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return handleTouchEvent(event, true);
	}

	private boolean handleTouchEvent(MotionEvent event, boolean touched) {
		if(!mShowTouches) {
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
		AbsoluteLayout.LayoutParams alp = new AbsoluteLayout.LayoutParams(0, 0,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
