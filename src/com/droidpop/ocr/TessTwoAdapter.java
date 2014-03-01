package com.droidpop.ocr;

import java.io.File;
import java.util.ArrayList;

import me.wtao.io.FileUtils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessTwoAdapter implements OcrAdapter {

	private static final String DEFAULT_LANG = "eng";
	private static final String FILE_FORMAT = ".traineddata";

	// assets path within the apk
	private static final String TESS_DATA = "tessdata/";
	// external directory need check, so delayed initialized
	private final String OCR_DIR;

	private Context mContext;
	private String mLanguage = DEFAULT_LANG;

	private TessBaseAPI baseApi;
	private Rect mRegionRect;

	public TessTwoAdapter(Context context) {
		this(context, null);
	}

	public TessTwoAdapter(Context context, String lang) {
		mContext = context;

		// throw exception if external storage removable
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.e(TessBaseAPI.class.getCanonicalName(),
					"external storage not ready");
		}
		OCR_DIR = Environment.getExternalStorageDirectory().toString()
				+ "/droidbubble/tess2/";

		if (lang != null) {
			mLanguage = lang;
		}

		if (!checkEnv()) {
			if (!initEnv()) {
				Log.e(TessBaseAPI.class.getCanonicalName(),
						"environment initialization failed");
			}
		}

		baseApi = new TessBaseAPI();
		baseApi.setDebug(true); // false when release
		if (!baseApi.init(OCR_DIR, mLanguage, TessBaseAPI.OEM_TESSERACT_ONLY)) {
			Log.e(TessBaseAPI.class.getCanonicalName(),
					"initialize tesseract engine failed");
		}
		mRegionRect = new Rect();
	}

	@Override
	public boolean checkEnv() {
		String langPath = OCR_DIR + TESS_DATA + mLanguage + FILE_FORMAT;

		return new File(langPath).exists();
	}

	@Override
	public boolean initEnv() {
		File dir = new File(OCR_DIR + TESS_DATA);
		if (!(dir.mkdirs() || dir.isDirectory())) {
			Log.e(TessTwoAdapter.class.getCanonicalName(),
					"assert that should not reach here");
		}

		final String langPkg = TESS_DATA + mLanguage + FILE_FORMAT;
		final String langPath = OCR_DIR + langPkg;
		if (!(new File(langPath).exists())) {
			FileUtils.copy(mContext, langPath, langPkg);
		}

		return checkEnv();
	}

	@Override
	public boolean loadImage(File file) {
		baseApi.clear();
		baseApi.setImage(file);
		initRegionRect();
		return (baseApi.meanConfidence() < MEAN_CONFIDENCE_THREHOLD);
	}

	@Override
	public String getUTF8Text(Point point) {
		ArrayList<Rect> wordsRects = baseApi.getWords().getBoxRects();

		String recognizedText = null;

		Rect rect = null;
		int cnt = wordsRects.size();
		for (int i = 0; i < cnt; i++) {
			rect = wordsRects.get(i);
			if (rect.contains(point.x, point.y)) {
				baseApi.setRectangle(rect);
				recognizedText = baseApi.getUTF8Text();
				recognizedText = recognizedText
						.replaceAll("[^a-zA-Z0-9]+", " ");

				baseApi.setRectangle(mRegionRect);
				
				return recognizedText;
			}
		}

		return null;
	}

	private void initRegionRect() {
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;

		ArrayList<Rect> regionRects = baseApi.getRegions().getBoxRects();
		Rect rect = null;
		int cnt = regionRects.size();

		for (int i = 0; i != cnt; i++) {
			rect = regionRects.get(i);

			left = Math.min(left, rect.left);
			right = Math.max(right, rect.right);
			top = Math.min(top, rect.top);
			bottom = Math.max(bottom, rect.bottom);
		}

		mRegionRect.set(left, top, right, bottom);
	}

}
