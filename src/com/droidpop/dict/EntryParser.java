package com.droidpop.dict;

import java.io.InputStream;

public interface EntryParser {
	public WordEntry parse(InputStream in) throws EntryParseException;
}
