package com.droidpop.ocr;

import java.io.File;
import java.util.ArrayList;

import me.wtao.io.ExternalStorage;
import me.wtao.view.PointerFactory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
import android.view.PointerIcon;

import com.droidpop.R;
import com.droidpop.app.DroidPop;
import com.droidpop.config.ApplicationConfig;

public abstract class OcrAdapter {
	/**
	 * private directory using context.getDir(OCR_DIR, Context.MODE_PRIVATE).
	 * 
	 */
	private static final String OCR_DIR = "data";
	
	protected static Rect sTouchBound;
	
	protected final Context mContext;
	
	public OcrAdapter(Context context) {
		mContext = context;
		
		if (sTouchBound == null) {
			synchronized (OcrAdapter.class) {
				if (sTouchBound == null) {
					PointerFactory factory = new PointerFactory(context);
					PointerIcon icon = factory.createDefaultPointer();
					Bitmap bitmap = icon.getBitmap();
					
					// origin point is (getHotSpotX(), getHotSpotY())
					final int left = (int) -icon.getHotSpotX();
					final int top = (int) -icon.getHotSpotY();
					final int right = left + bitmap.getWidth();
					final int bottom = top + bitmap.getHeight();
					
					sTouchBound = new Rect(left, top, right, bottom);
				}
			}
		}
	}
	
	public Context getContext() {
		return mContext;
	}

	/**
	 * specify the source of image to be extracted before other getter(f.e.
	 * {@link #getText(int, String)}) method, it will auto clear up previous
	 * recognition results and any stored image data for security reasons.
	 * 
	 * @param bitmap
	 *            the source of image to be extracted
	 * @param points
	 *            the touch points on screen
	 */
	public abstract boolean recognize(Bitmap bitmap, Point... points);

	public abstract boolean isConfidence();

	/**
	 * using {@link #isConfidence()} to check recognized result's confidence
	 * 
	 * @param index
	 * @return if success, get the recognized text; otherwise return null
	 */
	public abstract String getText(int index, String encode);
	
	/**
	 * {@link #getText(int, String)} by UTF-8 encoding
	 * 
	 */
	public String getText() {
		return getText(0, "UTF-8");
	}
	
	protected File getOcrDir() {
		Context context = mContext.getApplicationContext();
		
		File ocrDir = null;
		if(ApplicationConfig.DEBUG) {
			// unzip tesseract-ocr.eng.zip to external storage and remove it from assets/
			// to compressing the .apk size from 10+ MB to fewer size f.e. 2M, and fast debug
			if(ExternalStorage.isExternalStorageWritable()) {
				final String droidpop = context.getResources().getString(R.string.app_name);
				ocrDir = ExternalStorage.getExternalStorageDirectory(droidpop);
			}
		}
		
		if(ocrDir == null) {
			ocrDir = context.getDir(OCR_DIR, Context.MODE_PRIVATE);
		}
		
		if (!(ocrDir.mkdir() || ocrDir.isDirectory())) {
			DroidPop.log(DroidPop.LEVEL_WARN, ocrDir.getAbsolutePath(), " not created!");
		}
		return ocrDir;
	}

	protected static Rect getTargetRect(Point touchPoint, ArrayList<Rect> rects) {
		long costMillis = System.currentTimeMillis();
		DroidPop.debug("getTargetRect...");
		
		Rect touchRect = getTouchRect(touchPoint);
		
		ArrayList<Pair<Integer, Rect>> overlaps = new ArrayList<Pair<Integer,Rect>>();
		for(Rect rect : rects) {
			Integer overlap = getOverlapArea(touchRect, rect);
			if(overlap != null) {
				overlaps.add(Pair.create(overlap, rect));
			}
		}
		
		Integer max = Integer.MIN_VALUE;
		Integer min = Integer.MAX_VALUE;
		Rect target = null;
		boolean update = false;
		
		final int len = overlaps.size();
		for(int i = 0; i != len; ++i) {
			Integer area = overlaps.get(i).first;
			if(area > max) {
				update = true;
			} else if(area == max) {
				if(getDisdence(touchPoint, overlaps.get(i).second) < min) {
					update = true;
				}
			}
			
			if(update) {
				max = area;
				min = getDisdence(touchPoint, overlaps.get(i).second);
				target = overlaps.get(i).second;
				update = false;
			}
		}
		
		if(target != null) {
			DroidPop.debug(target);
		}

		costMillis = System.currentTimeMillis() - costMillis;
		DroidPop.debug("cost ", costMillis, " ms");
		
		return target;
	}
	
	private static Rect getTouchRect(Point touchPoint) {
		Rect rect = new Rect(touchPoint.x + sTouchBound.left,
				touchPoint.y + sTouchBound.top,
				touchPoint.x + sTouchBound.right,
				touchPoint.y + sTouchBound.bottom);
		
		DroidPop.debug(rect);
		
		return rect;
	}
	
	private static Integer getOverlapArea(Rect touchRect, Rect rect) {
		if(touchRect.left >= rect.right) {
			return null; // rect entirely at left side
		}
		if(touchRect.top >= rect.bottom) {
			return null; // rect entirely above
		}
		if(touchRect.right <= rect.left) {
			return null; // rect entirely at right side
		}
		if(touchRect.bottom <= rect.top) {
			return null; // rect entirely below
		}
		
		// other cases, overlap
		Rect overlap = new Rect();
		overlap.left = Math.max(touchRect.left, rect.left);
		overlap.top = Math.max(touchRect.top, rect.top);
		overlap.right = Math.min(touchRect.right, rect.right);
		overlap.bottom = Math.min(touchRect.bottom, rect.bottom);
		
		final int area = overlap.width() * overlap.height();
		DroidPop.debug(overlap, ", area: ", area);

		return area;
	}
	
	private static int getDisdence(Point touchPoint, Rect rect) {
		Point centerHotSpot = getCenterHotSpot(rect);
		return Math.abs(touchPoint.x - centerHotSpot.x)
				+ Math.abs(touchPoint.y - centerHotSpot.y);
	}
	
	private static Point getCenterHotSpot(Rect rect) {
		Point center = new Point();
		center.x = rect.centerX();
		center.y = rect.centerY();
		return center;
	}
}