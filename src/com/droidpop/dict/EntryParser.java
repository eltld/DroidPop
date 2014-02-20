package com.droidpop.dict;

import java.io.InputStream;

public interface EntryParser {
	public boolean parse(InputStream in) throws EntryParseException;
	public int getState();
	public String getWord();
	public String getPhoneticSymbol();
	public String getBasicParaphrase();
	public String[] getParaphrases();
	public String[] getParaphraseBy(int category);
}
