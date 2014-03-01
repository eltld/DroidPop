package com.droidpop.ocr;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.droidpop.app.DroidPop;

public abstract class OcrAdapter {
	/**
	 * private directory using context.getDir(OCR_DIR, Context.MODE_PRIVATE).
	 * 
	 */
	protected static final String OCR_DIR = "ocr";

	/**
	 * specify the source of image to be extracted before other getter(f.e.
	 * {@link #getText(Point)}) method, it will auto clear up previous
	 * recognition results and any stored image data for security reasons.
	 * 
	 */
	public abstract boolean recognize(Bitmap bitmap);

	public abstract boolean isConfidence();

	/**
	 * {@link #getText(Point, String)} by UTF-8 encoding
	 * 
	 */
	public String getText(Point point) {
		return getText(point, "UTF-8");
	}

	/**
	 * using {@link #isConfidence()} to check recognized result's confidence
	 * 
	 * @param point
	 *            the first touch point on screen
	 * @return if success, get the recognized text; otherwise return null
	 */
	public abstract String getText(Point point, String encode);
	
	protected abstract Context getContext();
	
	protected File getOcrDir() {
		Context context = getContext().getApplicationContext();
		File ocrDir = context.getDir(OCR_DIR, Context.MODE_PRIVATE);
		if (!(ocrDir.mkdir() || ocrDir.isDirectory())) {
			DroidPop.log(DroidPop.LEVEL_WARN, ocrDir.getAbsolutePath(), " not created!");
		}
		return ocrDir;
	}
}