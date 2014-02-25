package com.droidpop.dict;

import java.util.Map;
import java.util.Set;

import android.util.SparseArray;

public class WordCategory {
	public static interface WordCategoryConfig {
		public Map<String, Integer> getCategoryMap();
	}
	
	public static final int NOUN_MASK = 0x0f;
	public static final int NOUN = 0x01;
	
	public static final int VERB_MASK = 0xf0;
	public static final int VERB = 0x10;
	public static final int VERB_TRANSITIVE = 0x30;
	public static final int VERB_INTRANSITIVE = 0x50;
	
	private final Map<String, Integer> mCategoryMap;
	private SparseArray<String> mCache;
	
	public static WordCategory getWordCategoryBy(WordCategoryConfig config) {
		return new WordCategory(config);
	}
	
	public static boolean isNoun(int category) {
		return isCategoryOf(category, NOUN, NOUN_MASK);
	}
	
	public static boolean isVerb(int category) {
		return isCategoryOf(category, VERB, VERB_MASK);
	}
	
	public int getCategory(String key) {
		return mCategoryMap.get(key);
	}
	
	public String toString(int category) {
		if (mCategoryMap.containsValue(category)) {
			Set<String> set = mCategoryMap.keySet();
			
			String literal = mCache.get(category);
			if(literal != null) {
				return literal;
			}
			
			// cache miss
			for (String key : set) {
				if (mCategoryMap.get(key) == category) {
					mCache.put(category, key);
					return key;
				}
			}
		}

		return null;
	}
	
	private static final boolean isCategoryOf(int value, int category, int mask) {
		return (((value & mask) & category) == category);
	}
	
	private WordCategory(WordCategoryConfig config) {
		mCategoryMap = config.getCategoryMap();
		mCache = new SparseArray<String>();
	}
}
