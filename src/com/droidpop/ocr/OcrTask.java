package com.droidpop.ocr;

import java.io.File;

import junit.framework.Assert;
import android.content.Context;
import android.os.AsyncTask;

public class OcrTask extends AsyncTask<String, Integer, Boolean> {

	private Context mContext;
	private OcrAdapter mOcrAdapter;

	public OcrTask(Context context) {
		this(context, new TessTwoAdapter(context));
	}

	public OcrTask(Context context, OcrAdapter ocrAdapter) {
		mContext = context;
		mOcrAdapter = ocrAdapter;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		if (!mOcrAdapter.checkEnv()) {
			return false;
		}

		Assert.assertEquals("Assert: " + OcrTask.class.getCanonicalName()
				+ " handle one task per time", params.length == 1);
		String path = params[0];

		mOcrAdapter.loadImage(new File(path));

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
	
}
