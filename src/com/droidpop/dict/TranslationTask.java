package com.droidpop.dict;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

public class TranslationTask extends AsyncTask<String, Integer, WordEntry> {
	public static interface OnTranslateListener {
		public void onTranslated(WordEntry entry, Status state);
	}

	public static enum Status {
		FINISHED, PENDING, RUNNING, CANCELLED
	};

	private static final Map<AsyncTask.Status, Status> sStatusMap;
	static {
		sStatusMap = new HashMap<AsyncTask.Status, Status>();
		sStatusMap.put(AsyncTask.Status.FINISHED, Status.FINISHED);
		sStatusMap.put(AsyncTask.Status.PENDING, Status.PENDING);
		sStatusMap.put(AsyncTask.Status.RUNNING, Status.RUNNING);
	}

	private Translator mTranslator;
	private OnTranslateListener mListener;
	private boolean mAutoCheck;

	public TranslationTask(Translator translator, OnTranslateListener listener) {
		this(translator, listener, translator.isDefualtAutoDetect());
	}

	public TranslationTask(Translator translator, OnTranslateListener listener,
			boolean auto) {
		mTranslator = translator;
		mListener = listener;
		mAutoCheck = auto;
	}
	
	public void translate(String text) {
		execute(text, mTranslator.getLocale());
	}
	
	public void translate(String text, String to) {
		execute(text, to);
	}
	
	public void translate(String text, String from, String to) {
		execute(text, from, to);
	}

	@Override
	protected WordEntry doInBackground(String... params) {
		WordEntry entry = null;

		if (params.length == 2 && mAutoCheck) {
			String text = params[0];
			String to = params[1];

			entry = mTranslator.autoTranslate(text, to);
		} else if (params.length == 3) {
			String text = params[0];
			String from = params[1];
			String to = params[2];

			entry = mTranslator.manualTranslate(text, from, to);
		}

		return entry;
	}

	@Override
	protected void onPostExecute(WordEntry result) {
		if (mListener != null) {
			mListener.onTranslated(result, sStatusMap.get(getStatus()));
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(WordEntry result) {
		if (mListener != null) {
			mListener.onTranslated(result, Status.CANCELLED);
		}
		super.onCancelled(result);
	}
}
