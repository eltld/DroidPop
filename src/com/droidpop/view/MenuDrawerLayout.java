package com.droidpop.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MenuDrawerLayout extends DrawerLayout {
	
	public static interface OnInterceptTouchEventListener {
		public boolean shouldInterceptTouchEvent(MotionEvent event);
	}
	
	private OnInterceptTouchEventListener mListener;
	private boolean mIntercepted;

	public MenuDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MenuDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MenuDrawerLayout(Context context) {
		super(context);
	}
	
	public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener listener) {
		mListener = listener;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mIntercepted = false;
			if(mListener != null && mListener.shouldInterceptTouchEvent(event)) {
				mIntercepted = true;
			}
			break;
		}
		
		if(mIntercepted) {
			return false;
		} else {
			return super.onInterceptTouchEvent(event);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mIntercepted) {
			return true;
		}
		
		return super.onTouchEvent(event);
	}

}
