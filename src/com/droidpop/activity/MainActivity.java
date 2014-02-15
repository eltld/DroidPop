package com.droidpop.activity;

import me.wtao.os.UiThreadHandler;
import me.wtao.utils.DebugStatusBar;
import me.wtao.utils.Logcat;
import me.wtao.utils.ScreenMetrics;
import me.wtao.view.Hotspot;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCapManager;
import com.droidpop.app.ScreenCoordsManager;
import com.droidpop.view.OnScreenTouchListener;
import com.droidpop.view.WordCapLockView;

public class MainActivity extends Activity {

	private ScreenCoordsManager mScreenCoordsManager;
	private ScreenCapManager mScreenCapManager;
	
	private final UiThreadHandler mUiHandler = new UiThreadHandler();

	private DebugStatusBar mStatusBar;
	private OnScreenTouchListener mTestListener = new OnScreenTouchListener() {
		private MotionEvent mPrevEvent = null;
		
		@Override
		public void onScreenTouch(final MotionEvent event) {
			DroidPop.debug(Logcat.shortFor(event, "action", "x", "y"));
			
			if(mPrevEvent != null) {
				mPrevEvent.recycle();
			}
			mPrevEvent = MotionEvent.obtain(event);
			
			mUiHandler.runOnUiThread(new Runnable() {
//				private static final int ACTION = 1<<0;
//				private static final int AXIS_X = 1<<1;
//				private static final int AXIS_Y = 1<<2;
				
				@Override
				public void run() {
					mStatusBar.updateDebugStatus(
							Logcat.shortFor(event, "action", "x", "y"));
					
//					mStatusBar.updateDebugStatus(
//							getEventInfo(event, mPrevEvent, ACTION),
//							getEventInfo(event, mPrevEvent, AXIS_X),
//							getEventInfo(event, mPrevEvent, AXIS_Y));
				}
				
				// TODO: high light the debug info.
//				private String getEventInfo(MotionEvent ev, MotionEvent prev, final int key) {
//					String keyVal;
//					switch (key) {
//					case ACTION:
//						keyVal = "action";
//						break;
//					case AXIS_X:
//						keyVal = "x";
//						break;
//					case AXIS_Y:
//						keyVal = "y";
//						break;
//					default:
//						return null;
//					}
//					
//					String value;
//					if(ev.getAction() != prev.getAction()) {
//						value = DebugStatusBar.I + Logcat.shortFor(event, keyVal);
//					} else {
//						value = Logcat.shortFor(event, keyVal);
//					}
//					return value;
//				}
			});
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DroidPop.initFromLauncherActivity(this);
//		DroidPop app = DroidPop.getApplication();
		
//		mScreenCoordsManager = (ScreenCoordsManager) app
//				.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
//		mScreenCoordsManager.setOnScreenTouchListener(mTestListener);
		
//		mScreenCapManager = (ScreenCapManager) app
//				.getAppService(DroidPop.SCREEN_CAPTURE_SERVICE);
//		mScreenCapManager.takeScreenCapture();
		
		WordCapLockView test = new WordCapLockView(getApplicationContext());
		test.attachedToWindow();
		test.setEnable();
		
		mStatusBar = new DebugStatusBar(getApplicationContext());
		mStatusBar.setHightlightOff(); // TODO: disable just for now
		mStatusBar.attachedToWindow();
		mStatusBar.show();		
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

}
