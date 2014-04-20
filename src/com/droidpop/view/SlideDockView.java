package com.droidpop.view;

import me.wtao.utils.Logcat;
import me.wtao.utils.ScreenMetrics;
import me.wtao.view.FloatingView;
import me.wtao.view.Hotspot;
import me.wtao.view.Hotspot.OnHotspotListener;
import me.wtao.widget.SlidingDrawer;
import me.wtao.widget.SlidingDrawer.OnDrawerCloseListener;
import me.wtao.widget.SlidingDrawer.OnDrawerOpenListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.droidpop.R;

public class SlideDockView extends FloatingView {
	
	private static final Logcat sLogcat = new Logcat();

	private final RelativeLayout mContainer;
	private final SlidingDrawer mSlidingDrawer;
	private final Hotspot mHotSide;
	
	public SlideDockView(Context context) {
		super(context);

		LayoutInflater inflater = LayoutInflater.from(context);
		mContainer = (RelativeLayout)inflater.inflate(R.layout.layout_slide_dock_view, this);
		mSlidingDrawer = (SlidingDrawer) mContainer.findViewById(R.id.slide_dock_view);
		
		mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				setTouchable(true);
			}
		});
		mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				setTouchable(false);
			}
		});
		
		
		mHotSide = new Hotspot(context);
		mHotSide.setGravity(Hotspot.EDGE_RIGHT);
		mHotSide.setPhysicalScreenMode();
		mHotSide.setOnHotspotListener(new OnHotspotListener() {
			
			@Override
			public int getHotspotGravity() {
				return Hotspot.EDGE_RIGHT;
			}
			
			@Override
			public boolean dispatchTouchEvent(MotionEvent event) {
				sLogcat.d(Logcat.shortFor(event));
				return mSlidingDrawer.dispatchTouchEvent(event);
			}
		});
		
	}
	
	public void setEnable() {
		mHotSide.show();
		show();
	}
	
	public void setDisable() {
		hide();
		mHotSide.hide();
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
	
	private void requestMessure() {
		ScreenMetrics metrics = new ScreenMetrics(this);
		metrics.setPhysicalScreenMode();
		metrics.requestMessure();

		mWindowParams.height = ScreenMetrics.getResolutionY();
		

		mHotSide.setHotspotWidth(ScreenMetrics.getMeasuredStatusBarHeight() / 2);
		mHotSide.setHotspotHeight(ScreenMetrics.getResolutionY());
	}
	
	private void setTouchable(boolean enable) {
		if(enable) {
			mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		} else {
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		}
		
		sWindowManager.updateViewLayout(this, mWindowParams);
	}

}
