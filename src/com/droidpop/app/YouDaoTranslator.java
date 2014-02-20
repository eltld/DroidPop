package com.droidpop.app;

import java.io.InputStream;

import com.droidpop.dict.Translator;

public class YouDaoTranslator implements Translator {
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

	@Override
	public InputStream autoTranslate(String text, String to) {
		StringBuilder url = new StringBuilder(URL_BASE);
		url.append(text);
		
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
