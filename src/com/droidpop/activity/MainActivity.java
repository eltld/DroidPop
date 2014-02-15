package com.droidpop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCapManager;
import com.droidpop.view.WordCapLockView;

public class MainActivity extends Activity {

	private ScreenCapManager mScreenCapManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DroidPop.initFromLauncherActivity(this);
//		DroidPop app = DroidPop.getApplication();
//		mScreenCapManager = (ScreenCapManager) app
//				.getAppService(DroidPop.SCREEN_CAPTURE_SERVICE);
//		mScreenCapManager.takeScreenCapture();
				
		WordCapLockView test2 = new WordCapLockView(getApplicationContext());
		test2.attachedToWindow();
		test2.setEnable();
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
