package com.droidpop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.wtao.utils.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
	
	private static final String TAG = "MainActivity";
	
	private Typeface mExistencefont;
	private Typeface mRobotoLightFont;
	
	private Dialog mExitPromptDialog;
	
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
		setupActionBar();
		setupMenuListAdapter();
		
		mSlideMenuLayout = (DrawerLayout) findViewById(R.id.slide_menu);
		
		mMenuListView = (ExpandableListView) findViewById(R.id.menu_list);
		mMenuListView.setAdapter(mMenuListAdapter);
		
		mRcentQueryListView = (ListView) findViewById(R.id.recent_query_list);
		mRcentQueryListView.setAdapter(new RecentQueryListAdapter(this));
		
		mNoContentView = (TextView) findViewById(R.id.no_content_prompt_view);
		mNoContentView.setTypeface(mExistencefont);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			break;

		case R.id.action_settings:
			showSettings();
			break;
			
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
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
	
	private void initFont() {
		FontFactory factory = new FontFactory(this);
		mExistencefont = factory.buildFont(Font.EXISTENCE);
		mRobotoLightFont = factory.buildFont(Font.ROBOTO_LIGHT);
	}

	private void setupActionBar() {
		try {
			TextView actionBarTitle = (TextView) findViewById(getIdentifier("action_bar_title"));
			actionBarTitle.setTypeface(mExistencefont);
			actionBarTitle.setTextColor(Color.WHITE);
		} catch (Exception e) {
			Log.w(TAG, "failed to decorate the dialog");
		}
	}

	private void setupMenuListAdapter() {
		ArrayList<String> groupItems = new ArrayList<String>();
		groupItems.add("ONLINE DICTIONARY");
		groupItems.add("LOCAL DICTIONARY");
		groupItems.add("FEATURES");
		groupItems.add("GENERAL");
		groupItems.add("ABOUT");
		
		HashMap<String, List<MenuItemHolder>> childItems = new HashMap<String, List<MenuItemHolder>>();
		
		List<MenuItemHolder> itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_YOUDAO_TRANSLATE, R.drawable.ic_action_clip_translate, "YouDao Translate APIv1.0"));
		childItems.put(groupItems.get(0), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_WORDNET_DICTIONARY, R.drawable.ic_action_clip_translate, "WordNet Dictionary"));
		childItems.put(groupItems.get(1), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_CLIP_TRANSLATE, R.drawable.ic_action_clip_translate, "Clip Translate"));
		itemHolders.add(new MenuItemHolder(ACTION_OCR_TRANSLATE, R.drawable.ic_action_search_light, "OCR Translate"));
		itemHolders.add(new MenuItemHolder(ACTION_LOCK_SCREEN, R.drawable.ic_action_lock_screen_portrait, "Lock Screen"));
		itemHolders.add(new MenuItemHolder(ACTION_SHOW_TOUCHES, R.drawable.ic_action_show_touches_on, "Show Touches"));
		childItems.put(groupItems.get(2), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_SHOW_SLIDE_DOCK, R.drawable.ic_action_lock_screen_portrait, "Show Slide Dock"));
		itemHolders.add(new MenuItemHolder(ACTION_EXIT, R.drawable.ic_action_show_touches_on, "Exit"));
		childItems.put(groupItems.get(3), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_USAGE_HELP, R.drawable.ic_action_help, "Usage Help"));
		itemHolders.add(new MenuItemHolder(ACTION_DISCLAIMER, R.drawable.ic_action_about, "Disclaimer"));
		childItems.put(groupItems.get(4), itemHolders);
		
		mMenuListAdapter = new MenuListAdapter(this, groupItems, childItems);
	}

	private boolean moveTaskBack() {
		if(mSlideMenuLayout.isDrawerOpen(Gravity.RIGHT)) {
			mSlideMenuLayout.closeDrawer(Gravity.RIGHT);
			return true;
		}
		
		if(mExitPromptDialog == null) {
			mExitPromptDialog = buildAndShowExitPromptDialog();
		} else {
			mExitPromptDialog.show();
		}
		
		return true;
	}
	
	private Dialog buildAndShowExitPromptDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.exit_using_back_title).setMessage(R.string.exit_using_back_message);
		builder.setPositiveButton(R.string.exit_positive_button_prompt,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						moveTaskToBack(true);
					}
				});
		builder.setNegativeButton(R.string.exit_negative_button_prompt,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO: exit
					}
				});
		
		Dialog dialog = builder.create();
		dialog.show();

		try {
			// MUST called after show(), where did inflate layout
			View contentView = dialog.findViewById(Window.ID_ANDROID_CONTENT);
			TextView alertTitle = (TextView) contentView.findViewById(getIdentifier("alertTitle"));
			alertTitle.setTypeface(mRobotoLightFont);
			
			TextView message = (TextView) contentView.findViewById(android.R.id.message);
			message.setTypeface(mRobotoLightFont);

			Button positiveButton = (Button) contentView.findViewById(android.R.id.button1);
			positiveButton.setTypeface(mRobotoLightFont);
			positiveButton.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
			positiveButton.setBackgroundResource(R.drawable.bg_menu_button_item);
			
			Button negativeButton = (Button) contentView.findViewById(android.R.id.button2);
			negativeButton.setTypeface(mRobotoLightFont);
			negativeButton.setBackgroundResource(R.drawable.bg_menu_button_item);
		} catch(Exception e) {
			Log.w(TAG, "failed to decorate the dialog");
		}
		
		return dialog;
	}
	
	private int getIdentifier(String name) {
		return getResources().getIdentifier(name, "id", "android");
	}
	
	private void showSettings() {
		if (mSlideMenuLayout.isDrawerOpen(Gravity.RIGHT)) {
			mSlideMenuLayout.closeDrawer(Gravity.RIGHT);
		} else {
			mSlideMenuLayout.openDrawer(Gravity.RIGHT);
		}
	}

}
