package com.droidpop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.droidpop.R;
import com.droidpop.app.DroidPop;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DroidPop.initFromLauncherActivity(this);
		
//		DroidPop app = DroidPop.getApplication();
		
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
		
//		TestCase test3 = new OnLongPressTranslationListenerTestCase(this);
//		test3.setUp();
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
