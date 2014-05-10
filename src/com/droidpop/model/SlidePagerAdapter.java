package com.droidpop.model;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.droidpop.dict.WordEntry.Paraphrase;

public class SlidePagerAdapter extends PagerAdapter {

	List<Paraphrase> mParaphrases;

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	public void setParaphrases(ArrayList<Paraphrase> paraphrases) {
		mParaphrases = paraphrases;
	}

	@Override
	public int getCount() {
		if (null != mParaphrases) {
			return mParaphrases.size();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return false;
	}

}
