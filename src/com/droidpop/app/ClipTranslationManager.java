package com.droidpop.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.wtao.os.UiThreadHandler;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.droidpop.dict.TranslationTask.OnTranslateListener;
import com.droidpop.view.OnScreenTouchListener;

public class ClipTranslationManager implements ServiceManager {
	public static interface OnClipTranslationListener extends OnTranslateListener {
		
		public void onClipped();
	}
	
	public static abstract class OnLongPressTranslationListener implements OnClipTranslationListener {

		private static OnScreenLongPressListener sOnScreenLongPressListener = null;
		
		public OnLongPressTranslationListener(Context context) {
			if(sOnScreenLongPressListener == null) {
				synchronized(OnLongPressTranslationListener.class) {
					if(sOnScreenLongPressListener == null) {
						sOnScreenLongPressListener = new OnScreenLongPressListener(context);
						
						ScreenCoordsManager mgr = (ScreenCoordsManager) DroidPop
								.getApplication().getAppService(
										DroidPop.SCREEN_COORDS_SERVICE);
						mgr.setOnScreenTouchListener(sOnScreenLongPressListener);
					}
				}
			}
		}

		/**
		 * 
		 * @param coords when long press to clip and translate, return the coords on the screen
		 */
		public abstract void onClipped(PointerCoords coords);
		
		@Override
		public void onClipped() {
			onClipped(sOnScreenLongPressListener.getPointerCoords());
		}
		
		private static class OnScreenLongPressListener implements OnScreenTouchListener {

			private boolean mIsLongPress = false;
			private PointerCoords mCoords = new PointerCoords();
			
			private GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDown(MotionEvent e) {
					synchronized (mCoords) {
						mIsLongPress = false;
						e.getPointerCoords(0, mCoords);
					}
					return true;
				};

				@Override
				public void onLongPress(MotionEvent e) {
					synchronized (mCoords) {
						mIsLongPress = true;
					}
				};
			};

			private UiThreadHandler mUiHandler;
			private GestureDetector mDetector;

			public OnScreenLongPressListener(Context context) {
				mUiHandler = new UiThreadHandler();
				mDetector = new GestureDetector(context, listener,
						mUiHandler.getHandler());
			}

			@Override
			public void onScreenTouch(MotionEvent event) {
				mDetector.onTouchEvent(event);
			}

			public PointerCoords getPointerCoords() {
				synchronized (mCoords) {
					if (mIsLongPress) {
						return mCoords;
					} else {
						return null;
					}
				}
			}
		};
		
	}
	
	private static ClipTranslationManager sClipTranslationManager;

	private Context mContext;
	private ClipboardManager mClipboardManager;
	private ArrayList<WeakReference<OnClipTranslationListener>> mListeners;
	
	public void setOnClipTranslationListener(OnClipTranslationListener listener) {
		mListeners.add(new WeakReference<OnClipTranslationListener>(listener));
	}

	protected static ClipTranslationManager getManager(Context context) {
		if (sClipTranslationManager == null) {
			synchronized (ClipTranslationManager.class) {
				if (sClipTranslationManager == null) {
					sClipTranslationManager = new ClipTranslationManager(context);
				}
			}
		}
		return sClipTranslationManager;
	}

	private ClipTranslationManager(Context context) {
		mContext = context;
		mClipboardManager = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		mListeners = new ArrayList<WeakReference<OnClipTranslationListener>>();
	}
}
