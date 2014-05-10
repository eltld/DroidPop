package com.droidpop.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.model.RecentQueryListAdapter;

public class RecentQueryListFragment extends Fragment {
	
	public static interface OnItemClickListener extends android.widget.AdapterView.OnItemClickListener {
		
	}

	private Typeface mExistencefont;
	
	private OnItemClickListener mListener;
	
	private View mContentView;
	private ListView mRcentQueryListView;
	private TextView mNoContentView;
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Context context = getActivity();
		FontFactory factory = new FontFactory(context);
		mExistencefont = factory.buildFont(Font.EXISTENCE);
				
		mContentView = inflater.inflate(R.layout.layout_recent_query_list,
				container, false);
		
		mRcentQueryListView = (ListView) mContentView.findViewById(R.id.recent_query_list);
		final RecentQueryListAdapter adapter = new RecentQueryListAdapter(context);
		mRcentQueryListView.setAdapter(adapter);
		if(mListener == null && context instanceof OnItemClickListener) {
			mRcentQueryListView.setOnItemClickListener((OnItemClickListener) context);
		}
		
		mNoContentView = (TextView) mContentView.findViewById(R.id.no_content_prompt_view);
		mNoContentView.setTypeface(mExistencefont);
		if(adapter.isEmpty()) {
			mNoContentView.setVisibility(View.VISIBLE);
		}
		
		return mContentView;
	}

}
