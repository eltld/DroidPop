package com.droidpop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCoordsManager;
import com.droidpop.model.MenuListAdapter;
import com.droidpop.model.MenuListAdapter.MenuItemHolder;
import com.droidpop.ocr.OcrHandler;
import com.droidpop.ocr.OnOcrRecognitionListener;
import com.droidpop.test.OnLongPressTranslationListenerTestCase;
import com.droidpop.test.TestCase;
import com.droidpop.view.SystemOverlayView;

public class ControlCenter implements QuickSettingController {
	
	private static ControlCenter sInstance;
	
	private final Context mContext;
	private final ScreenCoordsManager mScreenCoordsManager;
	
	private MenuListAdapter mMenuListAdapter;
	
	private OcrHandler mOcrHandler;
	private SystemOverlayView mLockScreen;
	
	public static synchronized ControlCenter getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ControlCenter(context.getApplicationContext());
		}
		return sInstance;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	@Override
	public MenuListAdapter getMenuListAdapter() {
		if(mMenuListAdapter == null) {
			mMenuListAdapter = setupMenuListAdapter();
		}
		return mMenuListAdapter;
	}
	
	@Override
	public Status onOptionsItemSelected(int actionId, Status status) {
		boolean turnOn = (status != Status.ON);
		
		switch (actionId) {
		case ACTION_CLIP_TRANSLATE:
			if (turnOn) {
				enableClipTranslate();
			} else {
				disableClipTranslate();
			}
			break;
		case ACTION_OCR_TRANSLATE:
			if(turnOn) {
				enableOcrTranslate();
			} else {
				disableOcrTranslate();
			}
			break;
		case ACTION_LOCK_SCREEN:
			if (turnOn) {
				lockScreen();
			} else {
				unlockScreen();
			}
			break;
		case ACTION_SHOW_TOUCHES:
//			if (status == null) {
//				boolean hasShow = (Boolean) PreferenceSettingsManager
//						.get(PreferenceSettingsManager.SHOW_TOUCHES_CUSTOMIZED);
//				turnOn = !hasShow;
//			}
			
			if (turnOn) {
				showTouches(true);
			} else {
				showTouches(false);
			}
			break;
		case ACTION_USAGE_HELP:
			turnOn = false;
			break;
		case ACTION_DISCLAIMER:
			turnOn = false;
			break;
		}
		
		return (turnOn ? Status.ON : Status.OFF);
	}
	
	private ControlCenter(Context context) {
		mContext = context;
		DroidPop app = DroidPop.getApplication();
		mScreenCoordsManager = (ScreenCoordsManager) app
				.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
		
		mOcrHandler = new OcrHandler(context, new OnOcrRecognitionListener() {
			
			@Override
			public void onRecognized(String text, boolean confidence) {
				Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public boolean isRapidOcr() {
				return true;
			}
		});
		
		mLockScreen = SystemOverlayView.getInstance(mContext);
		mLockScreen.attachedToWindow();
	}
	
	private MenuListAdapter setupMenuListAdapter() {
		Context context = getContext();
		ArrayList<String> groupItems = new ArrayList<String>();
		groupItems.add("SETTINGS");
		groupItems.add("ABOUT");
		
		HashMap<String, List<MenuItemHolder>> childItems = new HashMap<String, List<MenuItemHolder>>();
		
		List<MenuItemHolder> itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_CLIP_TRANSLATE, R.drawable.ic_action_clip_translate, "Clip Translate"));
		itemHolders.add(new MenuItemHolder(ACTION_OCR_TRANSLATE, R.drawable.ic_action_ocr_translate, "OCR Translate"));
		itemHolders.add(new MenuItemHolder(ACTION_LOCK_SCREEN, R.drawable.ic_action_lock_screen_portrait, "Lock Screen"));
		itemHolders.add(new MenuItemHolder(ACTION_SHOW_TOUCHES, R.drawable.ic_action_show_touches_on, "Show Touches"));
		childItems.put(groupItems.get(0), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_USAGE_HELP, R.drawable.ic_action_help, "Usage Help"));
		itemHolders.add(new MenuItemHolder(ACTION_DISCLAIMER, R.drawable.ic_action_about, "Disclaimer"));
		childItems.put(groupItems.get(1), itemHolders);
		
		MenuListAdapter adapter = new MenuListAdapter(context, groupItems, childItems);
		return adapter;
	}
	
	private void enableClipTranslate() {
		TestCase test3 = new OnLongPressTranslationListenerTestCase(getContext(), null);
		test3.setUp();
	}
	
	private void disableClipTranslate() {
		
	}
	
	private void enableOcrTranslate() {
		mScreenCoordsManager.addOnScreenTouchListener(mOcrHandler);
	}
	
	private void disableOcrTranslate() {
		mScreenCoordsManager.removeOnScreenTouchListener(mOcrHandler);
	}
	
	private void lockScreen() {
		mLockScreen.enableTouchable();
	}
	
	private void unlockScreen() {
		mLockScreen.disableTouchable();
	}
	
	private void showTouches(boolean enable) {
		if (enable) {
			mScreenCoordsManager.enableShowTouches();
		} else {
			mScreenCoordsManager.disableShowTouches();
		}
	}

}
