package com.droidpop.dict;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

public class TranslationTask extends AsyncTask<String, Integer, InputStream> {
	public static interface OnTranslateListener {
		public void onTranslated(InputStream result, int state);
	}
	
	public static final int FINISHED = 0;
	public static final int PENDING = 1;
	public static final int RUNNING = 2;
	public static final int CANCELLED = 3;
	
	private static final Map<Status, Integer> sStatusMap;
	static {
		sStatusMap = new HashMap<AsyncTask.Status, Integer>();
		sStatusMap.put(Status.FINISHED, FINISHED);
		sStatusMap.put(Status.PENDING, PENDING);
		sStatusMap.put(Status.RUNNING, RUNNING);
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
	
	@Override
	protected InputStream doInBackground(String... params) {
		InputStream in = null;
		
		if(params.length == 2 && mAutoCheck) {
			String text = params[0];
			String to = params[1];
			
			in = mTranslator.autoTranslate(text, to);
		} else if(params.length == 3 && !mAutoCheck) {
			String text = params[0];
			String from = params[1];
			String to = params[2];
			
			in = mTranslator.manualTranslate(text, from, to);
		}
		
		return in;
	}
	
	@Override
	protected void onPostExecute(InputStream result) {
		if(mListener != null) {
			mListener.onTranslated(result, sStatusMap.get(getStatus()));
		}
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled(InputStream result) {
		if(mListener != null) {
			mListener.onTranslated(result, CANCELLED);
		}
		super.onCancelled(result);
	}

}
