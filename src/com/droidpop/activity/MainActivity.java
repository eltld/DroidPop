package com.droidpop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.controller.SettingController;
import com.droidpop.model.MenuListAdapter;
import com.droidpop.model.MenuListAdapter.MenuItemHolder;
import com.droidpop.model.RecentQueryListAdapter;

public class MainActivity extends Activity implements SettingController {
	
	private Typeface mExistencefont;
	private Typeface mRobotoLightFont;
	
	private DrawerLayout mSlideMenuLayout;
	private MenuListAdapter mMenuListAdapter;
	private ExpandableListView mMenuListView;
	
	private AutoCompleteTextView mSearchView;
	private ListView mRcentQueryListView;
	private TextView mNoContentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DroidPop.initFromLauncherActivity(this);
		DroidPop.getApplication().createShortcut(false);
		
		setContentView(R.layout.activity_main);
		initFont();
		setupMenuListAdapter();
		
		mSlideMenuLayout = (DrawerLayout) findViewById(R.id.slide_menu);
		
		mMenuListView = (ExpandableListView) findViewById(R.id.menu_list);
		mMenuListView.setAdapter(mMenuListAdapter);
		
		mSearchView = (AutoCompleteTextView) findViewById(R.id.search_auto_complete);
		mSearchView.setTypeface(mRobotoLightFont);
		mSearchView.setCursorVisible(true);
		
		mRcentQueryListView = (ListView) findViewById(R.id.recent_query_list);
		mRcentQueryListView.setAdapter(new RecentQueryListAdapter(this));
		
		mNoContentView = (TextView) findViewById(R.id.no_content_prompt_view);
		mNoContentView.setTypeface(mExistencefont);
	}
	
	private void initFont() {
		FontFactory factory = new FontFactory(this);
		mExistencefont = factory.buildFont(Font.EXISTENCE);
		mRobotoLightFont = factory.buildFont(Font.ROBOTO_LIGHT);
	}
	
	private void setupMenuListAdapter() {
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
		
		mMenuListAdapter = new MenuListAdapter(this, groupItems, childItems);
	}
	
	@Override
	public void onBackPressed() {
		moveTaskBack();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return moveTaskBack();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean moveTaskBack() {
		if(mSlideMenuLayout.isDrawerOpen(Gravity.RIGHT)) {
			mSlideMenuLayout.closeDrawer(Gravity.RIGHT);
			return true;
		}
		
		return false;
	}

}
