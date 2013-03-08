package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.ui.widgets.ValidatedInputField;

/**
* A sub-class of ValidatedInputField, an EditText based view that allows to validate
* the text entered by a user. The method isValid() is used to determine whether
* the text entered is valid or not. If the text entered is invalid then the textfield's
* border is shown in read and the attach error label is shown (visibility changed from
* gone to Visible). Otherwise, border is return to normal and error label is hidden.
* 
* A class that is owner of an instance of this class can use the isValid method to 
* determine whether text entered has a minimum of 2 characters and non of the invalid 
* characters "<>()&;'"[]{}" have been entered.
* 
* An error label must be attached via attachErrorLabel() in order to show or hide
* an error message when the text is invalid.
* 
* @author henryoyuela
*
*/
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
		//TO BE DEFINED IN ANOTHER PULL REQUEST
		return false;
	}

}
