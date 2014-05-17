package com.droidpop.app;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.wtao.utils.Log;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.droidpop.config.ApplicationConfig;
import com.droidpop.dict.TranslationTask;
import com.droidpop.dict.TranslationTask.OnTranslateListener;
import com.droidpop.dict.TranslationTask.Status;
import com.droidpop.dict.Translator;
import com.droidpop.dict.WordCategory;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntry.Paraphrase;
import com.droidpop.dict.wordnet.WordNetTranslator;
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
						
						ScreenCoordsManager mgr = (ScreenCoordsManager) DroidPop.APPLICATION
								.getAppService(DroidPop.SCREEN_COORDS_SERVICE);
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
			
			/**
		     * Defines the default duration in milliseconds before a press turns into
		     * a long press
		     */
		    private static final int DEFAULT_LONG_PRESS_TIMEOUT = 500;
		    /**
		     * ViewConfiguration.getLongPressTimeout(); defualt {@link #DEFAULT_LONG_PRESS_TIMEOUT}.
		     */
			private static final int LONGPRESS_TIMEOUT = DEFAULT_LONG_PRESS_TIMEOUT;
			/**
		     * Defines the duration in milliseconds we will wait to see if a touch event
		     * is a tap or a scroll. If the user does not move within this interval, it is
		     * considered to be a tap.
		     */
		    private static final int TAP_TIMEOUT = 180;
			
			// constants for Message.what used by GestureHandler below
		    private static final int SHOW_PRESS = 1;
		    private static final int LONG_PRESS = 2;
			
			private boolean mIsLongPress = false;
			private PointerCoords mCoords = new PointerCoords();

			private final GestureDetector.OnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {
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

			private final boolean mIsLongpressEnabled = true;
			private final Handler mHandler;
			private MotionEvent mCurrentDownEvent;
			
			public OnScreenLongPressListener(Context context) {
				this(context, null);
			}
			
			public OnScreenLongPressListener(Context context, Handler handler) {
//				// TODO: test the solution, which onShow() not work;
//				// use other specified 'GestureDetector' instead but more jobs :(
//				mDetector = new GestureDetector(context, mListener, handler);
				
				if (handler != null) {
		            mHandler = new GestureHandler(handler);
		        } else {
		            mHandler = new GestureHandler();
		        }
			}

			@Override
			public void onScreenTouch(MotionEvent event) {
				DroidPop.log(DroidPop.LEVEL_VERBOSE, Log.shortFor(event));
				
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					if (mCurrentDownEvent != null) {
		                mCurrentDownEvent.recycle();
		            }
		            mCurrentDownEvent = MotionEvent.obtain(event);
					mListener.onDown(event);
		            
		            boolean succeeded = false;
					if (mIsLongpressEnabled) {
						mHandler.removeMessages(LONG_PRESS);
//						succeeded = mHandler.sendEmptyMessageAtTime(LONG_PRESS,
//								mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);

						// good luck, it works; but why? it look likes depending on uptimeMillis()
						succeeded = mHandler.sendEmptyMessageDelayed(LONG_PRESS, 
								TAP_TIMEOUT + LONGPRESS_TIMEOUT);
					} else {
						// show press, short time
//						succeeded = mHandler.sendEmptyMessageAtTime(SHOW_PRESS,
//								mCurrentDownEvent.getDownTime() + TAP_TIMEOUT);
						
						succeeded = mHandler.sendEmptyMessageDelayed(LONG_PRESS, 
								TAP_TIMEOUT + LONGPRESS_TIMEOUT);
					}
					
					if(ApplicationConfig.DEBUG) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
						Date currentDate = new Date(mCurrentDownEvent.getDownTime());
						DroidPop.debug("sendEmptyMessageAtTime() ", ((succeeded) ? "succeeded" : "failed")
								, ", time stamp: ", sdf.format(currentDate));
					}
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if(mIsLongpressEnabled) {
						mHandler.removeMessages(LONG_PRESS);
					} else {
						mHandler.removeMessages(SHOW_PRESS);
					}
					break;
				}
				
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
			
			@SuppressLint("HandlerLeak")
			private class GestureHandler extends Handler {
		        GestureHandler() {
		            super(); // main thread
		        }

				GestureHandler(Handler handler) {
		            super(handler.getLooper());
		            DroidPop.debug(Thread.currentThread());
		        }

		        @Override
		        public void handleMessage(Message msg) {
		        	DroidPop.debug("recieved and handle...");
		        	
		            switch (msg.what) {
		            case SHOW_PRESS:
		                mListener.onShowPress(mCurrentDownEvent);
		                break;
		                
		            case LONG_PRESS:
		                dispatchLongPress();
		                break;

		            default:
		                throw new RuntimeException("Unknown message " + msg); //never
		            }
		        }
		    }
			
			private void dispatchLongPress() {
		        mListener.onLongPress(mCurrentDownEvent);
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
		
		mTranslator = new WordNetTranslator(context); // TODO:
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
				if(ApplicationConfig.DEBUG) {
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
