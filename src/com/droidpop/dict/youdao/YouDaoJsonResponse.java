package com.droidpop.dict.youdao;

import me.wtao.lang.reflect.Reflect;

/**
 * <b>YouDao APIv1.1 json response, f.e.</b><br>
 * <br>
 * 
 * {<br>
 * &nbsp&nbsp "errorCode": 0,<br>
 * &nbsp&nbsp "web": [<br>
 * &nbsp&nbsp&nbsp&nbsp {<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "value": [<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "你好",<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "您好",<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "哈啰"<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp ],<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "key": "Hello"<br>
 * &nbsp&nbsp&nbsp&nbsp },<br>
 * &nbsp&nbsp&nbsp&nbsp {<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "value": [<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "凯蒂猫",<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "昵称",<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "凯帝猫"<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp ],<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "key": "Hello Kitty"<br>
 * &nbsp&nbsp&nbsp&nbsp },<br>
 * &nbsp&nbsp&nbsp&nbsp {<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "value": [<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "你好"<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp ],<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "key": "Hello Fred"<br>
 * &nbsp&nbsp&nbsp&nbsp }<br>
 * &nbsp&nbsp ],<br>
 * &nbsp&nbsp "translation": [<br>
 * &nbsp&nbsp&nbsp&nbsp "你好"<br>
 * &nbsp&nbsp ],<br>
 * &nbsp&nbsp "basic": {<br>
 * &nbsp&nbsp&nbsp&nbsp "explains": [<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "n. 表示问候， 惊奇或唤起注意时的用语",<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "int. 喂；哈罗",<br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp "n. (Hello)人名；(法)埃洛"<br>
 * &nbsp&nbsp&nbsp&nbsp ],<br>
 * &nbsp&nbsp&nbsp&nbsp "phonetic": "hə'ləʊ; he-"<br>
 * &nbsp&nbsp },<br>
 * &nbsp&nbsp "query": "hello"<br>
 * }<br>
 * <br>
 * 
 * Using Gson to serializing or deserializing, and access the field by getters;
 * <b>Note that inner class should be static, more info. on <a
 * href="https://sites.google.com/site/gson/gson-user-guide"
 * >gson-user-guide</a>.</b><br>
 * 
 */
class YouDaoJsonResponse {

	public class Web {
		private String[] value;
		private String key;

		public String[] getValue() {
			return value;
		}

		public String getKey() {
			return key;
		}
		
		@Override
		public String toString() {
			return Reflect.toString(Web.this);
		}
	}

	public static class Basic {
		private String phonetic;
		private String[] explains;

		public String getPhonetic() {
			return phonetic;
		}

		public String[] getExplains() {
			return explains;
		}
		
		@Override
		public String toString() {
			return Reflect.toString(Basic.this);
		}
	}

	private String[] translation;
	private Basic basic;
	private String query;
	private Integer errorCode;
	private Web[] web;

	public String[] getTranslation() {
		return translation;
	}

	public Basic getBasic() {
		return basic;
	}

	public String getQuery() {
		return query;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Web[] getWeb() {
		return web;
	}
	
	@Override
	public String toString() {
		return Reflect.toString(YouDaoJsonResponse.this);
	}

}
