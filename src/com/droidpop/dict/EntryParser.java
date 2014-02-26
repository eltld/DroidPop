package com.droidpop.dict;

import java.io.InputStream;

public interface EntryParser extends WordEntryReader {
	public static final String DEFAULT_ENCODE = "UTF-8";
	
	public enum Tag {
		KEY,
		VALUE,
		STATUS_CODE,
		PHONETIC_SYMBOL,
		BASIC_PARAPHRASE,
		PARAPHRASES,
		TRANSLATION,
		WEB_ENTRY_MINING
	};
	
	public WordEntry parse(InputStream in) throws EntryParseException;
}
