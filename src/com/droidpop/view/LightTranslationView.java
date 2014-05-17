package com.droidpop.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.droidpop.R;
import com.droidpop.config.FontFactory;
import com.droidpop.config.FontFactory.Font;
import com.droidpop.dict.WordEntry;
import com.droidpop.dict.WordEntryReader;

public class LightTranslationView extends InstantTranslationView {

	private View mContentView;
	private TextView mTranslationView;
	
	public LightTranslationView(Context context) {
		super(context);
		
		mContentView = setContentView(R.layout.layout_light_translation_content);
		
		mTranslationView = (TextView) mContentView.findViewById(R.id.light_translation);
		FontFactory factory = new FontFactory(context);
		mTranslationView.setTypeface(factory.buildFont(Font.ROBOTO_LIGHT));
	}
	
	public void setTranslation(WordEntry entry) {
		if(entry.getStatus() == WordEntryReader.Status.SUCCESS) {
//			ArrayList<WeakReference<Paraphrase>> paraphraseRefs = entry.getParaphrasesBy(WordCategory.BASIC_CATEGORY);
//			StringBuilder sb = new StringBuilder();
//			final char NEW_LINE = '\n';
//			for(WeakReference<Paraphrase> ref : paraphraseRefs) {
//				Paraphrase paraphrase = ref.get();
//				if(paraphrase != null) {
//					sb.append(paraphrase.getDetail());
//					sb.append(NEW_LINE);
//				}
//			}
//			
//			String text = sb.toString().trim();
//			mTranslationView.setText(text);
			
			mTranslationView.setText(entry.getBasicParaphrase().getDetail());
		}
	}

}
