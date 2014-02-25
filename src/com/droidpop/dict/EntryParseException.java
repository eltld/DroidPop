package com.droidpop.dict;

import java.text.ParseException;

public class EntryParseException extends ParseException {

	private static final long serialVersionUID = 1L;
	public static enum Status {
		INVALID_INPUT_STREAM,
		DIRTY_WORD
	};
	
	public EntryParseException(String detailMessage, int location) {
		super(detailMessage, location);
	}

}
