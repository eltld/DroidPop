package com.droidpop.view;

import me.wtao.utils.ScreenMetrics;
import me.wtao.utils.ViewUtils;
import me.wtao.view.FloatingView;
import me.wtao.view.Hotspot;
import me.wtao.view.Hotspot.OnHotspotListener;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.droidpop.R;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.controller.ControlCenter;
import com.droidpop.controller.QuickSettingController;
import com.droidpop.controller.SettingController.Status;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.wordnet.WordNetTranslator;
import com.droidpop.model.MenuListAdapter;
import com.droidpop.model.MenuListAdapter.MenuItemHolder;
import com.droidpop.model.RecentQueryCache;
import com.droidpop.model.RecentQueryCache.RecentQuery;
import com.droidpop.model.SlidePagerAdapter;
import com.droidpop.view.MenuDrawerLayout.OnInterceptTouchEventListener;

public class SlideDockView extends FloatingView {
	
	private static final String TAG = "SlideDockView";
	
	private final Typeface mRobotoLightfont;
	private ScreenMetrics mScreenMetrics;
	
	private QuickSettingController mController;
	private InputMethodManager mInputMethodManager;
	
	private RelativeLayout mContainer;
	private MenuDrawerLayout mSlidingLayout;
	private AutoCompleteTextView mSearchView;
	
	private TextView mTitleView;
	private ViewPager mDetailPager;
	private SlidePagerAdapter mDetailPagerAdapter;
	
	private ExpandableListView mMenuListView;
	private Hotspot mHotSide;
	
