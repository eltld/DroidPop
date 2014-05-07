package com.droidpop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class MenuListView extends ExpandableListView {

	public MenuListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MenuListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MenuListView(Context context) {
		super(context);
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(adapter);

		int size = adapter.getGroupCount();
		for (int groupPos = 0; groupPos != size; ++groupPos) {
			expandGroup(groupPos);
		}

		setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// neither collapse nor expand

				return true;
			}
		});
		
		setDivider(null);
	}

}
