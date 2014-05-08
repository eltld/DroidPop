package com.droidpop.app;

import java.io.File;
import java.io.FilenameFilter;
import java.security.InvalidParameterException;

import junit.framework.Assert;
import me.wtao.app.LauncherShortcut;
import me.wtao.utils.Log;
import android.app.Activity;
import android.content.Context;

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
	private final static Log Log = new Log();
	static {
		Log.setOn();	// if turned off, check the lang package at tess2/
		Log.calibrateIndexOfCaller(4);
	}
	
	private final Context mContext;
	private final Activity mActivity;
	private final ScreenCoordsManager mScreenCoordsManager;
	private final ScreenCapManager mScreenCapManager;
	private final ClipTranslationManager mClipTranslationManager;
	
	public static void initFromLauncherActivity(Context context) {
		if(sDroidPop == null) {
			synchronized (DroidPop.class) {
				if(sDroidPop == null) {
					sDroidPop = new DroidPop(context);
					checkLibSupported(context);
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
	
	public static boolean isDebuggable() {
		return Log.isDebuggable();
	}
	
	public static void debug(Object... msg_segs) {
		Log.debug(msg_segs);
	}
	
	public static void log(int level, Object... msg_segs) {
		switch (level) {
		case LEVEL_VERBOSE:
			Log.vobe(msg_segs);
			break;
		case LEVEL_DEBUG:
			Log.debug(msg_segs);
			break;
		case LEVEL_INFO:
			Log.inform(msg_segs);
			break;
		case LEVEL_WARN:
			Log.warn(msg_segs);
			break;
		case LEVEL_ERROR:
			Log.error(msg_segs);
			break;
		}
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public void createShortcut(boolean force) {
		if(force || !PreferenceSettingsManager.hasCreatedShotcut()) {
			final String appName = mContext.getResources().getString(R.string.app_name);
			final int iconId = R.drawable.ic_logo;
			LauncherShortcut.createShortcutFor(mActivity, appName, iconId);
			PreferenceSettingsManager.set(PreferenceSettingsManager.SHOTCUT, true);
		}
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
		if(context instanceof Activity) {
			mActivity = (Activity) context;
		} else {
			final String appName = mContext.getResources().getString(R.string.app_name);
			Log.warn(appName, " is not initialized from LauncherActivity!");
			throw new InvalidParameterException("should initialized from LauncherActivity");
		}
		
		mScreenCoordsManager = new ScreenCoordsManager(mContext);
		mScreenCapManager = new ScreenCapManager(mContext);
		mClipTranslationManager = ClipTranslationManager.getManager(mContext);
	}
	
	private static void checkLibSupported(Context context) {
		final String abi = Environment.checkAbi();
		
		// if supported, *.so will copy to here
		String[] ls = null;
		File libDir = new File(context.getApplicationInfo().nativeLibraryDir);
		if(libDir.isDirectory()) {
			ls = libDir.list(new FilenameFilter() {
				private static final String LIB_SUFFIX = ".so";
				
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(LIB_SUFFIX);
				}
			});
		}
		
		// list should not be null or only contain libenv.so
		boolean supported = (ls != null && ls.length > 1);
		
		if(!supported) {
			DroidPop.log(DroidPop.LEVEL_WARN,
					"device's processor arch not supported, which is ",
					abi);
			DroidPop.debug(
					"if you're a developer, you may find the right arch-abi at abi/; ",
					"default is armeabi-v7a ",
					"to compressing the .apk size from 10+ MB to fewer size f.e. 2M, ",
					"and fast debug");
		}
	}
	
}
