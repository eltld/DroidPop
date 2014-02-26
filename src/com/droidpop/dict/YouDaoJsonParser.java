package com.droidpop.dict;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.JsonReader;

import com.droidpop.dict.WordEntry.Paraphrase;

public class YouDaoJsonParser implements EntryParser {
	protected static final WordCategory sCategory;
	static {
		sCategory = WordCategory.getWordCategoryBy(new YouDaoTranslator.CategoryConfig());
	}
	
	protected final String mEncode; // YouDao APIv1.1 encode: UTF-8
	protected final HashMap<String, Tag> mKeyMap;

	public YouDaoJsonParser() {
		this(DEFAULT_ENCODE);
	}

	public YouDaoJsonParser(String encode) {
		mEncode = encode;

		mKeyMap = new HashMap<String, Tag>();

		mKeyMap.put("translation", Tag.TRANSLATION);
		mKeyMap.put("basic", Tag.BASIC_PARAPHRASE);
		mKeyMap.put("phonetic", Tag.PHONETIC_SYMBOL);
		mKeyMap.put("explains", Tag.PARAPHRASES);
		mKeyMap.put("errorCode", Tag.STATUS_CODE);
		mKeyMap.put("web", Tag.WEB_ENTRY_MINING);
		mKeyMap.put("key", Tag.KEY);
		mKeyMap.put("value", Tag.VALUE);
	}

	@Override
	public WordEntry parse(InputStream in) throws EntryParseException {
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(in, mEncode));
			
			try {
				WordEntry entry = new WordEntry();
				
				while (reader.hasNext()) {
					String key = reader.nextName();
					switch (mKeyMap.get(key)) {
					case STATUS_CODE:
						entry.setStatus(readStatus(reader));
						if(!entry.isValid()) {
							return entry;
						}
						break;
					case BASIC_PARAPHRASE:
						entry = readBasicParaphrase(entry, reader);
						break;
					default:
						reader.skipValue();
						break;
					}
				}

				return entry;
			} finally {
				reader.close();
			}
		} catch (UnsupportedEncodingException e) {
			throw new EntryParseException(e.toString(), Status.UNSUPPORTED_ENCODING_EXCEPTION);
		} catch (IOException e) {
			throw new EntryParseException(e.toString(), Status.IO_EXCEPTION);
		} catch (Exception e) {
			throw new EntryParseException(e.toString(), Status.UNDEFINED_EXCEPTION);
		}
	}

	protected Status readStatus(JsonReader reader) throws IOException {
		switch (reader.nextInt()) {
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
	
	protected List<String> readStringArray(JsonReader reader) throws IOException {
		ArrayList<String> arrStr = new ArrayList<String>();
		
		reader.beginArray();
		while(reader.hasNext()) {
			arrStr.add(reader.nextString());
		}
		reader.endArray();
		
		return arrStr;
	}
	
	private WordEntry readBasicParaphrase(WordEntry entry, JsonReader reader) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			switch (mKeyMap.get(key)) {
			case PHONETIC_SYMBOL:
				entry.setPhoneticSymbol(reader.nextString());
				break;
			case PARAPHRASES:
				List<String> data = readStringArray(reader);
				ArrayList<Paraphrase> paraphrases = new ArrayList<Paraphrase>();
				for (String detail : data) {
					String abbr = YouDaoTranslator.CategoryConfig
							.abbrGategoryOf(detail);
					Paraphrase paraphrase = new Paraphrase(
							sCategory.getCategory(abbr), detail);
					paraphrases.add(paraphrase);
				}
				entry.setParaphrases(paraphrases);
				break;

			default:
				reader.skipValue();
				break;
			}
		}
		reader.endObject();
		
		return entry;
	}
	
}
