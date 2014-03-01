package com.droidpop.dict;

import com.droidpop.app.DroidPop;

public class WordCategoryNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public WordCategoryNotFoundException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
		
		log(detailMessage);
	}

	public WordCategoryNotFoundException(String detailMessage) {
		super(detailMessage);
		
		log(detailMessage);
	}
	
	private void log(String detailMessage) {
		DroidPop.log(DroidPop.LEVEL_WARN, detailMessage);
	}
	
}
