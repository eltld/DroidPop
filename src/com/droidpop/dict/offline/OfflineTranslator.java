package com.droidpop.dict.offline;

import me.wtao.utils.Log;

import android.content.Context;

import com.droidpop.dict.Translator;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntryReader;

public abstract class OfflineTranslator extends Translator {
	
	private static final String TAG = "OfflineTranslator";
	
	protected final Context mContext;
	private AbstractDBHelper mDbHelper;
	
	protected OfflineTranslator(Context context, WordEntryReader reader) {
		super(reader);
		mContext = context;
		mDbHelper = getDbHelper();
	}
	
	public WordEntry translte(String text) {
		final int id = mDbHelper.getWordId(text);
		if (id != -1) {
			WordEntry entry = mDbHelper.getWordEntry(id);
			
			if(entry == null) {
				Log.d(TAG, "word entry is null");
			} else if(!entry.isValid()) {
				Log.d(TAG, "word entry is dirty");
			} else {
				Log.d(TAG, "query: ", text,
						", word: ", entry.getWord(),
						", paraphrase size: ", entry.getParaphrases().size());
			}
			
			return entry;
		} else {
			return null;
		}
	}

	@Override
	public WordEntry autoTranslate(String text, String to) {
		return translte(text);
	}

	@Override
	public WordEntry manualTranslate(String text, String from, String to) {
		return translte(text);
	}
	
	@Override
	public boolean isDefualtAutoDetect() {
		return true;
	}
	
	protected abstract AbstractDBHelper getDbHelper();

}
