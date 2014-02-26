package com.droidpop.dict;

import java.text.ParseException;

import com.droidpop.dict.WordEntryReader.Status;


public class EntryParseException extends ParseException {

	private static final long serialVersionUID = 1L;
	
	protected WordEntryReader.Status mStatus;
	
	public EntryParseException(String detailMessage, Status status) {
		this(detailMessage, status, 1);
	}
	
	public EntryParseException(String detailMessage, int location) {
		super(detailMessage, location);
	}
	
	public EntryParseException(String detailMessage, Status status, int location) {
		super(detailMessage, location);
		mStatus = status;
	}
	
	public Status getStatus() {
		return mStatus;
	}

}
