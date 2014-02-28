package com.droidpop.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.wtao.utils.Logcat;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.droidpop.dict.TranslationTask;
import com.droidpop.dict.TranslationTask.OnTranslateListener;
import com.droidpop.dict.TranslationTask.Status;
import com.droidpop.dict.Translator;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntry.Paraphrase;
import com.droidpop.dict.youdao.YouDaoTranslator;
import com.droidpop.view.OnScreenTouchListener;

public class ClipTranslationManager implements ServiceManager, ClipboardManager.OnPrimaryClipChangedListener {
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
						mgr.addOnScreenTouchListener(sOnScreenLongPressListener);
						
						DroidPop.debug("init ok.");
					}
				}
			}
		}

		/**
		 * Note: the coords may be <b>null</b>, check before using it.
		 * 
		 * @param coords the coords on the screen when long press to clip and translate
		 */
		public abstract void onClipped(PointerCoords coords);
		
		/**
		 * invoked after {@link #onClipped(PointerCoords)}
		 */
		@Override
		public abstract void onTranslated(WordEntry entry, Status state);
		
		@Override
		public void onClipped() {
			onClipped(sOnScreenLongPressListener.getPointerCoords());
		}
		
		private static class OnScreenLongPressListener implements OnScreenTouchListener {

			private boolean mIsLongPress = false;
			private PointerCoords mCoords = new PointerCoords();
			
			private GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDown(MotionEvent event) {
					synchronized (mCoords) {
						mIsLongPress = false;
						DroidPop.debug("touch down...");
					}
					return true;
				};

				@Override
				public void onLongPress(MotionEvent event) {
					onShow(event);
				};
				
				private synchronized void onShow(MotionEvent event) {
					synchronized (mCoords) {
						mIsLongPress = true;
						event.getPointerCoords(0, mCoords);
						DroidPop.debug("on show...");
					}
				}
			};

//			private UiThreadHandler mUiHandler;
			private GestureDetector mDetector;

			public OnScreenLongPressListener(Context context) {
//				mUiHandler = new UiThreadHandler();
//				mDetector = new GestureDetector(context, listener,
//						mUiHandler.getHandler());
				
				mDetector = new GestureDetector(context, listener);
			}

			@Override
			public void onScreenTouch(MotionEvent event) {
				DroidPop.log(DroidPop.LEVEL_VERBOSE, Logcat.shortFor(event));
				mDetector.onTouchEvent(event);
			}

			public PointerCoords getPointerCoords() {
				synchronized (mCoords) {
					if (mIsLongPress) {
						return mCoords;
					} else {
						DroidPop.debug("bad coords!");
						return null;
					}
				}
			}
		};
		
	}
	
	private static ClipTranslationManager sClipTranslationManager;

	protected final Context mContext;
	private final ClipboardManager mClipboardManager;
	private final ArrayList<OnClipTranslationListener> mListeners;
	
	private Translator mTranslator;
	
	public boolean addOnClipTranslationListener(OnClipTranslationListener listener) {
		return mListeners.add(listener);
	}
	
	public boolean removeOnClipTranslationListener(OnClipTranslationListener listener) {
		return mListeners.remove(listener);
	}
	
	public void setTranslator(Translator translator) {
		if(translator != null) {
			mTranslator = translator;
		}
	}
	
	@Override
	public void onPrimaryClipChanged() {
		DroidPop.debug("did clip...");
		
		boolean translated = false;
		for(OnClipTranslationListener listener : mListeners) {
			if(listener != null) {
				listener.onClipped();
				translated = true;
			}
		}
		
		if (translated) {
			DroidPop.debug("translating...");
			doTranslation();
			DroidPop.debug("did translate.");
		}
	}

	protected static ClipTranslationManager getManager(Context context) {
		if (sClipTranslationManager == null) {
			synchronized (ClipTranslationManager.class) {
				if (sClipTranslationManager == null) {
					sClipTranslationManager = new ClipTranslationManager(context);
					
					DroidPop.debug("init ok.");
				}
			}
		}
		return sClipTranslationManager;
	}
	
	protected void startService() {
		mClipboardManager.addPrimaryClipChangedListener(this);
	}
	
	protected void stopService() {
		mClipboardManager.removePrimaryClipChangedListener(this);
	}

	private ClipTranslationManager(Context context) {
		mContext = context;
		mClipboardManager = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		mListeners = new ArrayList<OnClipTranslationListener>();
		
		mTranslator = new YouDaoTranslator();
	}
	
	private synchronized void doTranslation() {
		TranslationTask task = new TranslationTask(mTranslator, new OnTranslateListener() {
			@Override
			public void onTranslated(WordEntry entry, Status state) {
				if(state == TranslationTask.Status.CANCELLED) {
					DroidPop.debug("TranslationTask cancelled");
					return;
				}
				
				if(entry == null || !entry.isValid()) {
					DroidPop.debug("bad word entry!");
					return;
				}
				
				log(entry);
				
				for (OnClipTranslationListener listener : mListeners) {
					if (listener != null) {
						listener.onTranslated(entry, state);
					}
				}
			}
			
			private void log(WordEntry entry) {
				if(DroidPop.isDebuggable()) {
					StringBuilder sb = new StringBuilder();
					sb.append(entry.getWord());
					final String NEW_LINE = "\n\t";
					for (WeakReference<Paraphrase> ref : entry.getParaphrasesBy(WordCategory.BASIC_CATEGORY)) {
						sb.append(NEW_LINE).append(ref.get().getDetail());
					}
					DroidPop.debug(sb.toString());
				}
			}
		});
		
		try {
			task.translate(performPaste());
		} catch(NullPointerException e) {
			DroidPop.log(DroidPop.LEVEL_WARN, "TranslationTask cancelled for the reason: ", e.getMessage());
			task.cancel(true);
		}
	}
	
	private String performPaste() throws NullPointerException {
		ClipData clip = mClipboardManager.getPrimaryClip();
		if (clip != null) {
			ClipData.Item item = clip.getItemAt(0);
			String text = item.getText().toString();
			DroidPop.debug("paste text: ", text);
			return text;
		} else {
			throw new NullPointerException("clip data is empty [null].");
		}
	}
	
}
