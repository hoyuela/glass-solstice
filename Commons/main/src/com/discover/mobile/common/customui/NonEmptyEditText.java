package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

public class NonEmptyEditText extends ValidatedInputField {

	/**
	 * Default constructor
	 * @param context the context of use for the EditText.
	 */ 
	public NonEmptyEditText(final Context context) {
		super(context);	
	}
	
	public NonEmptyEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
	}
	
	public NonEmptyEditText(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isValid() {
		return this.length() > 0;
	}
	
}
