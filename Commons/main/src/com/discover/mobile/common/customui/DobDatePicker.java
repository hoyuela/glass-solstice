package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.R;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.utils.CommonUtils;

/**
 * The date of birth date picker.
 * This is a stylized edit text field. It is attached to a date picker dialog that is responsible
 * for setting the selected day, month, and year values.
 * 
 * @author scottseward
 *
 */
public class DobDatePicker extends ValidatedInputField{
	private final static int GOOD_YEAR_LENGTH = 4;
	private final static int MAX_MONTH = 12;
	private final static int MIN_MONTH = 1;
	
	private final static int EMS_LENGTH = 11;
	
	private final static int MAX_DAY = 31;
	private final static int MIN_DAY = MIN_MONTH;
	
	private final static int INVALID_VALUE = -1;
	
	private int dobDay = INVALID_VALUE;
	private int dobMonth = INVALID_VALUE;
	private int dobYear = INVALID_VALUE;
	
	public DobDatePicker(Context context) {
		super(context);
	}
	
	public DobDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DobDatePicker(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	/**
	 * Checks to see if the set DOB day, month, and year are valid.
	 * @return true if the day, month, and year are all valid.
	 */
	@Override
	public boolean isValid() {
		return isDobYearValid() && isDobMonthValid() && isDobDayValid();
	}
	
	/**
	 * Checks to see if the set DOB Year is a valid year value.
	 * @return true if the DOB year is valid.
	 */
	private boolean isDobYearValid() {
		return InputValidator.isYearValid(this.dobYear);
	}
	
	/**
	 * Checks to see if the set DOB Month is a valid month value.
	 * @return true if the DOB month is valid.
	 */
	private boolean isDobMonthValid() {
		return InputValidator.isMonthValid(this.dobMonth);
	}
	
	/**
	 * Checks to see if the set DOB Day is a valid day value.
	 * @return true if the DOB day is valid.
	 */
	private boolean isDobDayValid() {
		return InputValidator.isDayValid(dobDay);
	}

	/**
	 * Setup the default appearance of this input field to look like
	 * a date picker. Same as default but with hint text and the down arrow in the right drawable.
	 */
	@Override
	protected void setupDefaultAppearance(){
		super.setupDefaultAppearance();
		this.setHint(R.string.birth_date_placeholder);
		this.setEms(EMS_LENGTH);
		this.setFocusable(false);

		this.setCompoundDrawablesWithIntrinsicBounds(null, null, getDownArrow(), null);
	}
	
	/**
	 * If the date picker has valid values, update its label to a formatted date with those
	 * values.
	 */
	public void updateLabelWithSavedDate(){
		if(this.isValid())
			this.setText( CommonUtils.getFormattedDate(dobMonth, dobDay, dobYear) );
	}

	/**
	 * Sets all of the member data to invalid values.
	 */
	public void clearData() {
		dobDay = INVALID_VALUE;
		dobYear = INVALID_VALUE;
		dobMonth = INVALID_VALUE;
	}
	
	/**
	 * Getters and setters
	 * 
	 */
	public int getDobDay() {
		return this.dobDay;
	}
	
	public int getDobYear() {
		return this.dobYear;
	}
	
	public int getDobMonth() {
		return this.dobMonth;
	}
	
	public void setDobMonth(final int month){
		this.dobMonth = month;
	}
	
	public void setDobYear(final int year) {
		this.dobYear = year;
	}
	
	public void setDobDay(final int day) {
		this.dobDay = day;
	}
}

