package com.droidpop.dict.wordnet;

import java.util.Locale;

import com.droidpop.dict.WordEntryReader;
import com.droidpop.dict.offline.OfflineTranslator;

public class WordNetTranslator extends OfflineTranslator {

	public WordNetTranslator(WordEntryReader reader) {
		super(reader);
	}
	
	@Override
	public String getLocale() {
		return Locale.US.getLanguage();
	}

}
