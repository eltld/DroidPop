package com.droidpop.algorithm;

public interface SuffixTreeFactory {

	/**
	 * given a string, construct the SuffixTree
	 */
	public SuffixTree build(String target);

	/**
	 * given a string, using the built SuffixTree to match the common
	 * substrings.<br>
	 * 
	 * @param tree
	 *            the SuffixTree
	 * @param target
	 *            target string, <b>which can contain illegal character</b>
	 * @return common substrings
	 * 
	 * @see #build(String)
	 */
	public String[] getCommonSubstrings(SuffixTree tree, String target);

	/**
	 * given a string, using the built SuffixTree to match the longest common
	 * substrings.<br>
	 * 
	 * @param tree
	 *            the SuffixTree
	 * @param target
	 *            target string, <b>which can contain illegal character</b>
	 * @return the longest common substring
	 * 
	 * @see #build(String)
	 */
	public String getLongestCommonSubstrings(SuffixTree tree, String target);

	/**
	 * first, using str1#str2$ to {@link #build(String)} the SuffixTree,
	 * <b>where'#' and '$' denote NUL character ('\0') or other empty symbol,
	 * that is, the single character means a empty string.</b><br>
	 * then get all common substrings as possible as we can, and compute their
	 * matching degree.<b>
	 * 
	 * @return matching degree, witch is from 0 to 1.0f.
	 */
	public float match(String str1, String str2);
}
