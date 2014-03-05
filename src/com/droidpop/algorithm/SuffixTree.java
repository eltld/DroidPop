package com.droidpop.algorithm;

public abstract class SuffixTree {
	
	public static final class SuffixTreeNode {
		// TODO: simply public attributes, usage like Point.x, Point.y, etc.
	}
	
	protected final SuffixTreeNode mRoot;
	
	/**
	 * default constructor
	 * 
	 * @param target
	 *            given target string
	 */
	protected SuffixTree(String target) {
		mRoot = new SuffixTreeNode();
	}
	
	// TODO: public methods provide features
	public SuffixTreeNode getRoot() {
		return mRoot;
	}
	
	// TODO: protected methods below are not visible to other package, but visible if same package
	
	// TODO: private methods never reached from outside
}
