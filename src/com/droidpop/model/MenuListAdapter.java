package com.droidpop.model;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;

public class MenuListAdapter extends BaseExpandableListAdapter {
	
	public static class MenuItemHolder {
		public final int actionId;
		public Integer drawableId;
		public String detail;
		
		public MenuItemHolder(int actionId) {
			this.actionId = actionId;
		}
		
		public MenuItemHolder(int actionId, Integer drawableId, String detail) {
			this.actionId = actionId;
			this.drawableId = drawableId;
			this.detail = detail;
		}
		
	}
	
	private final Context mContext;
	private final List<String> mGroupItems;
	private final Map<String, List<MenuItemHolder>> mChildItems;
	
	private Typeface mGroupItemfont;
	private Typeface mChildItemfont;
	
	public MenuListAdapter(Context context, List<String> groupItems, Map<String, List<MenuItemHolder>> childItems) {
		mContext = context;
		mGroupItems = groupItems;
		mChildItems = childItems;
		
		FontFactory factory = new FontFactory(context);
		mGroupItemfont = factory.buildFont(Font.ROBOTO_BOLD);
		mChildItemfont = factory.buildFont(Font.ROBOTO_LIGHT);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mChildItems.get(mGroupItems.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.layout_menu_item, null);
		}
		
		MenuItemHolder itemHolder = (MenuItemHolder) getChild(groupPosition, childPosition);
		
		TextView tv = (TextView) convertView.findViewById(R.id.menu_item);
		tv.setText(itemHolder.detail);
		tv.setTypeface(mChildItemfont);
		if(itemHolder.drawableId != null) {
			tv.setCompoundDrawablesWithIntrinsicBounds(itemHolder.drawableId, 0, 0, 0);
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<MenuItemHolder> groupHolders = mChildItems.get(mGroupItems.get(groupPosition));
		if(groupHolders == null) {
			return 0;
		} else {
			return groupHolders.size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupItems.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroupItems.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.layout_menu_group, null);
		}
		
		TextView tv = (TextView) convertView.findViewById(R.id.group_item);
		tv.setText((String)getGroup(groupPosition));
		tv.setTypeface(mGroupItemfont);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
