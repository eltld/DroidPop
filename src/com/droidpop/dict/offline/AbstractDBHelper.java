package com.droidpop.dict.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.wtao.io.ExternalStorage;
import me.wtao.io.FileUtils;
import me.wtao.utils.Log;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntryReader;

public abstract class AbstractDBHelper implements WordEntryReader {
	
	protected static final int ACTION_QUERY_BY_WORD_ID = 1;

	private static final String TAG = "AbstractDBHelper";
	private static final String ASSETS_DB_DIR = "db/";

	protected final Context mContext;
	protected String mPath;
	protected String mQuerySql;
	
	public AbstractDBHelper(Context context) {
		mContext = context;
		mPath = getDBFilePath();
		mQuerySql = null;
	}
	
	/**
	 * 
	 * @param flag not used but reserved
	 * @return
	 */
	public List<String> getColumnOfWord(int flag) {
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(
					mPath, null,
					SQLiteDatabase.OPEN_READONLY);
			if (db.isOpen()) {
				try {
					Cursor cursor = db.query(
							OfflineDBMetadata.Word.TABLE_NAME_WORD,
							new String[] { OfflineDBMetadata.Word.COLUMN_WORD },
							"", null, null, null, null);
					if (cursor.moveToFirst()) {
						ArrayList<String> vocabulary = new ArrayList<String>();
						int idxOfWord = cursor.getColumnIndex(OfflineDBMetadata.Word.COLUMN_WORD);
						do {
							vocabulary.add(cursor.getString(idxOfWord));
						} while(cursor.moveToNext());
						return vocabulary;
					}
				} finally {
					db.close();
				}
			}
		} catch (Exception e) {

		}

		return null;
	}
	
	public int getWordId(String text) {
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(
					mPath, null,
					SQLiteDatabase.OPEN_READONLY);
			if (db.isOpen()) {
				try {
					Cursor cursor = db.query(
							OfflineDBMetadata.Word.TABLE_NAME_WORD,
							new String[] { OfflineDBMetadata.Word.COLUMN_ID },
							OfflineDBMetadata.Word.COLUMN_WORD + " = ?",
							new String[] { text }, null, null, null);
					if (cursor.moveToFirst()) {
						return cursor.getInt(cursor.getColumnIndex(OfflineDBMetadata.Word.COLUMN_ID));
					}
				} finally {
					db.close();
				}
			}
		} catch (Exception e) {

		}

		return -1;
	}
	
	public WordEntry getWordEntry(int wordId) {
		WordEntry entry = null;
		
		if(mQuerySql == null) {
			mQuerySql = buildSql(ACTION_QUERY_BY_WORD_ID);
			if(mQuerySql == null) {
				Log.w(TAG, "query sql is null");
				return null;
			} else {
				Log.v(TAG, mQuerySql);
			}
		}
		
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(mPath, null,
					SQLiteDatabase.OPEN_READONLY);
			if (db.isOpen()) {
				try {
					entry = new WordEntry();
					Cursor cursor = db.rawQuery(mQuerySql,
							new String[] { String.valueOf(wordId) });
					
					// query word by id
					if (cursor.moveToFirst()) {
						entry.setWord(cursor.getString(cursor
								.getColumnIndex(OfflineDBMetadata.Word.COLUMN_WORD)));
						Log.d(TAG, entry.getWord());
					}
					
					ArrayList<WordEntry.Paraphrase> paraphrases = new ArrayList<WordEntry.Paraphrase>();
					int paraphraseId = -1;
					WordEntry.Paraphrase paraphrase = null;
					if (cursor.moveToFirst()) {
						int idxOfId = cursor.getColumnIndex(OfflineDBMetadata.Paraphrase.COLUMN_ID);
						int idxOfCategore = cursor.getColumnIndex(OfflineDBMetadata.Paraphrase.COLUMN_CATEGORE);
						int idxOfDetail = cursor.getColumnIndex(OfflineDBMetadata.Paraphrase.COLUMN_DETAIL);
						int idxOfDemo = cursor.getColumnIndex(OfflineDBMetadata.Demonstration.COLUMN_DEMONSTRATION);
						
						do {
							int id = cursor.getInt(idxOfId);
							if(paraphrase == null || paraphraseId != id) {
								paraphraseId = id;
								
								int category = cursor.getInt(idxOfCategore);
								String detail = cursor.getString(idxOfDetail);
								paraphrase = new WordEntry.Paraphrase(category, detail);
								paraphrases.add(paraphrase);
								
								android.util.Log.d(TAG, "   - " + paraphrase.getDetail());
							}
							
							if(paraphrase != null) {
								String demo = cursor.getString(idxOfDemo);
								if(demo != null) {
									paraphrase.addDemo(demo);
								}
								android.util.Log.d(TAG, "     - " + demo);
							}
						} while(cursor.moveToNext());
					}
					
					entry.setParaphrases(paraphrases);
					entry.setStatus(Status.SUCCESS);
				} finally {
					db.close();
				}
			}
		} catch (Exception e) {
			if (entry != null) {
				entry.setStatus(Status.ERROR_DIRTY_WORD);
			}
		}
		
		return entry;
	}
	
	protected String buildSql(int action) {
		String path = null;
		switch (action) {
		case ACTION_QUERY_BY_WORD_ID:
			path = "sql/query_by_word_id.sql";
			break;
		}
		
		if(path != null) {
			try {
				InputStream in = mContext.getAssets().open(path);
				BufferedReader sqlReader = new BufferedReader(new InputStreamReader(in));
				StringBuilder sqlBuilder = new StringBuilder();
				while(sqlReader.ready()) {
					sqlBuilder.append(sqlReader.readLine());
				}
				return sqlBuilder.toString();
			} catch (IOException e) {
				Log.w(TAG, e.toString());
			}
		}
		
		return null;
	}
	
	public boolean exportDBFile(String db, String dir, boolean external) {
		if(external) {
			if(ExternalStorage.isExternalStorageWritable()) {
				File target = ExternalStorage.getExternalStorageDirectory(dir);
				if(target != null) {
					FileUtils.copyFromAssets(mContext, new File(target, db).getAbsolutePath(),  ASSETS_DB_DIR + db);
					return true;
				}
			}
		} else {
			File target = mContext.getDatabasePath(db); // TODO:
			if(target != null) {
				FileUtils.copyFromAssets(mContext, target.getAbsolutePath(),  ASSETS_DB_DIR + db);
				return true;
			}
		}
		
		return false;
	}
	
	protected abstract String getDBFilePath();

}
