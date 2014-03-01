package com.droidpop.dict.youdao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.droidpop.dict.EntryParseException;
import com.droidpop.dict.EntryParser;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordCategoryNotFoundException;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntry.Paraphrase;
import com.droidpop.dict.youdao.YouDaoJsonResponse.Web;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YouDaoJsonParser implements EntryParser {
	protected static final WordCategory sCategory;
	static {
		sCategory = WordCategory.getWordCategoryBy(new YouDaoTranslator.CategoryConfig());
	}
	
	protected final String mEncode; // YouDao APIv1.1 encode: UTF-8

	public YouDaoJsonParser() {
		this(DEFAULT_ENCODE);
	}

	public YouDaoJsonParser(String encode) {
		mEncode = encode;
	}

	@Override
	public WordEntry parse(InputStream in) throws EntryParseException {
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
			throw new EntryParseException(e.toString(), Status.UNSUPPORTED_ENCODING_EXCEPTION);
		} catch (IOException e) {
			throw new EntryParseException(e.getMessage(), Status.IO_EXCEPTION);
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
				String abbr = YouDaoTranslator.CategoryConfig.abbrGategoryOf(explain);
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
