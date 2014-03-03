package com.droidpop.app;

import java.util.Set;

import junit.framework.Assert;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

public final class PreferenceSettingsManager {
	// TYPE_BOOLEAN below
	public static final int SHOTCUT;
	
	private static final int TYPE_MASK = 0xf0000000;
	private static final int TYPE_BOOLEAN = 0x10000000;
	private static final int TYPE_FLOAT = 0x20000000;
	private static final int TYPE_INT = 0x30000000;
	private static final int TYPE_LONG = 0x40000000;
	private static final int TYPE_STRING = 0x50000000;
	private static final int TYPE_STRING_SET = 0x60000000;
	
	private static final SparseArray<String> sKeyMap;
	private static final SparseArray<Object> sDefaultMap;
	static {
		sKeyMap = new SparseArray<String>();
		sDefaultMap = new SparseArray<Object>();
		int autoincr;
		
		// init TYPE_BOOLEAN
		autoincr = 1;
		SHOTCUT = TYPE_BOOLEAN | (autoincr++);
		
		sKeyMap.put(SHOTCUT, "shotcut");
		sDefaultMap.put(SHOTCUT, false);
		// end init TYPE_BOOLEAN
	}
	
	
	private static final SharedPreferences sDefaultPreferences;
	static {
		sDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(DroidPop.getApplicationContext());
	}
	
	private static boolean sBatch = false;
	private static SharedPreferences.Editor sBatchEditor = null;
	
	public static boolean hasCreatedShotcut() {
		return (Boolean) get(SHOTCUT);
	}
	
	public static SharedPreferences getSharedPreferences() {
		return sDefaultPreferences;
	}
	
	public static Object get(int keyId) {
		final String key = sKeyMap.get(keyId);
		final Object defaultValue = sDefaultMap.get(keyId);
		
		switch (keyId & TYPE_MASK) {
		case TYPE_BOOLEAN:
			return sDefaultPreferences.getBoolean(key, (Boolean) defaultValue);
		case TYPE_FLOAT:
			return sDefaultPreferences.getFloat(key, (Float) defaultValue);
		case TYPE_INT:
			return sDefaultPreferences.getInt(key, (Integer) defaultValue);
		case TYPE_LONG:
			return sDefaultPreferences.getLong(key, (Long) defaultValue);
		case TYPE_STRING:
			return sDefaultPreferences.getString(key, (String) defaultValue);
		case TYPE_STRING_SET:
			return sDefaultPreferences.getStringSet(key, (Set<String>) defaultValue);
		default:
			return defaultValue;
		}
	}
	
	public static synchronized void prepare() {
		sBatch = true;
		if (sBatchEditor == null) {
			sBatchEditor = sDefaultPreferences.edit();
		} else {
			sBatchEditor.clear();
		}
	}

	public synchronized static boolean add(int keyId, Object value) {
		return set(keyId, value, false);
	}
	
	public static synchronized boolean batchCommit() {
		try {
			return (sBatchEditor != null) && sBatchEditor.commit();
		} finally {
			sBatch = false;
		}
	}
	
	public synchronized static boolean set(int keyId, Object value) {
		return set(keyId, value, true);
	}
	
	protected static boolean set(int keyId, Object value, boolean immediate) {
		Assert.assertEquals("You should prepare(), add(), and use batchCommit().", !immediate, sBatch);

		SharedPreferences.Editor editor;
		
		if (immediate) {
			editor = sDefaultPreferences.edit();
		} else {
			editor = sBatchEditor;
		}
		
		final String key = sKeyMap.get(keyId);
		
		switch (keyId & TYPE_MASK) {
		case TYPE_BOOLEAN:
			editor.putBoolean(key, (Boolean) value);
			break;
		case TYPE_FLOAT:
			editor.putFloat(key, (Float) value);
			break;
		case TYPE_INT:
			editor.putInt(key, (Integer) value);
			break;
		case TYPE_LONG:
			editor.putLong(key, (Long) value);
			break;
		case TYPE_STRING:
			editor.putString(key, (String) value);
			break;
		case TYPE_STRING_SET:
			editor.putStringSet(key, (Set<String>) value);
			break;
		}
		
		if(immediate) {
			return editor.commit();
		} else {
			return true;
		}
	}
	
	private PreferenceSettingsManager() {
		
	}
	
}
