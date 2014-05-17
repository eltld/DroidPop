package com.droidpop.dict;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.droidpop.R;
import com.droidpop.app.DroidPop;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

public class WordCategory {
	public static interface WordCategoryConfig {
		public Map<String, Integer> getCategoryMap();
	}
	
	public static final int ALL_CATEGORY = 0xffffffff;
	public static final int BASIC_CATEGORY = 0x00ffffff;
	
	public static final int ABBREVIATION = 0x01;
	public static final int ADJECTIVE = 0x02;
	public static final int ADVERB = 0x04;
	public static final int ARTICLE = 0x08;
	public static final int CONJUNCTION = 0x10;
	public static final int INTERJECTION = 0x20;
	public static final int NUMBERAL = 0x40;
	public static final int PREPOSITION = 0x80;
	public static final int PRONOUN = 0x100;
	
	public static final int NOUN_MASK = 0xf000;
	public static final int NOUN = 0x1000;
	public static final int NOUN_PROPER = 0x3000;
	public static final int NOUN_COUNTABLE = 0x5000;
	public static final int NOUN_UNCOUNTABLE = 0x9000;
	
	public static final int VERB_MASK = 0xff0000;
	public static final int VERB = 0x010000;
	public static final int VERB_AUXILIARY = 0x030000;
	public static final int VERB_MODAL = 0x050000;
	public static final int VERB_OTHERS = 0x090000;
	public static final int VERB_TRANSITIVE = 0x110000;
	public static final int VERB_INTRANSITIVE = 0x210000;
	
	public static final int OTHERS_MASK = 0xff000000;
	public static final int OTHERS_LEXICAL_PHRASE = 0x01000000;
	public static final int OTHERS_USAGE = 0x02000000;
	public static final int OTHERS_TRANSLATION = 0x03000000;
	public static final int OTHERS_WEB_ENTRY = 0x04000000;
	public static final int OTHERS_UNKOWN = 0x05000000;

	private static WordCategory sUniformWordCategory;
	
	private final Map<String, Integer> mCategoryMap;
	private SparseArray<String> mCache;
	
	public static synchronized WordCategory getUniformWordCategory(Context context) {
		if(null == sUniformWordCategory) {
			sUniformWordCategory = getWordCategoryBy(new UniformWordCategoryConfig(context));
		}
		return sUniformWordCategory;
	}
	
	public static WordCategory getWordCategoryBy(WordCategoryConfig config) {
		return new WordCategory(config);
	}
	
	public static boolean isNoun(int category) {
		return isCategoryOf(category, NOUN, NOUN_MASK);
	}
	
	public static boolean isVerb(int category) {
		return isCategoryOf(category, VERB, VERB_MASK);
	}
	
	public int getCategory(String key) throws WordCategoryNotFoundException {
		Integer value = mCategoryMap.get(key);
		if(value != null) {
			return value;
		} else {
			throw new WordCategoryNotFoundException(key);
		}
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
	
	private static class UniformWordCategoryConfig implements WordCategoryConfig {

		private final HashMap<String, Integer> mConfigMap;
		
		public UniformWordCategoryConfig(Context context) {
			mConfigMap = new HashMap<String, Integer>();
			
			String[] literals = context.getResources().getStringArray(
					R.array.word_category);
			int[] categorys = { ABBREVIATION, ADJECTIVE, ADVERB, ARTICLE,
					CONJUNCTION, INTERJECTION, NUMBERAL, PREPOSITION, PRONOUN,

					NOUN, NOUN_PROPER, NOUN_COUNTABLE, NOUN_UNCOUNTABLE,

					VERB, VERB_AUXILIARY, VERB_MODAL, VERB_TRANSITIVE,
					VERB_INTRANSITIVE,

					OTHERS_LEXICAL_PHRASE, OTHERS_USAGE, OTHERS_TRANSLATION,
					OTHERS_WEB_ENTRY, OTHERS_UNKOWN };
			
			if(literals.length != categorys.length) {
				DroidPop.log(DroidPop.LEVEL_WARN, "bad UniformWordCategoryConfig");
			}

			int len = Math.min(literals.length, categorys.length);
			for(int i = 0; i != len; ++i) {
				mConfigMap.put(literals[i], categorys[i]);
			}
		}
		
		@Override
		public Map<String, Integer> getCategoryMap() {
			return mConfigMap;
		}
		
	}
}
