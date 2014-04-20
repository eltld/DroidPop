package com.droidpop.dict.offline;

public abstract class OfflineDBMetadata {

	public static final class Word {
		public static final String TABLE_NAME_WORD = "Word";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_WORD = "_word";
		public static final String COLUMN_LENGTH = "_length";
		public static final String COLUMN_PHONETIC = "_phonetic";
	}

	public static final class Paraphrase {
		public static final String TABLE_NAME_PARAPHRASE = "Paraphrase";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_WORD_ID = "_word_id";
		public static final String COLUMN_CATEGORE = "_categore";
		public static final String COLUMN_DETAIL = "_detail";
	}

	public static final class Demonstration {
		public static final String TABLE_NAME_DEMONSTRATION = "Demo";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_PARAPHRASE_ID = "_paraphrase_id";
		public static final String COLUMN_DEMONSTRATION = "_demo";
	}

	public static final class Statistics {
		public static final String TABLE_NAME_STATISTICS_EXTRA = "Extra";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_WORD = "_word";
		public static final String COLUMN_FREQUENCY = "_frequency";
		public static final String COLUMN_OCR_ERROR_COUNT = "_ocr_err_cnt";
		public static final String COLUMN_WIKI_CATEGORE = "_wiki_pro_categore";
	}

}
