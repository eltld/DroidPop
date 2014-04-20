package com.droidpop.dict.offline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import me.wtao.utils.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.droidpop.app.DroidPop;
import com.droidpop.dict.Translator;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntryReader;
import com.droidpop.dict.WordEntryReader.Status;

public class OfflineTranslator extends Translator {
	
	private static final String TAG = "OfflineTranslator";
	
	protected static final int ACTION_QUERY_BY_WORD_ID = 1;
	
	protected String mPath = "/sdcard/droidpop/database/wordnet.db";
	protected String mQuerySql = null;
	
	public OfflineTranslator(WordEntryReader reader) {
		super(reader);
	}
	
	public WordEntry translte(String text) {
		final int id = getWordId(text);
		if (id != -1) {
			WordEntry entry = getWordEntry(id);
			
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
	
	protected String buildSql(int action) {
		String path = null;
		switch (action) {
		case ACTION_QUERY_BY_WORD_ID:
			path = "sql/query_by_word_id.sql";
			break;
		}
		
		if(path != null) {
			try {
				InputStream in = DroidPop.getApplicationContext().getAssets().open(path);
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
	
	private int getWordId(String text) {
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
	
	private WordEntry getWordEntry(int wordId) {
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
	
//	private WordEntry getWordEntry(int wordId) {
//		WordEntry entry = null;
//		try {
//			SQLiteDatabase db = SQLiteDatabase.openDatabase(mPath, null,
//					SQLiteDatabase.OPEN_READWRITE);
//			if (db.isOpen()) {
//				try {
//					db.beginTransaction();
//
//					entry = new WordEntry();
//					Cursor cursor;
//					
//					// query word by id
//					cursor = db.query(OfflineDBMetadata.Word.TABLE_NAME_WORD,
//							new String[] { OfflineDBMetadata.Word.COLUMN_WORD },
//							OfflineDBMetadata.Word.COLUMN_ID + " = " + wordId,
//							null, null, null, null);
//					if (cursor.moveToFirst()) {
//						entry.setWord(cursor.getString(cursor
//								.getColumnIndex(OfflineDBMetadata.Word.COLUMN_WORD)));
//						Log.d(TAG, entry.getWord());
//					}
//					
//					// query paraphrase by word id
//					cursor= db.query(OfflineDBMetadata.Paraphrase.TABLE_NAME_PARAPHRASE,
//							new String[] {
//							OfflineDBMetadata.Paraphrase.COLUMN_ID,
//							OfflineDBMetadata.Paraphrase.COLUMN_CATEGORE,
//							OfflineDBMetadata.Paraphrase.COLUMN_DETAIL },
//							OfflineDBMetadata.Paraphrase.COLUMN_WORD_ID + " = " + wordId,
//							null, null, null, null);
//					ArrayList<WordEntry.Paraphrase> paraphrases = new ArrayList<WordEntry.Paraphrase>();
//					if (cursor.moveToFirst()) {
//						int idxOfId = cursor.getColumnIndex(OfflineDBMetadata.Paraphrase.COLUMN_ID);
//						int idxOfCategore = cursor.getColumnIndex(OfflineDBMetadata.Paraphrase.COLUMN_CATEGORE);
//						int idxOfDetail = cursor.getColumnIndex(OfflineDBMetadata.Paraphrase.COLUMN_DETAIL);
//						
//						Integer idxOfDemo = null;
//						
//						do {
//							int id = cursor.getInt(idxOfId);
//							int category = cursor.getInt(idxOfCategore);
//							String detail = cursor.getString(idxOfDetail);
//							WordEntry.Paraphrase paraphrase = new WordEntry.Paraphrase(category, detail);
//							
//							android.util.Log.d(TAG, "   - " + paraphrase.getDetail());
//							
//							// query demo by paraphrase id
//							Cursor demonCursor = db.query(OfflineDBMetadata.Demonstration.TABLE_NAME_DEMONSTRATION,
//											new String[] { OfflineDBMetadata.Demonstration.COLUMN_DEMONSTRATION },
//											OfflineDBMetadata.Demonstration.COLUMN_PARAPHRASE_ID + " = " + id,
//											null, null, null, null);
//							if(demonCursor.moveToFirst()) {
//								if(idxOfDemo == null) {
//									idxOfDemo = demonCursor.getColumnIndex(OfflineDBMetadata.Demonstration.COLUMN_DEMONSTRATION);
//								}
//								
//								do {
//									String demo = demonCursor.getString(idxOfDemo);
//									paraphrase.addDemo(demo);
//									android.util.Log.d(TAG, "     - " + demo);
//								} while(demonCursor.moveToNext());
//							}
//							
//							paraphrases.add(paraphrase);
//						} while(cursor.moveToNext());
//					}
//					
//					entry.setParaphrases(paraphrases);
//					entry.setStatus(Status.SUCCESS);
//				} finally {
//					db.endTransaction();
//					db.close();
//				}
//			}
//		} catch (Exception e) {
//			if (entry != null) {
//				entry.setStatus(Status.ERROR_DIRTY_WORD);
//			}
//		}
//
//		return entry;
//	}

}
