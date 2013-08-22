package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.discover.mobile.card.common.InputValidator;
import com.google.common.base.Strings;

public class EmailEditText extends ValidatedInputField {

    public EmailEditText(final Context context) {
        super(context);
    }

    public EmailEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailEditText(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isValid() {
        return InputValidator.validateEmail(this.getText().toString());
    }
    
    /* 13.4 Baclklog items start*/
    
	@Override
	protected void showErrorLabel() {
		// TODO Auto-generated method stub
		if (errorLabel != null) {
			if (isNull()) {
				errorLabel.setVisibility(View.GONE);
			} else
			{
				errorLabel.setVisibility(View.VISIBLE);
			}
		}

	}
	  /*    13.4 Baclklog items end*/
}
