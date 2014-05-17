package com.droidpop.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordEntry.Paraphrase;

public class SlidePagerAdapter extends PagerAdapter {

	private final Context mContext;
	private final LayoutInflater mLayoutInflater;
	private final List<ViewGroup> mConvertViews;
	private HashMap<Integer, List<Paraphrase>> mSubset;
	private Integer[] mCategorySet;
	
	public SlidePagerAdapter(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mConvertViews = new ArrayList<ViewGroup>();
		mSubset = new HashMap<Integer, List<Paraphrase>>();
		mCategorySet = new Integer[0];
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ViewGroup convertView = null;
		if(mConvertViews.size() > position) {
			convertView = mConvertViews.get(position);
		} else {
			convertView = (ViewGroup) mLayoutInflater.inflate(R.layout.layout_paraphrase_list, null);
			mConvertViews.add(convertView);
		}
		
		int category = mCategorySet[position];
		List<Paraphrase> subset = mSubset.get(category);
		
		TextView categoryView = (TextView) convertView.findViewById(R.id.paraphrase_category);
		categoryView.setText(WordCategory.getUniformWordCategory(mContext)
				.toString(category));
		
		ExpandableListView paraphraseListView = (ExpandableListView) convertView.findViewById(R.id.paraphrase_list);
		ExpandableListAdapter adapter = paraphraseListView.getExpandableListAdapter();
		if(null == adapter || !(adapter instanceof ParaphraseListAdapter)) {
			paraphraseListView.setAdapter(new ParaphraseListAdapter(mContext, subset));
		} else {
			ParaphraseListAdapter listAdapter = (ParaphraseListAdapter) adapter;
			listAdapter.setParaphrases(subset);
		}
		
		ViewPager parent = (ViewPager) container;
		parent.addView(convertView);
		
		return convertView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	public void setParaphrases(ArrayList<Paraphrase> paraphrases) {
		mSubset.clear();

		int category;
		Integer key = null;
		List<Paraphrase> subset = null;
		for(Paraphrase paraphrase : paraphrases) {
			category = paraphrase.getCategory();
			
			if(key == null || category != key) {
				if(mSubset.containsKey(category)) {
					subset = mSubset.get(category);
				} else {
					subset = new ArrayList<Paraphrase>();
					mSubset.put(category, subset);
				}
			}
			
			subset.add(paraphrase);
			key = category;
		}
		
		Object[] keyset = mSubset.keySet().toArray();
		int len = keyset.length;
		mCategorySet = new Integer[len];
		for(int i = 0; i != len; ++i) {
			mCategorySet[i] = (Integer) keyset[i];
		}
		
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mCategorySet.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
