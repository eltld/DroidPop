package com.droidpop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.wtao.utils.Log;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.droidpop.R;
import com.droidpop.activity.RecentQueryListFragment.OnItemClickListener;
import com.droidpop.app.ContextUtils;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.controller.SettingController;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.offline.OfflineTranslator.Vocabulary;
import com.droidpop.dict.wordnet.WordNetTranslator;
import com.droidpop.model.AutoCompleteAdapter;
import com.droidpop.model.MenuListAdapter;
import com.droidpop.model.MenuListAdapter.MenuItemHolder;
import com.droidpop.model.RecentQueryCache;
import com.droidpop.model.RecentQueryCache.RecentQuery;
import com.droidpop.view.SlideDockView;

public class MainActivity extends FragmentActivity implements
		SettingController, OnItemClickListener, OnPageChangeListener {
	
	private static final String TAG = "MainActivity";
	private static SlideDockView sSlideDockView;
	
	private Typeface mExistencefont;
	private Typeface mRobotoLightFont;
	
	private InputMethodManager mInputMethodManager;
	private RecentQueryCache mRecentQueryCache;
	
	private Dialog mExitPromptDialog;
	
	private ViewPager mFragmentPager;
	private TwoPanelPagerAdapter mFragmentPagerAdapter;
	
	private DrawerLayout mSlideMenuLayout;
	private MenuListAdapter mMenuListAdapter;
	private ExpandableListView mMenuListView;
	
	private AutoCompleteTextView mSearchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		initFont();
		setupActionBar();
		setupMenuListAdapter();
		
		if(null == sSlideDockView) {
			sSlideDockView = new SlideDockView(getApplicationContext());
			sSlideDockView.attachedToWindow();
			sSlideDockView.setEnable();
		}
		
		mRecentQueryCache = RecentQueryCache.getRecentQueryCache(this);
		
		mFragmentPager = (ViewPager) findViewById(R.id.fragment_pager);
		mFragmentPagerAdapter = new TwoPanelPagerAdapter(getSupportFragmentManager());
		mFragmentPager.setAdapter(mFragmentPagerAdapter);
		
		mSlideMenuLayout = (DrawerLayout) findViewById(R.id.slide_menu);
		
		mMenuListView = (ExpandableListView) findViewById(R.id.menu_list);
		mMenuListView.setAdapter(mMenuListAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		
		MenuItem item =  menu.findItem(R.id.action_search);
		mSearchView = (AutoCompleteTextView) item.getActionView().findViewById(R.id.search_auto_complete);
		mSearchView.setCursorVisible(true);
		mSearchView.setTypeface(mRobotoLightFont);
		
		final WordNetTranslator translator = new WordNetTranslator(this);
		List<String> words = translator.getVocabularyBy(Vocabulary.WORD);
		if(words != null) {
			AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
					R.layout.layout_auto_complete_item, R.id.auto_complete_word,
					words);
			mSearchView.setAdapter(adapter);
		}
		
		mSearchView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				final Context context = v.getContext();
				String query = v.getText().toString();
				WordEntry entry = translator.translte(query);
				RecentQuery recentItem = new RecentQuery(System.currentTimeMillis(), query, entry);
				RecentQueryCache.getRecentQueryCache(context).addRecentItem(recentItem);
				
				clearImeStatus(v);
				showDetail(recentItem, true);
				
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onPause() {
		ContextUtils.createShortcutForLauncher(this, false);
		super.onPause();
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
	
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		if(TwoPanelPagerAdapter.FRAGMENT_WORD_ENTRY_DETAIL == position) {
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		RecentQuery recentItem = mRecentQueryCache.getRecentQueryByIndex(position);
		showDetail(recentItem, true);
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
		TextView contactMeView = (TextView) findViewById(R.id.contact_me);
		contactMeView.setTypeface(mRobotoLightFont);
		
		ArrayList<String> groupItems = new ArrayList<String>();
		groupItems.add("ONLINE DICTIONARY");
		groupItems.add("LOCAL DICTIONARY");
		groupItems.add("FEATURES");
		groupItems.add("ABOUT");
		groupItems.add("APPLICATION");
		
		HashMap<String, List<MenuItemHolder>> childItems = new HashMap<String, List<MenuItemHolder>>();
		
		List<MenuItemHolder> itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_YOUDAO_TRANSLATE, R.drawable.ic_dict_youdao_light, "YouDao Translate APIv1.0"));
		childItems.put(groupItems.get(0), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_WORDNET_DICTIONARY, R.drawable.ic_shortcuts_notebook_add, "WordNet Dictionary"));
		childItems.put(groupItems.get(1), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_CLIP_TRANSLATE, R.drawable.ic_action_clip_translate, "Clip Translate"));
		itemHolders.add(new MenuItemHolder(ACTION_OCR_TRANSLATE, R.drawable.ic_action_search_light, "OCR Translate"));
		itemHolders.add(new MenuItemHolder(ACTION_SHOW_SLIDE_DOCK, R.drawable.ic_action_lock_screen_portrait, "Show Slide Dock"));
		itemHolders.add(new MenuItemHolder(ACTION_SHOW_TOUCHES, R.drawable.ic_action_show_touches_on, "Show Touches"));
		childItems.put(groupItems.get(2), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_USAGE_HELP, R.drawable.ic_action_help, "Usage Help"));
		itemHolders.add(new MenuItemHolder(ACTION_DISCLAIMER, R.drawable.ic_action_about, "Disclaimer"));
		childItems.put(groupItems.get(3), itemHolders);
		
		itemHolders = new ArrayList<MenuListAdapter.MenuItemHolder>();
		itemHolders.add(new MenuItemHolder(ACTION_EXIT, R.drawable.ic_action_show_touches_on, "Exit"));
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
	
	private void showDetail(RecentQuery recentItem, boolean smoothScroll) {
		if(null != recentItem && null != recentItem.result) {
			mFragmentPagerAdapter.setWordEntry(recentItem.result);
			mFragmentPager.setCurrentItem(TwoPanelPagerAdapter.FRAGMENT_WORD_ENTRY_DETAIL, smoothScroll);
		}
	}
	
	private void clearImeStatus(View v) {
		v.clearFocus();
		
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	private class TwoPanelPagerAdapter extends FragmentStatePagerAdapter {

		public static final int FRAGMENT_RECNET_QUERY_LIST = 0;
		public static final int FRAGMENT_WORD_ENTRY_DETAIL = 1;
		private static final int FRAGMENT_PAGER_COUNT = 2;
		
		private RecentQueryListFragment mRecnetQueryListFragment;
		private WordEntryDetailFragment mWordEntryDetailFragment;

		public TwoPanelPagerAdapter(FragmentManager fm) {
			super(fm);
			mRecnetQueryListFragment = new RecentQueryListFragment();
			mWordEntryDetailFragment = new WordEntryDetailFragment();
		}
		
		public void setWordEntry(WordEntry entry) {
			mWordEntryDetailFragment.setWordEntry(entry);
		}

		@Override
		public Fragment getItem(int position) {
			if (FRAGMENT_RECNET_QUERY_LIST == position) {
				return mRecnetQueryListFragment;
			} else {
				return mWordEntryDetailFragment;
			}
		}

		@Override
		public int getCount() {
			return FRAGMENT_PAGER_COUNT;
		}

	}

}
