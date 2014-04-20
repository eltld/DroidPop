package com.droidpop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.dict.offline.OfflineTranslator;
import com.droidpop.test.OnLongPressTranslationListenerTestCase;
import com.droidpop.test.TestCase;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DroidPop.initFromLauncherActivity(this);
		DroidPop.getApplication().createShortcut(false);
		
		setContentView(R.layout.activity_main);
		
//		final String packageName = "me.wtao.service";
//		Context context = this;
//		PackageManager pkgMgr = context.getPackageManager();
//		try {
//			ApplicationInfo info = pkgMgr.getApplicationInfo(packageName,
//					PackageManager.GET_META_DATA);
//
//			SystemAppMover sysmover = new SystemAppMover(this);
//			if (!sysmover.isSystemApp(info)) {
//				sysmover.convertApplication(info);
//			}
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
		
//		ImageView imageView = (ImageView) findViewById(R.id.screencap);
//		TestCase test1 = new OcrTestCase(this, imageView);
//		test1.setUp();
		
//		WordCapLockView test2 = new WordCapLockView(getApplicationContext());
//		test2.attachedToWindow();
//		test2.setEnable();

		TestCase test3 = new OnLongPressTranslationListenerTestCase(this);
		test3.setUp();
		
		OfflineTranslator translator = new OfflineTranslator(null);
		translator.translte("best");
		
//		
//		ScreenCoordsManager mgr = (ScreenCoordsManager) DroidPop
//				.getApplication().getAppService(DroidPop.SCREEN_COORDS_SERVICE);
//		mgr.enableShowTouches();
		
//		SlideDockView v = new SlideDockView(getApplicationContext());
//		v.attachedToWindow();
//		v.setEnable();
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
