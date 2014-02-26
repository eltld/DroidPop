package com.droidpop.dict;

import java.util.Locale;


public abstract class Translator {
	protected WordEntryReader mReader;
	
	public Translator(WordEntryReader reader) {
		mReader = reader;
	}

	/**
	 * auto detect language of source text and translate
	 * 
	 * @param text source text to translate
	 * @param to target language
	 */
	public abstract WordEntry autoTranslate(String text, String to);
	
	/**
	 * manually set translation from source to target language
	 * 
	 * @param text source text to translate
	 * @param from source language
	 * @param to target language
	 */
	public abstract WordEntry manualTranslate(String text, String from, String to);
	
	/**
	 * 
	 * @return true if defualt auto detect language of source text
	 */
	public boolean isDefualtAutoDetect() {
		return false;
	}
	
	public String getLocale() {
		return Locale.CHINA.getLanguage();
	}
}
