package com.droidpop.ocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;

import junit.framework.Assert;
import me.wtao.io.FileUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import com.droidpop.app.DroidPop;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel;

public class TessTwoAdapter extends OcrAdapter {

	private final int MEAN_CONFIDENCE_THREHOLD = 80;

	// never modify static string value below if you don't understand what you do
	private static final String TESSERACT_OCR = "tesseract-ocr";
	private static final String DEFAULT_LANG = "eng";
	private static final String TESS_DATA = TESSERACT_OCR + "/tessdata/";
	private static final String DEFAULT_LANG_PACKAGE = "tess2/tesseract-ocr.eng.zip";
	private static final String FILE_FORMAT = ".traineddata";
	
	private Context mContext;
	private String mLanguage;

	private TessBaseAPI baseApi;
	private Rect mRegionRect;
	

	public TessTwoAdapter(Context context) {
		this(context, null);
	}

	/**
	 * 
	 * @param lang not used but reserved
	 */
	public TessTwoAdapter(Context context, String lang) {
		mContext = context;
		mLanguage = DEFAULT_LANG;
		
		if (!checkEnvironment()) {
			try {
				init();
			} catch (FileNotFoundException e) {
				DroidPop.log(DroidPop.LEVEL_WARN, e.getMessage(), " not found!");
			} catch (Exception e) {
				DroidPop.log(DroidPop.LEVEL_ERROR, e);
			}
		}
		
		File tess2Dir = new File(getOcrDir(), TESSERACT_OCR);
		String datapath = tess2Dir.getAbsolutePath();

		baseApi = new TessBaseAPI();
		baseApi.setDebug(true); // TODO: false when release
		if (!baseApi.init(datapath, mLanguage, TessBaseAPI.OEM_TESSERACT_ONLY)) {
			DroidPop.log(DroidPop.LEVEL_ERROR,
					"initialize tesseract engine failed");
		}
		mRegionRect = new Rect();
	}

	public boolean checkEnvironment() {
		try {
			File ocrDir = getOcrDir();
			File tess2Dir = new File(ocrDir, TESS_DATA);
			LanguagePackageFilter filter = new LanguagePackageFilter(mLanguage);
			String[] ls = tess2Dir.list(filter);
			return (ls.length == 1);
		} catch (Exception e) {
			return false;
		}
	}

	private void init() throws FileNotFoundException {
		File ocrDir = getOcrDir();
		
		File tessdataDir = new File(ocrDir, TESS_DATA);
		if (!(tessdataDir.mkdirs() || tessdataDir.isDirectory())) {
			throw new FileNotFoundException(tessdataDir.getAbsolutePath());
		}

		LanguagePackageFilter filter = new LanguagePackageFilter(mLanguage);
		String[] ls = tessdataDir.list(filter);
		if(ls.length == 0) {
			// not found, copy
			Context context = mContext.getApplicationContext();
			StringBuilder sb = new StringBuilder(context.getCacheDir().getAbsolutePath());
			sb.append("/lang.zip");
			final String path = sb.toString();
			
			FileUtils.copyFromAssets(mContext, path, DEFAULT_LANG_PACKAGE);
			
			File tmpZip = new File(path);
			if (tmpZip.exists()) {
				FileUtils.unzip(ocrDir.getAbsolutePath(), path);
				ls = tessdataDir.list(filter);
			} else {
				throw new FileNotFoundException(path);
			}
		}
		
		File langFile = null;
		if(ls.length == 1){
			// find the only one
			Assert.assertEquals(1, ls.length);
			langFile = new File(tessdataDir, ls[0]);
		}
		
		if (langFile == null || !langFile.exists()) {
			throw new FileNotFoundException(langFile.getAbsolutePath());
		}
	}

	@Override
	public boolean recognize(Bitmap bitmap) {
		baseApi.clear();
		baseApi.setImage(bitmap);
		initRegionRect();
		ResultIterator iter = baseApi.getResultIterator();
		return (iter != null && iter.next(PageIteratorLevel.RIL_SYMBOL));
	}

	@Override
	public boolean isConfidence() {
		return (baseApi.meanConfidence() < MEAN_CONFIDENCE_THREHOLD);
	}

	/**
	 * @param point
	 *            the first touch point on screen
	 * @param encode
	 *            ignored, default return UTF-8 text
	 */
	@Override
	public String getText(Point point, String encode) {
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
	
	@Override
	protected Context getContext() {
		return mContext;
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
	
	private class LanguagePackageFilter implements FilenameFilter {

		private final String mLanguage;
		
		public LanguagePackageFilter(String target) {
			StringBuilder sb = new StringBuilder(target).append(FILE_FORMAT);
			mLanguage = sb.toString();
		}
		
		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(mLanguage);
		}
		
	};

}
