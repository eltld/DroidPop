package com.droidpop.dict.anttek;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.droidpop.dict.WordEntry;
import com.droidpop.dict.offline.LocalDatabaseHelper;

public abstract class AntTekDictHelper implements LocalDatabaseHelper {
	
	public static final String DEFAULT_ENCODE = "UFT-8";
	
	private static final String TAG = "AntTekDictHelper";
	private static final String INDEX_FILENAME = "index.db";
	private static final String DATA_FILENAME = "data.txt";
	
	protected final Context mContext;
	protected String mPath;
	protected String mIndexPath;
	protected String mDataPath;
	protected SQLiteDatabase mSQLiteDatabase;
	protected String mQuerySql;
	
	public AntTekDictHelper(Context context) {
		mContext = context;
		mPath = getDictDirPath();
		mIndexPath = mPath + INDEX_FILENAME;
		mDataPath = mPath + DATA_FILENAME;
		mQuerySql = null;
	}
	
	
	@Override
	public WordEntry getWordEntry(String query) {
		return parse(getText(query));
	}
	
	protected abstract String getDictDirPath();
	protected abstract WordEntry parse(String text);
	
	protected synchronized boolean openDatabase() {
		if(null == mSQLiteDatabase) {
			mSQLiteDatabase = SQLiteDatabase.openDatabase(
					mPath, null,
					SQLiteDatabase.OPEN_READONLY);
		}
		
		return mSQLiteDatabase.isOpen();
	}
	
	protected synchronized void closeDatabase() {
		if(null != mSQLiteDatabase) {
			mSQLiteDatabase.close();
			mSQLiteDatabase = null;
		}
	}
	
	private String getText(String query) {
		if(null == query) {
			return null;
		}
		
		Pair<Integer, Integer> location = getLocation(query);
		if(null == location) {
			Log.w(TAG, "query index failed");
			return null;
		}
		
		try {
			FileInputStream in = new FileInputStream(new File(mDataPath));
			try {
				int index = location.first;
				int length = location.second;
				byte[] buffer = new byte[length];
				in.read(buffer, index, length);
				
				return new String(buffer, DEFAULT_ENCODE);
			} finally {
				in.close();
			}
		} catch (Exception e) {
			Log.w(TAG, e.toString());
		}
		
		return null;
	}
	
	private Pair<Integer, Integer> getLocation(String query) {
		try {
			if(openDatabase()) {
				Cursor cursor = mSQLiteDatabase.query(
						AntTekDBMetadata.Dictionary.TABLE_NAME_WORD,
						new String[] { AntTekDBMetadata.Dictionary.COLUMN_INDEX, AntTekDBMetadata.Dictionary.COLUMN_LENGTH },
						AntTekDBMetadata.Dictionary.COLUMN_WORD + " = ?",
						new String[] { query }, null, null, null);
				if (cursor.moveToFirst()) {
					int index = cursor.getInt(cursor.getColumnIndex(AntTekDBMetadata.Dictionary.COLUMN_INDEX));
					int length = cursor.getInt(cursor.getColumnIndex(AntTekDBMetadata.Dictionary.COLUMN_LENGTH));
					return new Pair<Integer, Integer>(index, length);
				}
			}
		} catch (Exception e) {
			Log.w(TAG, e.toString());
		}

		return null;
	}
	
}
