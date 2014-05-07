package com.droidpop.ocr;

public interface OnOcrRecognitionListener {
	
	public boolean isRapidOcr();
	public void onRecognized(String text, boolean confidence);

}
