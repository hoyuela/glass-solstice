package com.discover.mobile.common.customui;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * A SSN input field. Accepts the last 4 digits of a user's ssn number.
 *
 * @author scottseward
 *
 */
public class SsnEditText extends ValidatedInputField{

	private int ssn;
	
	private final static int GOOD_SSN_LENGTH = 4;
	
	public SsnEditText(Context context) {
		super(context);
	}
	
	public SsnEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SsnEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	/**
	 * Restrict the input filed to accept only numbers to a maximum length of 4.
	 */
	@Override
	public void setupInputRestrictions() {
		this.setMinEms(GOOD_SSN_LENGTH);
		this.setMaxEms(GOOD_SSN_LENGTH);
		filterArray[0] = new InputFilter.LengthFilter(GOOD_SSN_LENGTH);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_CLASS_PHONE);
	}
	
	/**
	 * Checks the current input to see if it is of valid length.
	 * @return returns true if the input if of valid length.
	 */
	@Override
	public boolean isValid() {
		final int ssnLength = this.getText().toString().length();

		if(ssnLength == GOOD_SSN_LENGTH)
			return true;
		else
			return false;
	}

	/**
	 * Return a saved ssn number.
	 * @return an integer representing the last 4 digits of an ssn number.
	 */
	public int getSsn() {
		return ssn;
	}
	
	/**
	 * Save a ssn number to the member ssn variable.
	 * @param ssn an integer representing the last 4 digits of an ssn number.
	 */
	public void setSsn(final int ssn) {
		this.ssn = ssn;
	}
}
