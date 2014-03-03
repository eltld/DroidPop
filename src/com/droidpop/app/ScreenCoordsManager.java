package com.droidpop.app;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.droidpop.service.LocalBinder;
import com.droidpop.service.ScreenCoordsService;
import com.droidpop.view.OnScreenTouchListener;
import com.droidpop.view.SystemOverlayView;

public class ScreenCoordsManager implements ServiceManager {
	private Context mContext;
	private ScreenCoordsService mService;
	private boolean mIsBound;
	private ServiceConnection mConn;
	private ArrayList<OnScreenTouchListener> mListeners; // TODO: buffer need improve
	private Integer mShowTouchesSemaphore; // TODO: simplified and need refactor
	
	protected ScreenCoordsManager(Context context) {
		mContext = context;
		mService = null;
		mIsBound = false;
		mConn = new ScreenCoordsServiceConn();
		mListeners = new ArrayList<OnScreenTouchListener>();
		mShowTouchesSemaphore = 0;
	}
	
	public void addOnScreenTouchListener(OnScreenTouchListener listener) {
		if (mService != null) {
			mService.addOnScreenTouchListener(listener);
		} else {
			mListeners.add(listener);
		}
	}
	
	public void enableShowTouches() {
		if (mService != null && mShowTouchesSemaphore > 0) {
			SystemOverlayView overlay = mService.getOverlayView();
			if (PreferenceSettingsManager.isShowTouches()) {
				overlay.showTouchPointer(PreferenceSettingsManager
						.isShowTouchesCustomized());
			}
		} else {
			synchronized (mShowTouchesSemaphore) {
				mShowTouchesSemaphore++;
			}
		}
	}
	
	public void disableShowTouches() {
		if (mService != null && mShowTouchesSemaphore < 0) {
			SystemOverlayView overlay = mService.getOverlayView();
			overlay.hideTouchPointer();
		} else {
			synchronized (mShowTouchesSemaphore) {
				mShowTouchesSemaphore--;
			}
		}
	}
	
	protected void startService() {
		if(mIsBound) {
			return;
		}
		
		mContext.startService(new Intent(mContext, ScreenCoordsService.class));
		bindService();
	}
	
	protected void stopService() {
		unbindService();
		mContext.stopService(new Intent(mContext, ScreenCoordsService.class));
	}
	
	private void bindService() {
		if (!mContext.bindService(new Intent(mContext,
				ScreenCoordsService.class), mConn, Context.BIND_AUTO_CREATE)) {
			DroidPop.log(DroidPop.LEVEL_WARN, "connection not established!");
		}
	}
	
	private void unbindService() {
		if(mIsBound) {
			mContext.unbindService(mConn);
			mIsBound = false;
		}
	}

	private class ScreenCoordsServiceConn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mService = (ScreenCoordsService) ((LocalBinder)binder).getService();
			mIsBound = true;
			
			if(!mListeners.isEmpty()) {
				for (OnScreenTouchListener listener : mListeners) {
					mService.addOnScreenTouchListener(listener);
				}
				mListeners.clear();
			}
			
			synchronized (mShowTouchesSemaphore) {
				if(mShowTouchesSemaphore > 0) {
					enableShowTouches();
				} else if(mShowTouchesSemaphore < 0) {
					disableShowTouches();
				}
				mShowTouchesSemaphore = 0;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			mIsBound = false;
		}
	}
}
