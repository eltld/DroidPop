package com.droidpop.app;

import me.wtao.app.LauncherShortcut;
import android.app.Activity;

import com.droidpop.R;

public class ContextUtils {
	public static void createShortcutForLauncher(Activity activity, boolean always) {
		if(always || !PreferenceSettingsManager.hasCreatedShotcut()) {
			final String appName = activity.getResources().getString(R.string.app_name);
			final int iconId = R.drawable.ic_logo;
			LauncherShortcut.createShortcutFor(activity, appName, iconId);
			PreferenceSettingsManager.set(PreferenceSettingsManager.SHOTCUT, true);
		}
	}
}
