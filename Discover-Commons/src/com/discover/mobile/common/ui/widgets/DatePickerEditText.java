package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.R;

/**
 * The date of birth date picker.
 * This is a stylized edit text field. It is attached to a date picker dialog that is responsible
 * for setting the selected day, month, and year values. 
 * 
 * @author scottseward
 *
 */
public class DatePickerEditText extends CustomDatePickerElement {
	/** Moves the default year to 35 years in the past.*/
	protected final int DOB_YEAR_OFFSET = 35;

	public DatePickerEditText(final Context context) {
		super(context);
	}

	public DatePickerEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public DatePickerEditText(final Context context, final AttributeSet attrs, final int defStyle){
		super(context, attrs, defStyle);
	}

	@Override
	protected int getYearOffset() {
		return DOB_YEAR_OFFSET;
	}

	@Override
	protected int getTitleText() {
		return R.string.account_info_dob_text;
	}

}
