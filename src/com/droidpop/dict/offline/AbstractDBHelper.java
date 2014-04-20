package com.droidpop.dict.offline;

import java.io.File;

import me.wtao.io.ExternalStorage;
import me.wtao.io.FileUtils;
import android.content.Context;

import com.droidpop.dict.WordEntryReader;

public abstract class AbstractDBHelper implements WordEntryReader {
	
	private static final String ASSETS_DB_DIR = "db/";
	
	public static boolean exportDBFile(Context context, String db, String dir, boolean external) {
		if(external) {
			if(ExternalStorage.isExternalStorageWritable()) {
				File target = ExternalStorage.getExternalStorageDirectory(dir);
				if(target != null) {
					FileUtils.copyFromAssets(context, new File(target, db).getAbsolutePath(),  ASSETS_DB_DIR + db);
					return true;
				}
			}
		} else {
			File target = context.getDatabasePath(db); // TODO:
			if(target != null) {
				FileUtils.copyFromAssets(context, target.getAbsolutePath(),  ASSETS_DB_DIR + db);
				return true;
			}
		}
		
		return false;
	}
	
	

}
