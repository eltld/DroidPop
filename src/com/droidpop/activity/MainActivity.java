package com.droidpop.activity;

import me.wtao.utils.DebugStatusBar;
import me.wtao.utils.Logcat;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.view.OnScreenTouchListener;
import com.droidpop.view.ScreenCoordsManager;

public class MainActivity extends Activity {

	private TextView mTestView;
	private ScreenCoordsManager mScreenCoordsManager;
	private OnScreenTouchListener mTestListener = new OnScreenTouchListener() {
		
		@Override
		public void onScreenTouch(MotionEvent event) {
			DroidPop.debug(Logcat.shortFor(event, "action", "x", "y"));
			DroidPop.debug(Thread.currentThread());
			mTestView.setText(Logcat.shortFor(event, "action", "x", "y"));
//			mStatusBar.updateDebugStatus(Logcat.shortFor(event, "action", "x", "y"));
		}
	};
	private DebugStatusBar mStatusBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTestView = (TextView)findViewById(R.id.test);
		
		DroidPop.initFromLauncherActivity(this);
		DroidPop app = DroidPop.getApplication();
		mScreenCoordsManager = (ScreenCoordsManager) app
				.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
		mScreenCoordsManager.setOnScreenTouchListener(mTestListener);
		
		mStatusBar = new DebugStatusBar(getApplicationContext());
		mStatusBar.setHightlightOff();
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
