package com.droidpop.dict;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.droidpop.dict.EntryParseException.Status;

public class WordEntry {
	
	public static class Paraphrase {
		private final int mCategory;
		private String mDetail;
		private ArrayList<String> mDemos;
		
		public Paraphrase(int category) {
			this(category, null);
		}
		
		public Paraphrase(int category, String detail) {
			mCategory = category;
			mDetail = detail;
			mDemos = new ArrayList<String>();
		}
		
		public int getCategory() {
			return mCategory;
		}
		
		public String getDetail() {
			return mDetail;
		}
		
		public void setDetail(String detail) {
			mDetail = detail;
		}
		
		public boolean hasDemo() {
			return !mDemos.isEmpty();
		}
		
		public ArrayList<String> getDemos() {
			return mDemos;
		}
		
		public boolean addDemo(String demo) {
			return mDemos.add(demo);
		}
		
		@Override
		public String toString() {
			return getDetail();
		}
	}

	private EntryParseException.Status mStatus;
	private String mWord;
	private String mPhoneticSymbol;
	private ArrayList<Paraphrase> mParaphrases;
	
	public WordEntry() {
		mStatus = null;
	}

	public boolean isValid() {
		return (mStatus == null);
	}
	
	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status status) {
		mStatus = status;
	}

	public String getWord() {
		return mWord;
	}

	public void setWord(String word) {
		mWord = word;
	}
	
	public boolean hasPhoneticSymbol() {
		return (mPhoneticSymbol != null);
	}

	public String getPhoneticSymbol() {
		return mPhoneticSymbol;
	}

	public void setPhoneticSymbol(String phoneticSymbol) {
		mPhoneticSymbol = phoneticSymbol;
	}
	
	public Paraphrase getBasicParaphrase() {
		if(mParaphrases == null || mParaphrases.isEmpty()) {
			return null;
		} else {
			return mParaphrases.get(indexOfBasicParaphrase());
		}
	}

	public ArrayList<Paraphrase> getParaphrases() {
		return mParaphrases;
	}

	public void setParaphrases(ArrayList<Paraphrase> paraphrases) {
		mParaphrases = paraphrases;
	}

	public Paraphrase getParaphraseBy(int category) {
		
		for(Paraphrase paraphrase : mParaphrases) {
			if(paraphrase.getCategory() == category) {
				return paraphrase;
			}
		}
		
		return null;
	}
	
	public ArrayList<WeakReference<Paraphrase>> getParaphrasesBy(int category) {
		ArrayList<WeakReference<Paraphrase>> subRefs = new ArrayList<WeakReference<Paraphrase>>();
		for(Paraphrase paraphrase : mParaphrases) {
			if(paraphrase.getCategory() == category) {
				subRefs.add(new WeakReference<Paraphrase>(paraphrase));
			}
		}
		return subRefs;
	}
	
	protected int indexOfBasicParaphrase() {
		return 0;
	}

}
