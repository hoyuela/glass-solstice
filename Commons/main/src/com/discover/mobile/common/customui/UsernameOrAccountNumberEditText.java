package com.discover.mobile.common.customui;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

import com.discover.mobile.common.auth.InputValidator;
import com.google.common.base.Strings;

/**
 * This class is an edit text which will function as either a Username field or an AccountNumber field.
 * Its functionality is toggled by using the setFieldAccountNumber or setFieldUsername methods.
 * 
 * This class contains functionality for both types of input fields because I could not find a
 * better solution for handling the input field changing between registration and forgot password.
 * 
 * @author scottseward
 *
 */
public class UsernameOrAccountNumberEditText extends ValidatedInputField{
	private boolean isUsernameField = true;
	
	private static final int EMS_SELECTED = 16;
	private static final int EMS_NOT_SELECTED = EMS_SELECTED;
	
	private static final int VALID_ACCOUNT_NUMBER_LENGTH = 16;
	private static final int MAX_USERNAME_LENGTH = 32;
	private static final String TAG = UsernameOrAccountNumberEditText.class.getSimpleName();
	/**
	 * Default constructors. Initially sets up the input field as a username field.
	 */
	public UsernameOrAccountNumberEditText(Context context) {
		super(context);
		setFieldUsername();
	}
	
	public UsernameOrAccountNumberEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFieldUsername();
	}
	
	public UsernameOrAccountNumberEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		setFieldUsername();
	}

	/**
	 * Change the input field to accept and validate a username. This is an alphanum input
	 * with no suggestions and a max length of 32 characters.
	 */
	public void setFieldUsername() {
		isUsernameField = true;
		setupUsernameInputRestrictions();
	}
	
	/**
	 * Change the input field to accept and validate an account number. This means it only
	 * accepts numbers up to the max account number length.
	 */
	public void setFieldAccountNumber() {
		isUsernameField = false;
		setupAccountNumberInputRestrictions();
	}
	
	/**
	 * Sets the max length of the input to the maximum account number length.
	 * Also restricts input to numbers only.
	 */
	private void setupAccountNumberInputRestrictions() {
		filterArray[0] = new InputFilter.LengthFilter(VALID_ACCOUNT_NUMBER_LENGTH);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_CLASS_PHONE);
	}

	/**
	 * Sets the max length of the input to the maximum username length.
	 * Also changes the input type to be alphanum
	 */
	private void setupUsernameInputRestrictions() {
		filterArray[0] = new InputFilter.LengthFilter(MAX_USERNAME_LENGTH);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
	}
	
	/**
	 * This method validates the current input for the set input field type. 
	 * If the input is valid, it returns true, otherwise false.
	 */
	@Override
	public boolean isValid(){
		if(isUsernameField)
			return isUsernameValid();
		else
			return isAccountNumberValid();
	}
	
	/**
	 * Returns true if the username is valid, in this case, if it is not empty or null.
	 * @return true if the input is not empty or null.
	 */
	public boolean isUsernameValid() {
		final String username = this.getText().toString();
		
		return !Strings.isNullOrEmpty(username);
	}
	
	/**
	 * Checks to see if the input looks like a valid account number. If it begins with 6011
	 * and is of length 16.
	 * 
	 * @return true if the input looks like a valid account number.
	 */
	public boolean isAccountNumberValid() {
		final String cardAccountNumber = this.getText().toString();
		
		return InputValidator.isCardAccountNumberValid(cardAccountNumber);
	}

	@Override
	protected int getEMSFocusedLength() {
		return EMS_SELECTED;
	}

	@Override
	protected int getEMSNotFocusedLength() {
		return EMS_NOT_SELECTED;
	}
}
