package com.droidpop.dict.youdao;

import com.google.gson.annotations.SerializedName;

class YouDaoJsonResponse {
	@SerializedName("errorCode")
	public int errorCode;
	
	public class Web {
		@SerializedName("value")
		public String value;
		@SerializedName("key")
		public String key;
	}
	
	@SerializedName("translation")
	public String translation;
	
	public class Basic {
		public class Explains {
			
		}
		@SerializedName("phonetic")
		public String phonetic;
	}
	
	@SerializedName("query")
	public String query;
}
