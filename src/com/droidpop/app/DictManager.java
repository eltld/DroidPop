package com.droidpop.app;

import android.content.Context;

import com.droidpop.dict.Translator;
import com.droidpop.dict.wordnet.WordNetTranslator;
import com.droidpop.dict.youdao.YouDaoTranslator;

public class DictManager {
	
	private final Context mContext;

	public static DictManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public Translator getOnlineTranslator() {
		return new YouDaoTranslator();
	}
	
	public Translator getOfflineTranslator() {
		return new WordNetTranslator(mContext);
	}
	
	private DictManager(Context context) {
		mContext = context;
	}

	private static class SingletonHolder {
		private static final DictManager INSTANCE = new DictManager(DroidPop.getApplicationContext());
	}

}
