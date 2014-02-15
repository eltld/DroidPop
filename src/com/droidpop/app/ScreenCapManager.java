package com.droidpop.app;

import me.wtao.service.IScreenCaptureService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;

public class ScreenCapManager implements ServiceManager {
	private static final long WAIT_TIME_THRESHOLD = 30000; // ms
	private static final long RE_CONNECT_LIMIT = 3; // attempt limit
	private static final long TIME_OUT_SERVICE_ANR = 10000; // ms
	private static final long TIME_OUT_CONNECTION_ESTABLISH = 1000; // ms

	protected Context mContext;
	
	private long mWaitTimeout = TIME_OUT_CONNECTION_ESTABLISH; // ms	
	private int mConnCnt = 0;
	
	private IScreenCaptureService mService = null;
	private boolean mIsBound = false;
	private ServiceConnection mConn = new ScreenCapServiceConn();
	
	protected ScreenCapManager(Context context) {
		mContext = context;
	}
	
	public Bitmap takeScreenCapture() {
		try {
			return mService.takeScreenCapture();
		} catch (RemoteException e) {
			DroidPop.log(DroidPop.LEVEL_WARN, e);
		} catch (Exception e) {
			DroidPop.log(DroidPop.LEVEL_ERROR, e);
		}
		
		return null;
	}

	protected void startService() {
		connect();
		
		while (mService == null && mConnCnt <= RE_CONNECT_LIMIT) {
			DroidPop.log(DroidPop.LEVEL_WARN, "try to reconnect...");
			connect();
			++mConnCnt;

			if (mWaitTimeout < WAIT_TIME_THRESHOLD) {
				mWaitTimeout *= 2; // double increase
			} else {
				mWaitTimeout += 1000; // linear increase
			}

		}
		
		if(mConnCnt >= RE_CONNECT_LIMIT) {
			DroidPop.log(DroidPop.LEVEL_WARN, "attempt limit!");
		}
	}

	protected void stopService() {
		unbindService();
	}
	
	private void connect() {
		bindService();
		
		// blocked and wait until connection established
		synchronized (this) {
			try {
				DroidPop.debug("blocked and wait...");
				wait((mConnCnt == 0) ? TIME_OUT_SERVICE_ANR : mWaitTimeout);				
			} catch (InterruptedException e) {
				DroidPop.log(DroidPop.LEVEL_WARN, "oops, cannot wait to go!");
			}
		}
	}
	
	private void bindService() {
		if (!mContext.bindService(new Intent(mContext,
				IScreenCaptureService.class), mConn, Context.BIND_AUTO_CREATE)) {
			DroidPop.log(DroidPop.LEVEL_WARN, "connection not established!");
		}
	}
	
	private void unbindService() {
		if(mIsBound) {
			mContext.unbindService(mConn);
			mIsBound = false;
		}
	}

	private class ScreenCapServiceConn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mService = IScreenCaptureService.Stub.asInterface(binder);
			mIsBound = true;
			synchronized(ScreenCapManager.this) {
				ScreenCapManager.this.notify();
				mWaitTimeout = TIME_OUT_CONNECTION_ESTABLISH;
				mConnCnt = 1;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			mIsBound = false;
		}
	}
}
