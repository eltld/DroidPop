package com.droidpop.app;

import junit.framework.Assert;
import me.wtao.utils.Logcat;
import android.content.Context;
import android.content.Intent;

import com.droidpop.R;

public class DroidPop {
	public static final int SCREEN_COORDS_SERVICE = 0;
	public static final int SCREEN_CAPTURE_SERVICE = 1;
	public static final int CLIP_TRANSLATION_SERVICE = 2;
	
	public static final int LEVEL_VERBOSE = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARN = 3;
	public static final int LEVEL_ERROR = 4;
	
	private volatile static DroidPop sDroidPop = null;
	private final static Logcat sLogcat = new Logcat();
	static {
		sLogcat.setOn();	// debug mode
	}
	
	private Context mContext;
	private final ScreenCoordsManager mScreenCoordsManager;
	private final ScreenCapManager mScreenCapManager;
	private final ClipTranslationManager mClipTranslationManager;
	
	public static void initFromLauncherActivity(Context context) {
		if(sDroidPop == null) {
			synchronized (DroidPop.class) {
				if(sDroidPop == null) {
					sDroidPop = new DroidPop(context);
				}
			}
		}
	}
	
	public static DroidPop getApplication() {
		Assert.assertNotNull("MUST initFromLauncherActivity(Context) first", sDroidPop);
		
		return sDroidPop;
	}
	
	public static Context getApplicationContext() {
		return getApplication().getContext();
	}
	
	static {
		sLogcat.setOn();
		sLogcat.calibrateIndexOfCaller(4);
	}
	
	public static boolean isDebuggable() {
		return sLogcat.isDebuggable();
	}
	
	public static void debug(Object... msg_segs) {
		sLogcat.d(msg_segs);
	}
	
	public static void log(int level, Object... msg_segs) {
		switch (level) {
		case LEVEL_VERBOSE:
			sLogcat.v(msg_segs);
			break;
		case LEVEL_DEBUG:
			sLogcat.d(msg_segs);
			break;
		case LEVEL_INFO:
			sLogcat.i(msg_segs);
			break;
		case LEVEL_WARN:
			sLogcat.w(msg_segs);
			break;
		case LEVEL_ERROR:
			sLogcat.e(msg_segs);
			break;
		}
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public void createShortcutFor(Context context, String name, String icon) {
		Intent launchIntent = new Intent();
		launchIntent.setClassName(mContext.getPackageName(), context.getClass().getName());
		launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		Intent shortcutIntent = new Intent();
		
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		
		shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		mContext.sendBroadcast(shortcutIntent);
	}
	
	public void createShortcutFor(Context context) {
		String appName = mContext.getResources().getString(R.string.app_name);
		String icon = null; // TODO
		createShortcutFor(context, appName, icon);
	}
	
	public ServiceManager getAppService(int serviceId) {
		ServiceManager manager = null;
		switch (serviceId) {
		case SCREEN_COORDS_SERVICE:
			mScreenCoordsManager.startService();
			manager = mScreenCoordsManager;
			break;
		case SCREEN_CAPTURE_SERVICE:
			manager = mScreenCapManager;
			break;
		case CLIP_TRANSLATION_SERVICE:
			mClipTranslationManager.startService();
			manager = mClipTranslationManager;
			break;
		}
		return manager;
	}
	
	public void stopService(int serviceId) {
		switch (serviceId) {
		case SCREEN_COORDS_SERVICE:
			mScreenCoordsManager.stopService();
			break;
		case SCREEN_CAPTURE_SERVICE:
			mScreenCapManager.stopService();
			break;
		case CLIP_TRANSLATION_SERVICE:
			mClipTranslationManager.stopService();
			break;
		}
	}
	
	public void stopService() {
		mScreenCoordsManager.stopService();
		mScreenCapManager.stopService();
		mClipTranslationManager.stopService();
	}
	
	private DroidPop(Context context) {
		mContext = context.getApplicationContext();
		
//		RootManager manager = new RootManager();
//		final String cmd = "chmod 777 " + mContext.getPackageCodePath();
//		if (manager.isRootAvailable()) {
//			sLogcat.d(cmd);
//			
//			manager.sudo(new OnShellProcessingListener() {
//
//				@Override
//				public void onCommandCompleted(int id, int exitCode) {
//					sLogcat.d("ok");
//				}
//
//				@Override
//				public void onCommandOutput(int id, String line) {
//					sLogcat.d(line);
//				}
//
//				@Override
//				public void onCommandTerminated(int id, String reason) {
//					sLogcat.d(reason);
//				}
//
//			}, cmd);
//		}
		
		mScreenCoordsManager = new ScreenCoordsManager(mContext);
		mScreenCapManager = new ScreenCapManager(mContext);
		mClipTranslationManager = ClipTranslationManager.getManager(mContext);
	}
	
}
