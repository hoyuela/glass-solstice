package com.discover.mobile.common.customui;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

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
	
	private static final int INVALID_VALUE = -1;
	
	private int expirationMonth = INVALID_VALUE;
	private int expirationYear = INVALID_VALUE;
	
	private Context currentContext;
	
	private CustomDatePickerDialog attachedDatePickerDialog;
		
	public CardExpirationDatePicker(Context context) {
		super(context);
		defaultSetup(context);
	}
	
	public CardExpirationDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultSetup(context);
	}
	
	public CardExpirationDatePicker(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		defaultSetup(context);
	}
	
	/**
	 * Initial setup for the date picker.
	 * @param context
	 */
	private void defaultSetup(final Context context) {
		currentContext = context;
		setupDatePickerDialog();
		setupOnClickListener();

		this.setCursorVisible(false);
		this.setKeyListener(null);
	}
	
	/**
	 * When the element is clicked, launch the date picker dialog.
	 */
	private void setupOnClickListener() {
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attachedDatePickerDialog.show();
			}
		});
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
	 * Setup the focus changed listeners so that we can focus this item and have it remain looking
	 * like a date picker
	 */
	@Override
	protected void setupFocusChangedListener() {
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					attachedDatePickerDialog.show();
			}
		});
	}
	
	private void setupDatePickerDialog() {
		final Calendar currentDate = Calendar.getInstance();

		final int NOT_NEEDED = 1;
		final int currentMonth = currentDate.get(Calendar.MONTH);
		final int currentYearPlusTwo = currentDate.get(Calendar.YEAR) + 2;
		
		attachedDatePickerDialog = new CustomDatePickerDialog(currentContext, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				setExpirationMonth(monthOfYear);
				setExpirationYear(year);
				updateLabelWithSavedDate();
				updateAppearanceForInput();
			}
		}, currentYearPlusTwo, currentMonth, NOT_NEEDED);
		
		final String datePickerTitle = getResources().getString(R.string.card_expiration_date_text);
		attachedDatePickerDialog.setTitle(datePickerTitle);
		attachedDatePickerDialog.hideDayPicker();
	}
	
	/**
	 * Setup the default apperance of the input field. Beyond the normal setup, a down arrow
	 * drawable is set on the right, along with hint text.
	 */
	@Override
	protected void setupDefaultAppearance(){
		super.setupDefaultAppearance();
		
		this.setHint(R.string.exp_date_placeholder);
		this.setEms(DATE_PICKER_EMS_LENGTH);
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
	
	@Override
	protected Drawable getRedX() {
		return getGrayX();
	}
	
	@Override
	protected Drawable getGrayX() {
		return getDownArrow();
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
