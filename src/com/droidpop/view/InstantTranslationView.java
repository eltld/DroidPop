package com.droidpop.view;

import me.wtao.view.FloatingView;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.WindowManager;

public class InstantTranslationView extends FloatingView {

	public InstantTranslationView(Context context) {
		super(context);
	}

	public InstantTranslationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InstantTranslationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 
	 * @param anchor
	 *            the popup's anchor point
	 */
	public void popUp(Point anchor) {
		mWindowParams.x = anchor.x;
		mWindowParams.y = anchor.y;
		sWindowManager.updateViewLayout(this, mWindowParams);

		show();
	}

	@Override
	protected void onInitializeWindowLayoutParams() {
		super.onInitializeWindowLayoutParams();

		mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
	}

}
