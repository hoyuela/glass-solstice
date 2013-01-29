package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * The date of birth date picker.
 * This is a stylized edit text field. It is attached to a date picker dialog that is responsible
 * for setting the selected day, month, and year values.
 * 
 * @author scottseward
 *
 */
public class DatePickerEditText extends CustomDatePickerElement {
	protected final int DOB_YEAR_OFFSET = 35;

	public DatePickerEditText(Context context) {
		super(context);
		defaultSetup(context);
	}
	
	public DatePickerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultSetup(context);

	}
	
	public DatePickerEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		defaultSetup(context);
	}
	
	@Override
	protected int getYearOffset() {
		return DOB_YEAR_OFFSET;
	}

}
