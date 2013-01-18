package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.auth.InputValidator;

public class EmailEditText extends ValidatedInputField {
	private final int EMS_DEFAULT = 16;
	public EmailEditText(Context context) {
		super(context);
	}
	
	public EmailEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public EmailEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	@Override
	protected int getEMSFocusedLength() {
		// TODO Auto-generated method stub
		return EMS_DEFAULT;
	}

	@Override
	protected int getEMSNotFocusedLength() {
		// TODO Auto-generated method stub
		return EMS_DEFAULT;
	}

	@Override
	public boolean isValid() {
		return InputValidator.isEmailValid(this.getText().toString());
	}
	
}
