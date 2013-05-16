package com.discover.mobile.common.ui.widgets;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;

import com.discover.mobile.common.R;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * The parent class for any date picker 
 * @author scottseward
 *
 */
public abstract class CustomDatePickerElement extends ValidatedInputField {
	protected Context currentContext;

	protected CustomDatePickerDialog attachedDatePickerDialog;

	protected final int INVALID_VALUE = -1;

	private int day = INVALID_VALUE;
	private int month = INVALID_VALUE;
	private int year = INVALID_VALUE;

	public CustomDatePickerElement(final Context context) {
		super(context);
		defaultSetup(context);
	}

	public CustomDatePickerElement(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		defaultSetup(context);

	}

	public CustomDatePickerElement(final Context context, final AttributeSet attrs, final int defStyle){
		super(context, attrs, defStyle);
		defaultSetup(context);

	}

	protected void defaultSetup(final Context context) {
		currentContext = context;
		setupDatePickerDialog();
		setupOnTouchListener();
		setupOnClickListener();

		this.setCursorVisible(false);
		this.setKeyListener(null);
	}
	
	abstract protected int getTitleText();

	/**
	 * This allows support for clicking the elemnt rather than touching it.
	 * So that if the field is focused and a physical button is pressed to 'click'
	 * the field, the dialog will show up.
	 */
	protected void setupOnClickListener() {
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				attachedDatePickerDialog.show();
			}
		});
	}

	/**
	 * When the element is tapped, launch the date picker dialog.
	 */
	private void setupOnTouchListener() {

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
					attachedDatePickerDialog.show();
				return false;
			}
		});
	}
	/**
	 * If the date picker gets focus and the loses it, validate the field.
	 * If the date is invalid, set the error state.
	 * If the date is valid, clear the error state.
	 */
	@Override
	protected void setupFocusChangedListener() {
		this.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if(!hasFocus){
					if (!isValid())
						setErrors();
					else
						clearErrors();
				}

			}
		});
	}


	protected void setupDatePickerDialog() {
		final Calendar currentDate = Calendar.getInstance();

		final int currentYearMinusEighteen = currentDate.get(Calendar.YEAR) - getYearOffset();
		final int currentDay = currentDate.get(Calendar.DAY_OF_MONTH) - getDayOffset();
		final int currentMonth = currentDate.get(Calendar.MONTH) - getMonthOffset();

		attachedDatePickerDialog = new CustomDatePickerDialog(currentContext, new OnDateSetListener() {

			@Override
			public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
				setDay(dayOfMonth);
				setMonth(monthOfYear);
				setYear(year);
				updateLabelWithSavedDate();
				updateAppearanceForInput();
			}
		}, currentYearMinusEighteen, currentMonth, currentDay);

		final String dobPickerTitle = getResources().getString(getTitleText());
		attachedDatePickerDialog.setTitle(dobPickerTitle);	

	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param day the day to set
	 */
	public void setDay(final int day) {
		this.day = day;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(final int month) {
		this.month = month;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(final int year) {
		this.year = year;
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
	 * Sets all of the member data to invalid values.
	 */
	public void clearData() {
		day = INVALID_VALUE;
		year = INVALID_VALUE;
		month = INVALID_VALUE;
	}

	/**
	 * If the date picker has valid values, update its label to a formatted date with those
	 * values.
	 */
	public void updateLabelWithSavedDate(){
		if(this.isValid())
			this.setText( CommonUtils.getFormattedDate(getMonth(), getDay(), getYear()) );
	}


	/**
	 * Checks to see if the set DOB Year is a valid year value.
	 * @return true if the DOB year is valid.
	 */
	protected boolean isYearValid() {
		return InputValidator.isYearValid(getYear());
	}

	/**
	 * Checks to see if the set DOB Month is a valid month value.
	 * @return true if the DOB month is valid.
	 */
	protected boolean isMonthValid() {
		return InputValidator.isMonthValid(getMonth());
	}

	/**
	 * Setup the default appearance of this input field to look like
	 * a date picker. Same as default but with hint text and the down arrow in the right drawable.
	 */
	@Override
	public void setupDefaultAppearance(){
		super.setupDefaultAppearance();
		this.setHint(getPlaceholderText());
		this.setEms(DATE_PICKER_EMS_LENGTH);

		this.setCompoundDrawablesWithIntrinsicBounds(null, null, getDownArrow(), null);
	}

	/**
	 * Checks to see if the set DOB Day is a valid day value.
	 * @return true if the DOB day is valid.
	 */
	protected boolean isDayValid() {
		return InputValidator.isDayValid(getDay());
	}

	/**
	 * Return the default placeholder text.
	 * @return a string resource representing the default placeholder text.
	 */
	protected int getPlaceholderText(){
		return R.string.date_placeholder;
	}

	/**
	 * Checks to see if the set DOB day, month, and year are valid.
	 * @return true if the day, month, and year are all valid.
	 */
	@Override
	public boolean isValid() {
		return isYearValid() && isMonthValid() && isDayValid();
	}

	protected int getYearOffset(){
		return 0;
	}
	protected int getDayOffset() {
		return 0;
	}
	protected int getMonthOffset(){
		return 0;
	}

}
