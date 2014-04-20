package com.droidpop.dict.wordnet;

import com.droidpop.dict.offline.OfflineDBMetadata;

public final class WordNetDBMetadata extends OfflineDBMetadata {

	public static final class Participle {
		public static final String TABLE_NAME_PARTICIPLE = "Participle";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_WORD_ID = "_word_id";
		public static final String COLUMN__PARTICIPLE = "_participle";
	}

	public static final class Flag {
		public static final String TABLE_NAME_FLAG = "Flag";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_PARAPHRASE_ID = "_paraphrase_id";
		public static final String COLUMN_FLAG = "_flag";
		public static final String COLUMN_EXTRA = "_extra";
	}

}
