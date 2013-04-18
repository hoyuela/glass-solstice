package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

import com.google.common.base.Strings;

/**
 * A input field. That accepts an integer of length 4 or less.
 * It is validated on the number being greater than zero.
 * 
 * @author scottseward
 *
 */
public class PositiveIntegerEditText extends ValidatedInputField{

	private static final int DEFAULT_EMS = 4;

	public PositiveIntegerEditText(final Context context) {
		super(context);
	}

	public PositiveIntegerEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public PositiveIntegerEditText(final Context context, final AttributeSet attrs, final int defStyle){
		super(context, attrs, defStyle);
	}

	@Override
	public void setupDefaultAppearance() {
		super.setupDefaultAppearance();
		this.setEms(DEFAULT_EMS);
	}

	/**
	 * Restrict the input filed to accept only numbers to a maximum length of 4.
	 */
	@Override
	public void setupInputRestrictions() {
		filterArray[0] = new InputFilter.LengthFilter(DEFAULT_EMS);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	/**
	 * Checks the current input to see if it is of valid length.
	 * @return returns true if the input if of valid length.
	 */
	@Override
	public boolean isValid() {
		final String currentText = this.getText().toString();
		int inputValue = 0;
		
		if(!Strings.isNullOrEmpty(currentText)) {
			inputValue = Integer.parseInt(currentText);
		}
		
		return inputValue > 0;
	}

}
