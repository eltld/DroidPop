package com.droidpop.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.dict.WordEntry;
import com.droidpop.model.SlidePagerAdapter;

public class WordEntryDetailFragment extends Fragment {
	
	private Typeface mExistencefont;
	
	private WordEntry mWordEntry;
	private boolean mHasInitialized;

	private View mContentLayout;
	private TextView mTitleView;
	private ViewPager mDetailPager;
	private SlidePagerAdapter mDetailPagerAdapter;
	
	public WordEntryDetailFragment() {
		mHasInitialized = false;
	}
	
	public void setWordEntry(WordEntry entry) {
		if (entry.isValid()) {
			mWordEntry = entry;
			mDetailPagerAdapter.setParaphrases(mWordEntry.getParaphrases());
			
			updateView();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Context context = getActivity();
		FontFactory factory = new FontFactory(context);
		mExistencefont = factory.buildFont(Font.EXISTENCE);

		mContentLayout = inflater.inflate(R.layout.layout_word_entry_detail,
				container, false);
		mTitleView = (TextView) mContentLayout.findViewById(R.id.word);
		mTitleView.setTypeface(mExistencefont);
		
		mDetailPager = (ViewPager) mContentLayout.findViewById(R.id.paraphrase);
		mDetailPagerAdapter = new SlidePagerAdapter(context);
		mDetailPager.setAdapter(mDetailPagerAdapter);

		mHasInitialized = true;
		
		return mContentLayout;
	}
	
	private void updateView() {
		if(!mHasInitialized) {
			return;
		}
		
		mTitleView.setText(mWordEntry.getWord());
	}
	

}
