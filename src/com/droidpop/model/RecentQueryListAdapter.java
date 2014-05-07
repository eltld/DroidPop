package com.droidpop.model;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.model.RecentQueryCache.RecentQuery;

public class RecentQueryListAdapter extends BaseAdapter {
	
	protected final Context mContext;
	protected RecentQueryCache mCache;
	
	private Typeface mWordFont;
	private Typeface mParaphraseFont;
	
	public RecentQueryListAdapter(Context context) {
		mContext = context;
		mCache = RecentQueryCache.getRecentQueryCache(context);

		FontFactory factory = new FontFactory(context);
		mWordFont = factory.buildFont(Font.EXISTENCE);
		mParaphraseFont = factory.buildFont(Font.ROBOTO_THIN);
	}

	@Override
	public int getCount() {
		return mCache.size();
	}

	@Override
	public Object getItem(int position) {
		return mCache.getRecentQueryByIndex(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.layout_word_entry_item, null);
		}
		
		TextView wordView = (TextView) convertView.findViewById(R.id.word);
		wordView.setTypeface(mWordFont);
		TextView basicParapyraseView = (TextView) convertView.findViewById(R.id.basic_paraphrase);
		basicParapyraseView.setTypeface(mParaphraseFont);
		
		RecentQuery recentItem = mCache.getRecentQueryByIndex(position);
		if (recentItem.query != null) {
			if (recentItem.result == null) {
				wordView.setText(recentItem.query);
			} else {
				wordView.setText(recentItem.result.getWord());
				basicParapyraseView.setText(recentItem.result
						.getBasicParaphrase().getDetail());
			}
		}
		
		return convertView;
	}

}
