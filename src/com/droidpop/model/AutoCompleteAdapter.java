package com.droidpop.model;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class AutoCompleteAdapter extends ArrayAdapter<String> {

	public AutoCompleteAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
	}

}
