package com.discover.mobile.common.customui;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.common.R;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.utils.CommonUtils;

/**
 * The card expiration date picker.
 * This is a stylized edit text field. It is attached to a date picker dialog that is responsible
 * for setting the selected month and year values.
 * 
 * @author scottseward
 *
 */
public class CardExpirationDatePicker extends ValidatedInputField{

	private static final int EMS_LENGTH = 10;
	
	private static final int INVALID_VALUE = -1;
	
	private int expirationMonth = INVALID_VALUE;
	private int expirationYear = INVALID_VALUE;
		
	public CardExpirationDatePicker(Context context) {
		super(context);
	}
	
	public CardExpirationDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CardExpirationDatePicker(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	/**
	 * Checks to see if the saved expiration date is valid.
	 * 
	 * @return true if the month and year member variables are valid.
	 */
	@Override
	public boolean isValid() {
		return  isExpirationMonthValid() && isExpirationYearValid();
	}
	
	/**
	 * Checks to see if the member variable containing the expiration month is valid.
	 * 
	 * @return true if the expiration month is valid.
	 */
	private boolean isExpirationMonthValid() {
		return InputValidator.isMonthValid(expirationMonth);
	}
	
	/**
	 * Checks to see if the member variable containing the expiration year is valid.
	 * 
	 * @return true if the expiration year is valid.
	 */
	private boolean isExpirationYearValid() {
		return InputValidator.isYearValid(expirationYear);
	}
	
	/**
	 * Setup the default apperance of the input field. Beyond the normal setup, a down arrow
	 * drawable is set on the right, along with hint text.
	 */
	@Override
	protected void setupDefaultAppearance(){
		super.setupDefaultAppearance();
		
		this.setHint(R.string.exp_date_placeholder);
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
			this.setText(CommonUtils.getFormattedDate(expirationMonth, expirationYear));
	}
		
	/**
	 * Sets all of the member data to invalid values.
	 */
	public void clearData() {
		expirationMonth = INVALID_VALUE;
		expirationYear = INVALID_VALUE;
	}
	
	/**
	 * Getters and setters
	 * 
	 */
	public int getExpirationMonth() {
		return expirationMonth;
	}
	
	public int getExpirationYear() {
		return expirationYear;
	}
	
	public void setExpirationMonth(final int expirationMonth) {
		this.expirationMonth = expirationMonth;
	}
	
	public void setExpirationYear(final int expirationYear) {
		this.expirationYear = expirationYear;
	}
	
}
