package com.droidpop.dict.youdao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.droidpop.dict.WordEntryException;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordCategoryNotFoundException;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordCategory.WordCategoryConfig;
import com.droidpop.dict.WordEntry.Paraphrase;
import com.droidpop.dict.online.EntryParser;
import com.droidpop.dict.youdao.YouDaoJsonResponse.Web;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YouDaoJsonParser implements EntryParser {
	
	private static class CategoryConfig implements WordCategoryConfig {
		
		private static final HashMap<String, Integer> sConfigMap;
		static {
			sConfigMap = new HashMap<String, Integer>();
			
			sConfigMap.put("abbr.", WordCategory.ABBREVIATION);
			sConfigMap.put("adj.", WordCategory.ADJECTIVE);
			sConfigMap.put("adv.", WordCategory.ADVERB);
			sConfigMap.put("art.", WordCategory.ARTICLE);
			sConfigMap.put("conj.", WordCategory.CONJUNCTION);
			sConfigMap.put("int.", WordCategory.INTERJECTION);
			sConfigMap.put("num.", WordCategory.NUMBERAL);
			sConfigMap.put("prep.", WordCategory.PREPOSITION);
			sConfigMap.put("pron.", WordCategory.PRONOUN);
			
			sConfigMap.put("n.", WordCategory.NOUN);
			
			sConfigMap.put("v.", WordCategory.VERB);
			sConfigMap.put("vt.", WordCategory.VERB_TRANSITIVE);
			sConfigMap.put("vi.", WordCategory.VERB_INTRANSITIVE);
		}

		public static final String abbrGategoryOf(String paraphrase) {
			return paraphrase.substring(0, paraphrase.indexOf('.') + 1);
		}
		
		@Override
		public Map<String, Integer> getCategoryMap() {
			return sConfigMap;
		}
		
	}
	
	private static final WordCategory sCategory;
	static {
		sCategory = WordCategory.getWordCategoryBy(new CategoryConfig());
	}
	
	protected final String mEncode; // YouDao APIv1.1 encode: UTF-8

	public YouDaoJsonParser() {
		this(DEFAULT_ENCODE);
	}

	public YouDaoJsonParser(String encode) {
		mEncode = encode;
	}

	@Override
	public WordEntry parse(InputStream in) throws WordEntryException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, mEncode));
			try {
				Gson gson = new GsonBuilder().create();
				YouDaoJsonResponse response = gson.fromJson(reader,
						YouDaoJsonResponse.class);

				WordEntry entry = new WordEntry();
				
				// get status
				entry.setStatus(getStatus(response.getErrorCode()));
				if (!entry.isValid()) {
					return null;
				}
				
				// get word
				entry.setWord(response.getQuery());
				
				// set phonetic
				String phonetic = response.getBasic().getPhonetic();
				if (phonetic != null) {
					entry.setPhoneticSymbol(phonetic);
				}
				
				// set paraphrases
				entry.setParaphrases(getParaphrases(
						response.getBasic().getExplains(),
						response.getTranslation(),
						response.getWeb()));

				return entry;
			} finally {
				reader.close();
			}
		} catch (UnsupportedEncodingException e) {
			throw new WordEntryException(e.toString(), Status.UNSUPPORTED_ENCODING_EXCEPTION);
		} catch (IOException e) {
			throw new WordEntryException(e.getMessage(), Status.IO_EXCEPTION);
		}	
	}

	private Status getStatus(int errorCode) {
		switch (errorCode) {
		case 0:
			return Status.SUCCESS;
		case 20:
			return Status.ERROR_UNACCEPTABLE_WORD_LENGTH;
		case 30:
			return Status.ERROR_DIRTY_WORD;
		case 40:
			return Status.ERROR_LANGAGE_NOT_SUPPORTED;
		case 50:
			return Status.ERROR_INVALID_API_KEY;
		default:
			return Status.UNKOWN;
		}
	}
	
	private ArrayList<Paraphrase> getParaphrases(String[] explains, String[] translations, Web[] webEntries) {
		ArrayList<Paraphrase> paraphrases = new ArrayList<Paraphrase>();
		
		if(explains != null) {
			for(String explain : explains) {
				String abbr = CategoryConfig.abbrGategoryOf(explain);
				try {
					paraphrases.add(new Paraphrase(sCategory.getCategory(abbr), explain));
				} catch (WordCategoryNotFoundException e) {
					break;
				}
			}
		}
		
		if(translations != null) {
			for(String translation :translations) {
				paraphrases.add(new Paraphrase(WordCategory.OTHERS_TRANSLATION, translation));
			}
		}
		
		if(explains != null) {
			for(Web entry : webEntries) {
				StringBuilder sb = new StringBuilder();
				sb.append(entry.getKey());
				sb.append(' ');
				for(String value : entry.getValue()) {
					sb.append(value);
					sb.append("；"); // TODO: using string.xml
				}
				Paraphrase paraphrase = new Paraphrase(
						WordCategory.OTHERS_WEB_ENTRY, sb.toString());
				paraphrases.add(paraphrase);
			}
		}
		
		return paraphrases;
	}
	
}
