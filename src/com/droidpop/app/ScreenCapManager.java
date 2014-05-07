package com.droidpop.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.wtao.service.IScreenCaptureService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

public class ScreenCapManager implements ServiceManager {

	public static interface ScreenCapTaskDispatcher extends OnScreenCaptureListener {
		public Rect getBound();
	}

	protected Context mContext;
	
	private final ArrayList<WeakReference<ScreenCaptureTask>> mCapTasks = new ArrayList<WeakReference<ScreenCaptureTask>>();
	
	protected ScreenCapManager(Context context) {
		mContext = context;
	}
	
	public void dispatch(ScreenCapTaskDispatcher dispatcher) {
		ScreenCaptureTask task = new ScreenCaptureTask();
		add(new WeakReference<ScreenCapManager.ScreenCaptureTask>(task));
		task.setOnScreenCaptureListener(dispatcher);
		Rect bound = dispatcher.getBound();
		if(bound == null) {
			task.execute();
		} else {
			task.execute(bound);
		}
	}

	/**
	 * not used but reserved
	 */
	protected void startService() {
		
	}

	protected void stopService() {
		clear();
	}
	
	private static interface OnScreenCaptureListener {
		public void onDone(Bitmap screencap);
		/**
		 * @param msg not used but reserved, error information
		 */
		public void onCancelled(String msg);
	}
	
	/**
	 * add the WeakReference referring a ScreenCaptureTask to the task set.
	 */
	private boolean add(WeakReference<ScreenCaptureTask> object) {
		final int size = mCapTasks.size();
		for (int i = 0; i != size; ++i) {
			WeakReference<ScreenCaptureTask> ref = mCapTasks.get(i);
			if (ref == null || ref.get() == null) {
				mCapTasks.set(i, object);
				return true;
			}
		}
		return mCapTasks.add(object);
	};


	/**
	 * cancel all ScreenCaptureTask if it's not finished, and remove all from
	 * the task set.
	 */
	private void clear() {
		for (WeakReference<ScreenCaptureTask> ref : mCapTasks) {
			if (ref != null && ref.get() != null) {
				ref.get().cancel(true);
			}
		}

		mCapTasks.clear();
	};
	
	private class ScreenCaptureTask extends AsyncTask<Rect, Bitmap, Bitmap> {
		private static final long WAIT_TIME_THRESHOLD = 30000; // ms
		private static final long RE_CONNECT_LIMIT = 3; // attempt limit
		private static final long TIME_OUT_SERVICE_ANR = 10000; // ms
		private static final long TIME_OUT_CONNECTION_ESTABLISH = 1000; // ms

		private ServiceConnection mConn;
		private OnScreenCaptureListener mListener;
		
		private IScreenCaptureService mService;
		private boolean mIsBound;
		
		private long mWaitTimeout;
		private int mConnCnt;
		
		public ScreenCaptureTask() {
			mConn = new ScreenCapServiceConn();
			mListener = null;
			
			mService = null;
			mIsBound = false;
			
			mWaitTimeout = TIME_OUT_CONNECTION_ESTABLISH; // ms	
			mConnCnt = 0;
		}
		
		public void setOnScreenCaptureListener(OnScreenCaptureListener listener) {
			mListener = listener;
		}
		
		/**
		 * @param rects not used but reserved, to specify rectangles on the screen capture
		 * @return first element is the whole screen capture if success
		 */
		@Override
		protected Bitmap doInBackground(Rect... rects) {
			DroidPop.debug("take screen capture...");
			
			mConnCnt = 0;
			connect();
			
			if (mService == null) {
				reconnect();
			}
			
			Bitmap screencap = null;
			try {
				screencap = mService.takeScreenCapture();
				
				Rect bound = null;
				if (rects.length == 1) {
					bound = rects[0];
				}
				
				if (bound != null) {
					Bitmap bmp = Bitmap.createBitmap(screencap, bound.left,
							bound.top, bound.width(), bound.height());
					screencap.recycle();
					screencap = bmp;
				}
			} catch (RemoteException e) {
				DroidPop.log(DroidPop.LEVEL_WARN, e);
			} catch (Exception e) {
				DroidPop.log(DroidPop.LEVEL_ERROR, e);
			}
			
			return screencap;
		}
		
		@Override
		protected void onProgressUpdate(Bitmap... values) {
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			DroidPop.debug("done");
			if(mListener != null) {
				mListener.onDone(result);
			}
			
			if (!isCancelled()) {
				unbindService();
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled(Bitmap result) {
			DroidPop.debug("cancelled");
			if(mListener != null) {
				mListener.onCancelled(null);
			}
			
			if(isCancelled()) {
				unbindService();
			}
			super.onCancelled(result);
		}

		private void connect() {
			bindService();
			
			if(!mIsBound) {
				// blocked and wait until connection established
				synchronized (this) {
					try {
						DroidPop.debug("TID", Thread.currentThread().getId(),
								": blocked and wait...");
						wait((mConnCnt == 0) ? TIME_OUT_SERVICE_ANR : mWaitTimeout);				
					} catch (InterruptedException e) {
						DroidPop.log(DroidPop.LEVEL_WARN, "oops, cannot wait to go!");
					}
				}
			}
			
			mConnCnt++;
		}
		
		private void reconnect() {
			while (mService == null && mConnCnt <= RE_CONNECT_LIMIT) {
				DroidPop.log(DroidPop.LEVEL_WARN, "try to reconnect...");
				connect();
				
				if (mWaitTimeout < WAIT_TIME_THRESHOLD) {
					mWaitTimeout *= 4; // fast increase
				} else {
					mWaitTimeout += 1000; // linear increase
				}
			}
			
			if(mConnCnt > RE_CONNECT_LIMIT) {
				DroidPop.log(DroidPop.LEVEL_WARN, "attempt limit!");
			}
		}
		
		private void bindService() {
			if (!mContext.bindService(
					new Intent(IScreenCaptureService.class.getName()),
					mConn, Context.BIND_AUTO_CREATE)) {
				DroidPop.log(DroidPop.LEVEL_WARN, "connection not established!");
			}
		}
		
		private void unbindService() {
			if(mIsBound) {
				mContext.unbindService(mConn);
				
				mService = null;
				mIsBound = false;
			}
		}
		
		private class ScreenCapServiceConn implements ServiceConnection {

			@Override
			public void onServiceConnected(ComponentName name, IBinder binder) {
				DroidPop.debug("connection established.");
				
				mService = IScreenCaptureService.Stub.asInterface(binder);
				mIsBound = true;
				synchronized(ScreenCaptureTask.this) {
					DroidPop.debug("TID", Thread.currentThread().getId(),
							": ok and notify");
					ScreenCaptureTask.this.notify();
					mWaitTimeout = TIME_OUT_CONNECTION_ESTABLISH;
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				DroidPop.debug("connection broken!");
				
				reconnect();
				
				if (mService == null) {
					mIsBound = false;
				}
			}
		}
	}
}
