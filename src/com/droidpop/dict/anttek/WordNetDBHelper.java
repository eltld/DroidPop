package com.droidpop.dict.anttek;

import com.droidpop.dict.WordEntry;

import android.content.Context;

public class WordNetDBHelper extends AntTekDictHelper {

	public WordNetDBHelper(Context context) {
		super(context);
	}

	@Override
	protected String getDictDirPath() {
		return "/sdcard/DroidPop/dict/wordnet/";
	}

	@Override
	protected WordEntry parse(String text) {
		return null;
	}

}
