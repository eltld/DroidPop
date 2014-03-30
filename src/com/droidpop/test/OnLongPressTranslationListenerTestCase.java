package com.droidpop.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.View.OnTouchListener;

import com.droidpop.app.ClipTranslationManager;
import com.droidpop.app.ClipTranslationManager.OnLongPressTranslationListener;
import com.droidpop.app.DroidPop;
import com.droidpop.dict.TranslationTask.Status;
import com.droidpop.dict.WordEntry;
import com.droidpop.view.LightTranslationView;

public class OnLongPressTranslationListenerTestCase implements TestCase, OnTouchListener {
	
	private Context mContext;
	SimpleOnLongPressTranslationListener mTargetListener;
	
	/**
	 * compare with OnLongPressTranslationListener$OnScreenLongPressListener,
	 * which cannot detect the onShow event, strange :(<br>
	 * <br>
	 * <b>problem solved but why? it look likes depending on uptimeMillis()</b>
	 */
	private GestureDetector mDetector;
	private GestureDetector.OnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {
		private PointerCoords mCoords = new PointerCoords();
		
		@Override
		public boolean onDown(MotionEvent event) {
			synchronized (mCoords) {
				DroidPop.log(DroidPop.LEVEL_WARN, "touch down...");
			}
			return true;
		};

		@Override
		public void onLongPress(MotionEvent event) {
			onShow(event);
		};
		
		private synchronized void onShow(MotionEvent event) {
			synchronized (mCoords) {
				event.getPointerCoords(0, mCoords);
				DroidPop.log(DroidPop.LEVEL_WARN, "on show...",
						"x[0]=", mCoords.x,
						", y[0]=", mCoords.y);
			}
		}
	};
	
	public OnLongPressTranslationListenerTestCase(Activity activity) {
		this((Context)activity, activity.findViewById(android.R.id.content));
	}
	
	public OnLongPressTranslationListenerTestCase(Context context, View v) {
		mContext = context;
		mTargetListener = new SimpleOnLongPressTranslationListener(mContext);
		mDetector = new GestureDetector(mContext, mListener);
		
		v.setOnTouchListener(this);
	}
	
	@Override
	public void setUp() {
		DroidPop app = DroidPop.getApplication();
		ClipTranslationManager mgr = (ClipTranslationManager) app
				.getAppService(DroidPop.CLIP_TRANSLATION_SERVICE);
		mgr.addOnClipTranslationListener(mTargetListener);
	}
	
	@Override
	public void tearDown() {
		DroidPop app = DroidPop.getApplication();
		ClipTranslationManager mgr = (ClipTranslationManager) app
				.getAppService(DroidPop.CLIP_TRANSLATION_SERVICE);
		mgr.removeOnClipTranslationListener(mTargetListener);
		app.stopService(DroidPop.CLIP_TRANSLATION_SERVICE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		DroidPop.debug(Logcat.shortFor(event, "action", "x", "y"));
		return mDetector.onTouchEvent(event);
	}
	
	private class SimpleOnLongPressTranslationListener extends OnLongPressTranslationListener {

		private final LightTranslationView mToast;
		
		public SimpleOnLongPressTranslationListener(Context context) {
			super(context);
			
			mToast = new LightTranslationView(context);
		}

		@Override
		public void onTranslated(WordEntry entry, Status state) {
			System.out.println(entry.toString());
			
			mToast.setWordEntryQuery(entry.getWord());
			mToast.setTranslation(entry);
		}

		@Override
		public void onClipped(PointerCoords coords) {
			DroidPop.debug(((coords == null) ? "null" : coords.toString()));
			
			Point point = new Point();
			point.x = ((coords == null) ? 0 : (int) coords.x);
			point.y = ((coords == null) ? 0 : (int) coords.y);
			
			DroidPop.debug(point);
			
//			mToast.popUp(point);
			mToast.show();
		}
		
	}
	
}
