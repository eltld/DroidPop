package com.droidpop.dict.youdao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.util.JsonReader;

import com.droidpop.dict.EntryParseException;
import com.droidpop.dict.EntryParser;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntry.Paraphrase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YouDaoJsonParser implements EntryParser {
	protected static final WordCategory sCategory;
	static {
		sCategory = WordCategory.getWordCategoryBy(new YouDaoTranslator.CategoryConfig());
	}
	
	protected final String mEncode; // YouDao APIv1.1 encode: UTF-8
	protected final HashMap<String, Tag> mTagMap;

	public YouDaoJsonParser() {
		this(DEFAULT_ENCODE);
	}

	public YouDaoJsonParser(String encode) {
		mEncode = encode;

		mTagMap = new HashMap<String, Tag>();

		mTagMap.put("translation", Tag.TRANSLATION);
		mTagMap.put("basic", Tag.BASIC_PARAPHRASE);
		mTagMap.put("phonetic", Tag.PHONETIC_SYMBOL);
		mTagMap.put("explains", Tag.PARAPHRASES);
		mTagMap.put("errorCode", Tag.STATUS_CODE);
		mTagMap.put("web", Tag.WEB_ENTRY_MINING);
		mTagMap.put("key", Tag.KEY);
		mTagMap.put("value", Tag.VALUE);
	}

	@Override
	public WordEntry parse(InputStream in) throws EntryParseException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, mEncode));
//			char[] json = new char[1024];
//			CharBuffer buffer = CharBuffer.wrap(json);
//			br.read(buffer);
//			JSONObject jobj = new JSONObject(new String(json));
//			System.out.println(jobj.toString(2));
			
			Gson gson = new GsonBuilder().create();
			YouDaoJsonResponse response = gson.fromJson(br, YouDaoJsonResponse.class);
			System.out.println(response.toString());
			
			JsonReader reader = new JsonReader(new InputStreamReader(in, mEncode));
			
			try {
				WordEntry entry = new WordEntry();
				
				while (reader.hasNext()) {
					String key = reader.nextName();
					switch (mTagMap.get(key)) {
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
			switch (mTagMap.get(key)) {
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
