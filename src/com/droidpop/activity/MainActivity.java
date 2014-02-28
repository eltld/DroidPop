package com.droidpop.activity;

import me.wtao.utils.Logcat;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.droidpop.R;
import com.droidpop.app.ClipTranslationManager;
import com.droidpop.app.ClipTranslationManager.OnLongPressTranslationListener;
import com.droidpop.app.DroidPop;
import com.droidpop.dict.TranslationTask.Status;
import com.droidpop.dict.WordEntry;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DroidPop.initFromLauncherActivity(this);
		
		DroidPop app = DroidPop.getApplication();
		
//		ScreenCapManager test1 = (ScreenCapManager) app
//				.getAppService(DroidPop.SCREEN_CAPTURE_SERVICE);
//		test1.dispatch(new ScreenCapTaskDispatcher() {
//			
//			@Override
//			public void onDone(ArrayList<Bitmap> resluts) {
//				DroidPop.debug("pass");
//			}
//			
//			@Override
//			public void onCancelled(String msg) {
//				
//			}
//			
//			@Override
//			public Rect[] setBounds() {
//				return null;
//			}
//		});
				
//		WordCapLockView test2 = new WordCapLockView(getApplicationContext());
//		test2.attachedToWindow();
//		test2.setEnable();

		// TODO: v.s. test3
		mDetector = new GestureDetector(this, listener);
		
		ClipTranslationManager test3 = (ClipTranslationManager) app
				.getAppService(DroidPop.CLIP_TRANSLATION_SERVICE);
		test3.addOnClipTranslationListener(new Test3(this));
	}
	
	@Override
	protected void onDestroy() {
		DroidPop app = DroidPop.getApplication();
		app.stopService();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
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
	
	/**
	 * compare with OnLongPressTranslationListener$OnScreenLongPressListener,
	 * which cannot detect the onShow event, strange :(
	 */
	private GestureDetector mDetector;
	
	public boolean onTouchEvent(MotionEvent event) {
		DroidPop.debug(Logcat.shortFor(event, "action", "x", "y"));
		return mDetector.onTouchEvent(event);
	};
	
	private class Test3 extends OnLongPressTranslationListener {

		public Test3(Context context) {
			super(context);
		}

		@Override
		public void onTranslated(WordEntry entry, Status state) {
			System.out.println(entry.toString());
		}

		@Override
		public void onClipped(PointerCoords coords) {
			System.out.println(((coords == null) ? "null" : coords.toString()));
		}
		
	}

}
