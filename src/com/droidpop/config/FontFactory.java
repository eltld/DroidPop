package com.droidpop.config;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;

public class FontFactory {

	public enum Font {
		EXISTENCE("fonts/existence.ttf"),
		HELIGHT("fonts/helight.ttf"),
		ROBOTO_BOLD("fonts/Roboto-Bold.ttf"),
		ROBOTO_ITALIC("fonts/Roboto-Italic.ttf"),
		ROBOTO_LIGHT("fonts/Roboto-Light.ttf"),
		ROBOTO_THIN("fonts/Roboto-Thin.ttf"),
		ROBOTO_THIN_ITALIC("fonts/Roboto-ThinItalic.ttf");
		
		private final String mPath;
		
		private Font(String path) {
			mPath = path;
		}
		
		@Override
		public String toString() {
			return mPath;
		}
	}

	private static final HashMap<Font, Typeface> sFontSet = new HashMap<FontFactory.Font, Typeface>();

	private final Context mContext;

	public FontFactory(Context context) {
		mContext = context;
	}

	public synchronized Typeface buildFont(Font font) {
		if(null == font) {
			return null;
		}
		
		Typeface typeface = sFontSet.get(font);
		if(null == typeface) {
			typeface = Typeface.createFromAsset(mContext.getAssets(), font.toString());
			sFontSet.put(font, typeface);
		}
		return typeface;
	}

}
