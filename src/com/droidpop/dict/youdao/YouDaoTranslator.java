package com.droidpop.dict.youdao;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.droidpop.app.DroidPop;
import com.droidpop.dict.EntryParseException;
import com.droidpop.dict.Translator;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordCategory.WordCategoryConfig;
import com.droidpop.dict.WordEntry;


public class YouDaoTranslator extends Translator {
	public YouDaoTranslator() {
		super(new YouDaoJsonParser());
	}

	public static class CategoryConfig implements WordCategoryConfig {
		
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
	
	private static final String KEY_FROM = "libaier";
	private static final String KEY = "165709530";
	private static final String DOC_TYPE = "json";
    private static final String URL_BASE;
    static {
    	StringBuilder sb = new StringBuilder("http://fanyi.youdao.com/openapi.do?keyfrom=");
    	sb.append(KEY_FROM);
    	sb.append("&key=");
    	sb.append(KEY);
    	sb.append("&type=data&doctype=");
    	sb.append(DOC_TYPE);
    	sb.append("&version=1.1&q=");
    	URL_BASE = sb.toString();
    }

	private static final String HTTP_METHOD = "GET"; // YouDao APIv1.1
    private static final int TIMEOUT = 10000; // ms

	@Override
	public WordEntry autoTranslate(String text, String to) {
		StringBuilder url = new StringBuilder(URL_BASE);
		url.append(text);
		
		DroidPop.log(DroidPop.LEVEL_VERBOSE, url.toString());
		
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url.toString())
					.openConnection();
			try {
				conn.setRequestMethod(HTTP_METHOD);
				conn.setConnectTimeout(TIMEOUT);
				conn.setDoInput(true);
				
				YouDaoJsonParser parser = (YouDaoJsonParser) mReader;
				return parser.parse(conn.getInputStream());
			} finally {
				conn.disconnect();
			}
		} catch (SocketTimeoutException e) {
			DroidPop.log(DroidPop.LEVEL_WARN,
					"time out and check network status!");
		} catch (EntryParseException e) {
			DroidPop.log(DroidPop.LEVEL_WARN, e.getStatus().name(),
					" parse failed, ", e.getMessage());
		} catch (Exception e) {
			DroidPop.log(DroidPop.LEVEL_ERROR, e);
		}
        
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
