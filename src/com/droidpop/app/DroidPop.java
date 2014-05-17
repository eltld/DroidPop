package com.droidpop.app;

import java.io.File;
import java.io.FilenameFilter;

import me.wtao.utils.Log;
import android.app.Application;
import android.content.Context;

public class DroidPop extends Application {
	public static Context APPLICATION_CONTEXT;
	public static DroidPop APPLICATION;
	
	public static final int SCREEN_COORDS_SERVICE = 0;
	public static final int SCREEN_CAPTURE_SERVICE = 1;
	public static final int CLIP_TRANSLATION_SERVICE = 2;
	
	public static final int LEVEL_VERBOSE = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARN = 3;
	public static final int LEVEL_ERROR = 4;
	
	private static final String TAG = DroidPop.class.getSimpleName();
	
	private Context mContext;
	private ScreenCoordsManager mScreenCoordsManager;
	private ScreenCapManager mScreenCapManager;
	private ClipTranslationManager mClipTranslationManager;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "DroidPop application onCreate...");
		
		APPLICATION_CONTEXT = getApplicationContext();
		APPLICATION = this;
		checkLibSupported(APPLICATION_CONTEXT);
		onInitialize(APPLICATION_CONTEXT);
		
		super.onCreate();
	}
	
	public static void debug(Object... msg_segs) {
		Log.d(TAG, msg_segs);
	}
	
	public static void log(int level, Object... msg_segs) {
		switch (level) {
		case LEVEL_VERBOSE:
			Log.v(TAG, msg_segs);
			break;
		case LEVEL_DEBUG:
			Log.d(TAG, msg_segs);
			break;
		case LEVEL_INFO:
			Log.i(TAG, msg_segs);
			break;
		case LEVEL_WARN:
			Log.w(TAG, msg_segs);
			break;
		case LEVEL_ERROR:
			Log.e(TAG, msg_segs);
			break;
		}
	}
	
	public Context getContext() {
		return mContext;
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
	
	private void onInitialize(Context context) {
		mContext = context.getApplicationContext();
		
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
