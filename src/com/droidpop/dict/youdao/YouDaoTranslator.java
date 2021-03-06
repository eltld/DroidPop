package com.droidpop.dict.youdao;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import com.droidpop.app.DroidPop;
import com.droidpop.dict.WordEntryException;
import com.droidpop.dict.Translator;
import com.droidpop.dict.WordEntry;


public class YouDaoTranslator extends Translator {
	public YouDaoTranslator() {
		super(new YouDaoJsonParser());
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
		} catch (WordEntryException e) {
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
