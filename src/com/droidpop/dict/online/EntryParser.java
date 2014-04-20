package com.droidpop.dict.online;

import java.io.InputStream;

import com.droidpop.dict.WordEntryException;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntryReader;

public interface EntryParser extends WordEntryReader {
	public static final String DEFAULT_ENCODE = "UTF-8";
	
	public WordEntry parse(InputStream in) throws WordEntryException;
}
