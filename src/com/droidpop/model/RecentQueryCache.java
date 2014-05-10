package com.droidpop.model;

import java.util.LinkedList;

import android.content.Context;

import com.droidpop.dict.WordEntry;

public class RecentQueryCache {
	
	public static class  RecentQuery {
		
		public long queryMillis = -1;
		public String query = null;
		public WordEntry result = null;
		
		public RecentQuery(long queryMillis, String query, WordEntry result) {
			this.queryMillis = queryMillis;
			this.query = query;
			if (result != null && result.isValid()) {
				this.result = result;
			}
		}
	}
	
	private static RecentQueryCache sRecentQueryCache;
	
	private final Context mContext;
	private LinkedList<RecentQuery> mContainer;
	
	public static synchronized RecentQueryCache getRecentQueryCache(Context context) {
		if(null == sRecentQueryCache) {
			sRecentQueryCache = new RecentQueryCache(context);
		}
		
		return sRecentQueryCache;
	}
	
	public int size() {
		return mContainer.size();
	}
	
	public RecentQuery getRecentQueryByIndex(int index) {
		try {
			return mContainer.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public boolean addRecentItem(RecentQuery item) {
		return mContainer.add(item);
	}
	
	private RecentQueryCache(Context context) {
		mContext = context;
		mContainer = new LinkedList<RecentQueryCache.RecentQuery>();
	}
	
}
