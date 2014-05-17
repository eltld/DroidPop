package com.droidpop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import com.droidpop.model.ParaphraseListAdapter;

public class ParaphraseListView extends ExpandableListView {

	public ParaphraseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ParaphraseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ParaphraseListView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				ParaphraseListAdapter adapter = (ParaphraseListAdapter) getExpandableListAdapter();
				boolean interrupt = !adapter.hasDemo(groupPosition);
				return interrupt;
			}
		});
		
		setDivider(null);
	}

}
