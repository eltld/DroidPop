package com.droidpop.dict;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.droidpop.app.DroidPop;
import com.droidpop.dict.WordCategory.WordCategoryConfig;


public class YouDaoTranslator implements Translator {
	public static class CategoryConfig implements WordCategoryConfig {
		private static final HashMap<String, Integer> sConfigMap;
		static {
			sConfigMap = new HashMap<String, Integer>();
			
			sConfigMap.put("n.", WordCategory.NOUN);
			sConfigMap.put("vt.", WordCategory.VERB);
			sConfigMap.put("vi.", WordCategory.VERB_INTRANSITIVE);
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
    	sb.append("xml&version=1.1&q=");
    	URL_BASE = sb.toString();
    }
    
    private static final int TIMEOUT = 10000; // ms

	@Override
	public InputStream autoTranslate(String text, String to) {
		StringBuilder url = new StringBuilder(URL_BASE);
		url.append(text);
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url.toString())
					.openConnection();
			try {
				conn.setConnectTimeout(TIMEOUT);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				return new BufferedInputStream(conn.getInputStream());
			} finally {
				conn.disconnect();
			}
		} catch (SocketTimeoutException e) {
			DroidPop.log(DroidPop.LEVEL_WARN,
					"time out and check network status!", e);
		} catch (Exception e) {
			DroidPop.log(DroidPop.LEVEL_ERROR, e);
		}
        
		return null;
	}

	@Override
	public boolean isDefualtAutoDetect() {
		return true;
	}

	@Override
	public InputStream manualTranslate(String text, String from, String to) {
		return autoTranslate(text, to);
	}

}
