package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.auth.InputValidator;

public class EmailEditText extends ValidatedInputField {

	public EmailEditText(final Context context) {
		super(context);
	}

	public EmailEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public EmailEditText(final Context context, final AttributeSet attrs, final int defStyle){
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isValid() {
		return InputValidator.isEmailValid(this.getText().toString());
	}
}
