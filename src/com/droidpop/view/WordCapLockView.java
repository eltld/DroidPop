package com.droidpop.view;

import me.wtao.utils.ScreenMetrics;
import me.wtao.view.FloatingView;
import me.wtao.view.Hotspot;
import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;

public class WordCapLockView extends FloatingView {

	private Hotspot mHotBottom;
	
	public WordCapLockView(Context context) {
		super(context);
		
		mHotBottom = new Hotspot(context);
		mHotBottom.setGravity(Hotspot.EDGE_BOTTOM);
		mHotBottom.setPhysicalScreenMode();
		mHotBottom.setHotspotWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mHotBottom.setHotspotHeight(ScreenMetrics.getMeasuredStatusBarHeight() / 2);
		
		mHotBottom.setBackgroundColor(Color.RED); // TODO: for testing
	}
	
	public void setEnable() {
		mHotBottom.show();
	}
	
	public void setDisable() {
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

}
