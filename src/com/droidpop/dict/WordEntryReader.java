package com.droidpop.dict;

public interface WordEntryReader {
	public static enum Status {
		// word status
		SUCCESS, UNKOWN,
		// error code
		ERROR_UNACCEPTABLE_WORD_LENGTH, ERROR_DIRTY_WORD, ERROR_LANGAGE_NOT_SUPPORTED, ERROR_INVALID_API_KEY, 
		// exception status
		UNDEFINED_EXCEPTION, UNSUPPORTED_ENCODING_EXCEPTION, IO_EXCEPTION,
	};
}
