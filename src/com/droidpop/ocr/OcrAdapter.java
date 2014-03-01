package com.droidpop.ocr;

import java.io.File;

import android.graphics.Point;

public interface OcrAdapter {
	/**
	 * it's considered usable when mean confidence (between 0 and 100) is
	 * greater than {@value #MEAN_CONFIDENCE_THREHOLD}
	 */
	public final int MEAN_CONFIDENCE_THREHOLD = 80;

	/**
	 * it's recommend check it within {@link #checkEnv()}
	 * 
	 * @return true if and only if environment initialization all ready
	 */
	public boolean checkEnv();

	/**
	 * initialized first before using other methods
	 * 
	 * @return true when everything runs OK; otherwise false
	 */
	public boolean initEnv();

	/**
	 * specify the source of image to be extracted before other getter(f.e.
	 * {@link #getUTF8Text(Point)}) method, it will auto clear up previous
	 * recognition results and any stored image data for security reasons.
	 * 
	 * @param file
	 *            instanced by you, for security you'd better check that it does
	 *            canRead
	 * @return true if and only if mean confidence is greater than
	 *         {@link #MEAN_CONFIDENCE_THREHOLD}, but in false case, you can
	 *         still go on and get the recognized text, which may be incorrect.
	 */
	public boolean loadImage(File file);

	/**
	 * 
	 * @param point
	 *            the first touch point on screen
	 * @return if success, get the recognized text in UTF8 format; otherwise
	 *         return null
	 */
	public String getUTF8Text(Point point);
}