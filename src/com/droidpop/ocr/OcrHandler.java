package com.droidpop.ocr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.wtao.utils.Log;
import me.wtao.utils.ScreenMetrics;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCapManager;
import com.droidpop.app.ScreenCapManager.ScreenCapTaskDispatcher;
import com.droidpop.view.OnScreenTouchListener;

public class OcrHandler implements OnScreenTouchListener,
		ScreenCapTaskDispatcher {

	private static final int WORD_LENGTH = 64; // px
	private static final int LINE_WIDTH = 32; // px

	private final Context mContext;
	
	private GestureDetector mDetector;
	
	private final ScreenCapManager mScreenCapManager;
	private final ScreenMetrics mScreenMetrics;
	
	private final OcrAdapter mOcr;
	
	private OnOcrRecognitionListener mListener;
	
	private Point mPoint;
	private long mCurrentMillis;
	
	public OcrHandler(Context context) {
		this(context, null);
	}

	public OcrHandler(Context context, OnOcrRecognitionListener listener) {
		mContext = context.getApplicationContext();
		
		mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mPoint = new Point((int) (event.getRawX() + 0.5f),
							(int) (event.getRawY() + 0.5f));
					mCurrentMillis = System.currentTimeMillis();
					
					mScreenCapManager.dispatch(OcrHandler.this);
				}
				
				return true;
			}
			
		});
		
		mScreenCapManager = (ScreenCapManager) DroidPop.getApplication()
				.getAppService(DroidPop.SCREEN_CAPTURE_SERVICE);
		mScreenMetrics = new ScreenMetrics(context);
		
		mOcr = new TessTwoAdapter(mContext);
		
		mListener = listener;
	}
	
	public void setOnOcrRecognitionListener(OnOcrRecognitionListener listener) {
		mListener = listener;
	}

	@Override
	public void onScreenTouch(MotionEvent event) {
		DroidPop.log(DroidPop.LEVEL_INFO, Log.shortFor(event));
		mDetector.onTouchEvent(event);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onDone(Bitmap screencap) {
		logCostMillis("screencap ok");
		
		mOcr.recognize(screencap);

		logCostMillis("ocr ok");

		String text = mOcr.getText(mPoint);
		DroidPop.debug(text);

		if(mListener != null) {
			mListener.onRecognized(text, mOcr.isConfidence());
			logCostMillis("translate ok");
		}
	}

	@Override
	public void onCancelled(String msg) {

	}

	@Override
	public Rect getBound() {
		if (mListener != null && mListener.isRapidOcr()) {
			mScreenMetrics.messure();
			
			// TODO:
			int left = mPoint.x - WORD_LENGTH;
			int right = Math.min(mPoint.y + WORD_LENGTH, mScreenMetrics.getWidth());
			int top = mPoint.y - LINE_WIDTH;
			int bottom = Math.min(mPoint.y + LINE_WIDTH, mScreenMetrics.getHeight());
			
			if(left < 0) {
				left = 0;
			}
			if(top < 0) {
				top = 0;
			}
			
			Rect bound = new Rect(left, top, right, bottom);
			mPoint.x = bound.centerX() - left; // relative X
			mPoint.y = bound.centerY() - top; //relative Y
			
			return bound;
		} else {
			return null;
		}
	}

	private void logCostMillis(String tag) {
		long diff = System.currentTimeMillis() - mCurrentMillis;
		mCurrentMillis = System.currentTimeMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS", Locale.CHINA);
		Date cost = new Date(diff);
		DroidPop.debug(tag, ": ", sdf.format(cost));
	}

}
