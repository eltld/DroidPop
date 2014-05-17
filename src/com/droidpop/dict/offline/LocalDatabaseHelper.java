package com.droidpop.dict.offline;

import com.droidpop.dict.WordEntry;

public interface LocalDatabaseHelper {
	public WordEntry getWordEntry(String query);
}
