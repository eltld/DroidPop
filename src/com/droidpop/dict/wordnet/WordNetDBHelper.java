package com.droidpop.dict.wordnet;

import me.wtao.io.ExternalStorage;
import android.content.Context;

import com.droidpop.config.ApplicationConfig;
import com.droidpop.dict.offline.AbstractDBHelper;

public class WordNetDBHelper extends AbstractDBHelper {

	public WordNetDBHelper(Context context) {
		super(context);
	}

	public static final String DATABASE_FILE_NAME = "wordnet.db";

	@Override
	protected String getDBFilePath() {
		if (ApplicationConfig.DEBUG && ExternalStorage.isExternalStorageReadable()) {
			return "/sdcard/droidpop/database/wordnet.db";
		} else {
			return mPath;
		}
	}
	
}