	public SlideDockView(Context context) {
		super(context);

		FontFactory factory = new FontFactory(context);
		mRobotoLightfont = factory.buildFont(Font.ROBOTO_LIGHT);
		mScreenMetrics = new ScreenMetrics(context);
		
		mController = ControlCenter.getInstance(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		mContainer = (RelativeLayout)inflater.inflate(R.layout.layout_slide_dock_view, this);
		
		mSlidingLayout = (MenuDrawerLayout) mContainer.findViewById(R.id.slide_dock_view);
		mSlidingLayout.setDrawerListener(new DrawerListener() {
			
			@Override
			public void onDrawerStateChanged(int drawerView) {
				
			}
			
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
			}
			
			@Override
			public void onDrawerOpened(View drawerView) {
				setFocusable(true);
				setTouchable(true);
			}
			
			@Override
			public void onDrawerClosed(View drawerView) {
				setFocusable(false);
				setTouchable(false);
			}
		});
		mSlidingLayout.setOnInterceptTouchEventListener(new OnInterceptTouchEventListener() {
			
			@Override
			public boolean shouldInterceptTouchEvent(MotionEvent event) {
				if(mSlidingLayout.isDrawerOpen(Gravity.RIGHT)) {
					if(ViewUtils.isOnTouch(mSlidingLayout, mDetailPager, (int)event.getX(), (int)event.getY())) {
						return true;
					}
				}
				
				return false;
			}
		});
		
		mSearchView = (AutoCompleteTextView) mSlidingLayout.findViewById(R.id.search_auto_complete);
		mSearchView.setTypeface(mRobotoLightfont);
		mSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText thiz = (EditText) v;
				thiz.setCursorVisible(hasFocus);
			}
		});
		
		final WordNetTranslator translator = new WordNetTranslator(getContext());
		mSearchView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				final Context context = v.getContext();
				String query = v.getText().toString();
				WordEntry entry = translator.translte(query);
				RecentQuery recentItem = new RecentQuery(System
						.currentTimeMillis(), query, entry);
				RecentQueryCache.getRecentQueryCache(context).addRecentItem(
						recentItem);

				clearImeStatus();
				setWordEntry(recentItem.result);

				return false;
			}
			
			public void setWordEntry(WordEntry entry) {
				if (entry != null && entry.isValid()) {
					mTitleView.setText(entry.getWord());
					mDetailPagerAdapter.setParaphrases(entry.getParaphrases());
				}
			}
		});
		
		mTitleView = (TextView) mSlidingLayout.findViewById(R.id.word);
		mTitleView.setTypeface(mRobotoLightfont);
		
		mDetailPager = (ViewPager) mSlidingLayout.findViewById(R.id.paraphrase);
		mDetailPagerAdapter = new SlidePagerAdapter(context);
		mDetailPager.setAdapter(mDetailPagerAdapter);
		
		mMenuListView = (ExpandableListView) mSlidingLayout.findViewById(R.id.menu_list);
		final MenuListAdapter adapter = mController.getMenuListAdapter();
		mMenuListView.setAdapter(adapter);
		mMenuListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// neither collapse nor expand
				
				clearImeStatus();
				return true;
			}
		});
		
		mMenuListView.setOnChildClickListener(new OnChildClickListener() {
			
			private final int KEY_QUICK_SETTING_STATUS = QuickSettingController.Status.class.hashCode();
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				MenuItemHolder itemHolder = (MenuItemHolder) adapter.getChild(groupPosition, childPosition);
				
				QuickSettingController.Status status = (QuickSettingController.Status) v.getTag(KEY_QUICK_SETTING_STATUS);
				status = mController.onOptionsItemSelected(itemHolder.actionId, status);
				v.setTag(KEY_QUICK_SETTING_STATUS, status);
				if ((itemHolder.actionId & QuickSettingController.ACTION_FEATURE) == QuickSettingController.ACTION_FEATURE) {
					TextView tv = (TextView) v.findViewById(R.id.menu_item);
					switch (itemHolder.actionId) {
					case QuickSettingController.ACTION_CLIP_TRANSLATE:
						
						break;

					case QuickSettingController.ACTION_OCR_TRANSLATE:
						break;
						
					case QuickSettingController.ACTION_LOCK_SCREEN:
						setSwitchStatus(tv, status,
								(mScreenMetrics.isLandspace() ? R.drawable.ic_action_lock_screen_landscape
										: R.drawable.ic_action_lock_screen_portrait),
								"Lock Screen",
								R.drawable.ic_action_unlock_screen,
								"Unlock Screen");
						break;
						
					case QuickSettingController.ACTION_SHOW_TOUCHES:
						setSwitchStatus(tv, status,
								R.drawable.ic_action_show_touches_on,
								"Show Touches",
								R.drawable.ic_action_show_touches_off,
								"Hide Touches");
						break;
					}
				}
				
				clearImeStatus();
				
				return true;
			}
		});
		
		mHotSide = new Hotspot(context);
		mHotSide.setGravity(Hotspot.EDGE_RIGHT);
		mHotSide.setPhysicalScreenMode();
		mHotSide.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		mHotSide.setOnHotspotListener(new OnHotspotListener() {
			
			@Override
			public int getHotspotGravity() {
				return Hotspot.EDGE_RIGHT;
			}
			
			@Override
			public boolean dispatchTouchEvent(MotionEvent event) {
				event.setLocation(event.getRawX(), event.getRawY());
				return mSlidingLayout.dispatchTouchEvent(event);
			}
		});
	}
	
	public void setEnable() {
		mHotSide.show();
		show();
	}
	
	public void setDisable() {
		hide();
		mHotSide.hide();
	}
	
	public void setFocusable(boolean focus) {
		if(focus) {
			mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		} else {
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		}
		
		sWindowManager.updateViewLayout(this, mWindowParams);
	}
	
	public void setTouchable(boolean enable) {
		if(enable) {
			mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		} else {
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		}
		
		sWindowManager.updateViewLayout(this, mWindowParams);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		requestMessure();
		if (hasAttachedToWindow()) {
			sWindowManager.updateViewLayout(this, mWindowParams);
		}

		super.onLayout(changed, l, t, r, mScreenMetrics.getResolutionY());
	}
	
	@Override
	protected void onInitializeWindowLayoutParams() {
		super.onInitializeWindowLayoutParams();

		mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

		mWindowParams.x = 0;
		mWindowParams.y = 0;
		mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;

		requestMessure();
	}
	
	private void requestMessure() {
		mScreenMetrics.setDefaultDisplayMode();
		mScreenMetrics.messure();

		mWindowParams.height = mScreenMetrics.getHeight();

		mHotSide.setHotspotWidth(mScreenMetrics.getStatusBarHeight() / 2);
		mHotSide.setHotspotHeight(mScreenMetrics.getHeight());
	}
	
	private void clearImeStatus() {
		mSearchView.clearFocus();
		
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
	}
	
	private void setSwitchStatus(TextView view, Status status, int drawableOn, String textOn, int drawableOff, String textOff) {
		if(status == Status.OFF) {
			// switch to turn on
			view.setCompoundDrawablesWithIntrinsicBounds(drawableOn, 0, 0, 0);
			view.setText(textOn);
		} else {
			// switch to turn off
			view.setCompoundDrawablesWithIntrinsicBounds(drawableOff, 0, 0, 0);
			view.setText(textOff);
		}
	}

}
