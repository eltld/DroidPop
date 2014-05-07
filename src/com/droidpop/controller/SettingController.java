package com.droidpop.controller;

public interface SettingController {
	public enum Status {
		ON, OFF
	}

	public static final int ACTION_SETTING = 0x1000;
	public static final int ACTION_CLIP_TRANSLATE = ACTION_SETTING | 0x0001;
	public static final int ACTION_OCR_TRANSLATE = ACTION_SETTING | 0x0002;
	public static final int ACTION_LOCK_SCREEN = ACTION_SETTING | 0x0004;
	public static final int ACTION_SHOW_TOUCHES = ACTION_SETTING | 0x0008;
	public static final int ACTION_ABOUT = 0x2000;
	public static final int ACTION_USAGE_HELP = ACTION_ABOUT | 0x0001;
	public static final int ACTION_DISCLAIMER = ACTION_ABOUT | 0x0002;
}
