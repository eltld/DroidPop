package com.droidpop.model;

import java.util.ArrayList;
import java.util.List;

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
import com.droidpop.dict.WordEntry.Paraphrase;

public class ParaphraseListAdapter extends BaseExpandableListAdapter {
	
	private final Context mContext;
	
	private Typeface mParaphraseFont;
	private Typeface mDemoFont;
	
	private List<Paraphrase> mParaphrases;
	
	public ParaphraseListAdapter(Context context) {
		this(context, null);
	}
	
	public ParaphraseListAdapter(Context context, List<Paraphrase> paraphrases) {
		mContext = context;
		FontFactory factory = new FontFactory(mContext);
		mParaphraseFont = factory.buildFont(Font.ROBOTO_LIGHT);
		mDemoFont = factory.buildFont(Font.ROBOTO_THIN_ITALIC);
		
		if(null == paraphrases) {
			mParaphrases = new ArrayList<Paraphrase>(); 
		} else {
			mParaphrases = paraphrases;
		}
	}
	
	public void setParaphrases(List<Paraphrase> paraphrases) {
		if (null == paraphrases) {
			mParaphrases = new ArrayList<Paraphrase>();
		} else {
			mParaphrases = paraphrases;
		}
		notifyDataSetChanged();
	}
	
	public boolean hasDemo(int groupPosition) {
		return mParaphrases.get(groupPosition).hasDemo();
	}

	@Override
	public int getGroupCount() {
		return mParaphrases.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mParaphrases.get(groupPosition).getDemos().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mParaphrases.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mParaphrases.get(groupPosition).getDemos().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.layout_paraphrase_detail, null);
		}
		
		Paraphrase paraphrase = mParaphrases.get(groupPosition);
		
		TextView tv = (TextView) convertView.findViewById(R.id.paraphrase_detail);
		tv.setText(paraphrase.getDetail());
		tv.setTypeface(mParaphraseFont);
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.layout_paraphrase_demo, null);
		}
		
		Paraphrase paraphrase = mParaphrases.get(groupPosition);
		String demo = paraphrase.getDemos().get(childPosition);
		
		TextView tv = (TextView) convertView.findViewById(R.id.paraphrase_demo);
		tv.setText(demo);
		tv.setTypeface(mDemoFont);
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
