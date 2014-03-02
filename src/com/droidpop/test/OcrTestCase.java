package com.droidpop.test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.wtao.utils.ScreenMetrics;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.droidpop.app.DroidPop;
import com.droidpop.app.ScreenCapManager;
import com.droidpop.app.ScreenCapManager.ScreenCapTaskDispatcher;
import com.droidpop.ocr.OcrAdapter;
import com.droidpop.ocr.TessTwoAdapter;

public class OcrTestCase implements TestCase, OnClickListener, OnTouchListener {
	
	private final int mStatusBarHeight;
	private OcrAdapter mOcr;
	private View mTouchView;
	private ImageView mImageView;

	public OcrTestCase(Activity activity, ImageView imageView) {
		this((Context)activity, activity.findViewById(R.id.content), imageView);
	}
	
	public OcrTestCase(Context context, View v, ImageView imageView) {
		ScreenMetrics metrics = new ScreenMetrics(context);
		metrics.requestMessure();
		mStatusBarHeight = ScreenMetrics.getMeasuredStatusBarHeight();
		
		mOcr = new TessTwoAdapter(context);
		mTouchView = v;
		mImageView = imageView;
	}

	@Override
	public void setUp() {
		mTouchView.setOnTouchListener(this);
		mImageView.setOnClickListener(this);
	}

	@Override
	public void tearDown() {
		mTouchView.setOnTouchListener(null);
		mImageView.setOnClickListener(null);
	}

	@Override
	public void onClick(View v) {
		DroidPop app = DroidPop.getApplication();
		ScreenCapManager test1 = (ScreenCapManager) app
				.getAppService(DroidPop.SCREEN_CAPTURE_SERVICE);
		final ImageView imgView = (ImageView) v;

		test1.dispatch(new ScreenCapTaskDispatcher() {

			private long curMillis = System.currentTimeMillis();

			@Override
			public void onDone(ArrayList<Bitmap> resluts) {
				logCostMillis("screencap ok");

				Bitmap bitmap = resluts.get(0);
				mOcr.recognize(bitmap);

				// logCostMillis("ocr ok");

				imgView.setImageBitmap(bitmap);

				// logCostMillis("load bitmap ok");
			}

			@Override
			public void onCancelled(String msg) {

			}

			@Override
			public Rect[] setBounds() {
				return null;
			}

			private void logCostMillis(String tag) {
				long diff = System.currentTimeMillis() - curMillis;
				curMillis = System.currentTimeMillis();

				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS",
						Locale.CHINA);
				Date cost = new Date(diff);
				DroidPop.debug(tag, ": ", sdf.format(cost));
			}

		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(mOcr == null) {
			return false;
		}
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Point point = new Point((int) (event.getRawX() + 0.5f),
					(int) (event.getRawY() - mStatusBarHeight + 0.5f));
			DroidPop.debug(mOcr.getText(point));
		}
		
		return true;
	}

}
