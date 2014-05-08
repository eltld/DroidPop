package com.droidpop.controller;

public interface SettingController {
	public enum Status {
		ON, OFF
	}
	
	public static final int ACTION_ONLINE_DICTIONARY = 0x010000;
	public static final int ACTION_YOUDAO_TRANSLATE = ACTION_ONLINE_DICTIONARY | 0x0001;
	
	public static final int ACTION_LOCAL_DICTIONARY = 0x020000;
	public static final int ACTION_WORDNET_DICTIONARY = ACTION_LOCAL_DICTIONARY | 0x0001;
	
	public static final int ACTION_FEATURE = 0x040000;
	public static final int ACTION_CLIP_TRANSLATE = ACTION_FEATURE | 0x0001;
	public static final int ACTION_OCR_TRANSLATE = ACTION_FEATURE | 0x0002;
	public static final int ACTION_LOCK_SCREEN = ACTION_FEATURE | 0x0004;
	public static final int ACTION_SHOW_TOUCHES = ACTION_FEATURE | 0x0008;
	
	public static final int ACTION_GENERAL = 0x080000;
	public static final int ACTION_SHOW_SLIDE_DOCK = ACTION_GENERAL | 0x0001;
	public static final int ACTION_EXIT = ACTION_GENERAL | 0x0002;
	
	public static final int ACTION_ABOUT = 0x100000;
	public static final int ACTION_USAGE_HELP = ACTION_ABOUT | 0x0001;
	public static final int ACTION_DISCLAIMER = ACTION_ABOUT | 0x0002;
}
