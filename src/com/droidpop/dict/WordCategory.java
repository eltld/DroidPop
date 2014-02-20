package com.droidpop.dict;

import java.util.HashMap;
import java.util.Map;

public class WordCategory {
	public static final int NOUN = 0;
	public static final int VERB = 1;
	public static final int VERB_TRANSITIVE = 2;
	public static final int VERB_INTRANSITIVE = 3;

	private static Map<String, Integer> sCategory;
	static {
		sCategory = new HashMap<String, Integer>();
		
		// YouDao Dict
		sCategory.put("n.", NOUN);
		sCategory.put("vt.", VERB);
		sCategory.put("vi.", VERB_INTRANSITIVE);
	}
	
	public static int getCategory(String key) {
		return sCategory.get(key);
	}
}
