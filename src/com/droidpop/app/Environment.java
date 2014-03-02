package com.droidpop.app;

public final class Environment {
	/**
	 * 
	 * @return get arch type, <b>armeabi, armeabi-v7a, mips, x86</b> or
	 *         <b>unkown</b>
	 */
	public static String checkAbi() {
		return nativeCheckAbi();
	}

	private native static String nativeCheckAbi();

	private Environment() {

	}
	
	static {
		System.loadLibrary("env");
	}
}
