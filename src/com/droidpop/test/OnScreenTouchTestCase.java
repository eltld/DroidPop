package com.droidpop.test;

import me.wtao.os.UiThreadHandler;
import me.wtao.utils.DebugStatusBar;
import me.wtao.utils.Logcat;
import android.content.Context;
import android.view.MotionEvent;

import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCoordsManager;
import com.droidpop.view.OnScreenTouchListener;

public class OnScreenTouchTestCase implements OnScreenTouchListener {

	private final UiThreadHandler mUiHandler = new UiThreadHandler();
	private MotionEvent mPrevEvent = null;
	
	private Context mContext;
	private DebugStatusBar mStatusBar;
	
	public OnScreenTouchTestCase(Context context) {
		mContext = context.getApplicationContext();
		
		mStatusBar = new DebugStatusBar(mContext);
		mStatusBar.setHightlightOff(); // TODO: disable just for now
		mStatusBar.attachedToWindow();
		mStatusBar.show();
	}
	
	public void setUp() {
		DroidPop app = DroidPop.getApplication();
		ScreenCoordsManager mgr = (ScreenCoordsManager) app
				.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
		mgr.setOnScreenTouchListener(this);
	}

	public void tearDown() {
		DroidPop app = DroidPop.getApplication();
		app.stopService(DroidPop.SCREEN_COORDS_SERVICE);
	}
	
	@Override
	public void onScreenTouch(final MotionEvent event) {
		DroidPop.debug(Logcat.shortFor(event, "action", "x", "y"));
		
		if(mPrevEvent != null) {
			mPrevEvent.recycle();
		}
		mPrevEvent = MotionEvent.obtain(event);
		
		mUiHandler.runOnUiThread(new Runnable() {
//			private static final int ACTION = 1<<0;
//			private static final int AXIS_X = 1<<1;
//			private static final int AXIS_Y = 1<<2;
			
			@Override
			public void run() {
				mStatusBar.updateDebugStatus(
						Logcat.shortFor(event, "action", "x", "y"));
				
//				mStatusBar.updateDebugStatus(
//						getEventInfo(event, mPrevEvent, ACTION),
//						getEventInfo(event, mPrevEvent, AXIS_X),
//						getEventInfo(event, mPrevEvent, AXIS_Y));
			}
			
			// TODO: high light the debug info.
//			private String getEventInfo(MotionEvent ev, MotionEvent prev, final int key) {
//				String keyVal;
//				switch (key) {
//				case ACTION:
//					keyVal = "action";
//					break;
//				case AXIS_X:
//					keyVal = "x";
//					break;
//				case AXIS_Y:
//					keyVal = "y";
//					break;
//				default:
//					return null;
//				}
//				
//				String value;
//				if(ev.getAction() != prev.getAction()) {
//					value = DebugStatusBar.I + Logcat.shortFor(event, keyVal);
//				} else {
//					value = Logcat.shortFor(event, keyVal);
//				}
//				return value;
//			}
		});
	}
	
}
