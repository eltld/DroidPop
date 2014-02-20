package com.droidpop.dict;

import java.text.ParseException;

public class EntryParseException extends ParseException {

	private static final long serialVersionUID = 1L;

	public static final int INVALID_INPUT_STREAM = 1 << 0;
	public static final int DIRTY_WORD = 1 << 1;

	public EntryParseException(String detailMessage, int location) {
		super(detailMessage, location);
	}

}
