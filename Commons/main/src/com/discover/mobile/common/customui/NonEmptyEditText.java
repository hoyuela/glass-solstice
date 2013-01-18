package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

public class NonEmptyEditText extends ValidatedInputField {

	private final int EMS_FOCUSED = 16;
	private final int EMS_NOT_FOCUSED = 16;
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
	protected int getEMSFocusedLength() {
		return EMS_FOCUSED;
	}

	@Override
	protected int getEMSNotFocusedLength() {
		return EMS_NOT_FOCUSED;
	}

	@Override
	protected boolean isValid() {
		return this.length() > 0;
	}
	

}
