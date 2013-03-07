package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.ui.widgets.ValidatedInputField;

public class AmountValidatedEditField extends ValidatedInputField {

	public AmountValidatedEditField(final Context context) {
		super(context);	
	}

	public AmountValidatedEditField(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
	}

	public AmountValidatedEditField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
