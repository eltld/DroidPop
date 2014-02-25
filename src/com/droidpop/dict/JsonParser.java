package com.droidpop.dict;

import java.io.InputStream;

public class JsonParser implements EntryParser {

	public static final String DEFAULT_ENCODE = "UTF-8";
	
	protected String mEncode = DEFAULT_ENCODE;
	
	protected String mWord;
	protected String mPhoneticSymbol;
	
	@Override
	public WordEntry parse(InputStream in) throws EntryParseException {
		
        
		return null;
	}
}
