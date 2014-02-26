package com.droidpop.activity;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCapManager;
import com.droidpop.app.ScreenCapManager.ScreenCapTaskDispatcher;
import com.droidpop.dict.EntryParseException;
import com.droidpop.dict.EntryParser;
import com.droidpop.dict.WordEntryReader;
import com.droidpop.dict.TranslationTask;
import com.droidpop.dict.TranslationTask.Status;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.TranslationTask.OnTranslateListener;
import com.droidpop.dict.youdao.YouDaoJsonParser;
import com.droidpop.dict.youdao.YouDaoTranslator;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DroidPop.initFromLauncherActivity(this);
		
		DroidPop app = DroidPop.getApplication();
		ScreenCapManager mgr = (ScreenCapManager) app
				.getAppService(DroidPop.SCREEN_CAPTURE_SERVICE);
		mgr.dispatch(new ScreenCapTaskDispatcher() {
			
			@Override
			public void onDone(ArrayList<Bitmap> resluts) {
				DroidPop.debug("pass");
			}
			
			@Override
			public void onCancelled(String msg) {
				
			}
			
			@Override
			public Rect[] setBounds() {
				return null;
			}
		});
				
//		WordCapLockView test2 = new WordCapLockView(getApplicationContext());
//		test2.attachedToWindow();
//		test2.setEnable();
		
		final TextView tv = (TextView) findViewById(R.id.test);
		TranslationTask test3 = new TranslationTask(new YouDaoTranslator(), new OnTranslateListener() {
			@Override
			public void onTranslated(WordEntry entry, Status state) {
				if(state == TranslationTask.Status.CANCELLED) {
					return;
				}
				
				if(entry == null || !entry.isValid()) {
					return;
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append("word=").append(entry.getWord());
				sb.append("basic=").append(
						entry.getBasicParaphrase().getDetail());

				DroidPop.debug(sb.toString());
				tv.setText(sb.toString());
			}
		});
		test3.translate("hello");
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
