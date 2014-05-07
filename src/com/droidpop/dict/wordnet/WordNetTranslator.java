package com.droidpop.dict.wordnet;

import java.util.Locale;

import android.content.Context;

import com.droidpop.dict.offline.AbstractDBHelper;
import com.droidpop.dict.offline.OfflineTranslator;

public class WordNetTranslator extends OfflineTranslator {

	public WordNetTranslator(Context context) {
		super(context, new WordNetDBHelper(context));
	}
	
	@Override
	public String getLocale() {
		return Locale.US.getLanguage();
	}

	@Override
	protected AbstractDBHelper getDbHelper() {
		return new WordNetDBHelper(mContext);
	}

}
