package com.droidpop.controller;

import com.droidpop.model.MenuListAdapter;

public interface QuickSettingController extends SettingController {
	
	public MenuListAdapter getMenuListAdapter();
	public Status onOptionsItemSelected(int actionId, Status status);

}
