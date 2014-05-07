package com.droidpop.view;

import me.wtao.view.FloatingView;
import me.wtao.view.Hotspot;
import me.wtao.view.Hotspot.OnHotspotListener;
import me.wtao.widget.GlowPadView;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.droidpop.R;

public class WordCapLockView extends FloatingView {

	private final RelativeLayout mContainer;
	private final GlowPadView mGlowPadView;
	private final Hotspot mHotBottom;
	
	public WordCapLockView(Context context) {
		super(context);
		
		mScreenMetrics.setPhysicalScreenMode();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		mContainer = (RelativeLayout)inflater.inflate(R.layout.layout_glow_pad_view, this);
		mGlowPadView = (GlowPadView) mContainer.findViewById(R.id.glow_pad_view);
		
		mHotBottom = new Hotspot(context);
		mHotBottom.setGravity(Hotspot.EDGE_BOTTOM);
		mHotBottom.setPhysicalScreenMode();
		mHotBottom.setHotspotWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mHotBottom.setHotspotHeight(mScreenMetrics.getStatusBarHeight() / 2);
		
		mHotBottom.setOnHotspotListener(new OnHotspotListener() {
			
			@Override
			public int getHotspotGravity() {
				return Hotspot.EDGE_BOTTOM;
			}
			
			@Override
			public boolean dispatchTouchEvent(MotionEvent event) {
				return mGlowPadView.dispatchTouchEvent(event);
			}
		});
		
		mHotBottom.setBackgroundColor(Color.RED); // TODO: for testing
	}
	
	public void setEnable() {
		mHotBottom.show();
		show();
	}
	
	public void setDisable() {
		hide();
		mHotBottom.hide();
	}
	
	@Override
	public void attachedToWindow() {
		super.attachedToWindow();
		mHotBottom.attachedToWindow();
	}
	
	@Override
	public void dismiss() {
		mHotBottom.dismiss();
		super.dismiss();
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
	
	private void requestMessure() {
		mScreenMetrics.messure();
		mWindowParams.height = mScreenMetrics.getResolutionY();
	}

}
