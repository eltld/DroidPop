package com.droidpop.dict;

import java.io.InputStream;

public interface EntryParser extends WordEntryReader {
	public static final String DEFAULT_ENCODE = "UTF-8";
	
	public WordEntry parse(InputStream in) throws EntryParseException;
}
