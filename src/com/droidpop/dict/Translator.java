package com.droidpop.dict;

import java.io.InputStream;

public interface Translator {
	/**
	 * auto detect language of source text and translate
	 * 
	 * @param text source text to translate
	 * @param to target language
	 */
	public InputStream autoTranslate(String text, String to);
	/**
	 * 
	 * @return true if defualt auto detect language of source text
	 */
	public boolean isDefualtAutoDetect();
	/**
	 * manually set translation from source to target language
	 * 
	 * @param text source text to translate
	 * @param from source language
	 * @param to target language
	 */
	public InputStream manualTranslate(String text, String from, String to);
}
