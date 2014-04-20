package com.droidpop.dict.wordnet;

import com.droidpop.dict.Translator;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntryReader;

public class WordNetTranslator extends Translator {

	public WordNetTranslator(WordEntryReader reader) {
		super(reader);
	}

	@Override
	public WordEntry autoTranslate(String text, String to) {
		return null;
	}

	@Override
	public WordEntry manualTranslate(String text, String from, String to) {
		return autoTranslate(text, to);
	}
	
	@Override
	public boolean isDefualtAutoDetect() {
		return true;
	}

}
