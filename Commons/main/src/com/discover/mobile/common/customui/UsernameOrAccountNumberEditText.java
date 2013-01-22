package com.discover.mobile.common.customui;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.discover.mobile.common.CommonMethods;
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
	
	private static final int DEFAULT_EMS = 20;
	
	private static final int VALID_ACCOUNT_NUMBER_LENGTH = 19;
	private static final int MAX_USERNAME_LENGTH = 32;

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
		setupInputStylizer();
	}

	@Override
	protected void setupDefaultAppearance() {
		super.setupDefaultAppearance();
		this.setEms(DEFAULT_EMS);
	}
	/**
	 * Listens for hardware keyboard inputs and stylizes the input for account numbers.
	 */
	private void setupInputStylizer() {
		
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String currentText = s.toString();
				String currentTextStylized = 
						CommonMethods.getStringWithSpacesEvery4Characters(CommonMethods.getSpacelessString(currentText));
				if(currentTextStylized.length() < 19 && !currentText.equals(currentTextStylized)){
					updateInputWithString(currentTextStylized);
					setCursorPositionToEnd();
				}
			}
			
		});

	}
	
	/**
	 * Returns the number of space characters that occur before a given position in the field.
	 * @param position
	 * @return
	 */
	public int numberOfSpacesBeforePosition(final int position) {
		int numberOfSpaces = 0;
		String currentInput = getInputText();
		for(int i = 0; i < position; ++i){
			if(currentInput.charAt(i) == ' ')
				numberOfSpaces += 1;
		}
		
		return numberOfSpaces;
	}
	
	/**
	 * Updates the current input to the passed String parameter.
	 * @param newInput
	 */
	private void updateInputWithString(final String newInput) {
		this.setText(newInput);
	}
	
	/**
	 * Return the current text in the field.
	 * @return
	 */
	private String getInputText(){
		return this.getText().toString();
	}
	
	/**
	 * Sets the max length of the input to the maximum account number length.
	 * Also restricts input to numbers only.
	 */
	private void setupAccountNumberInputRestrictions() {
		filterArray[0] = new InputFilter.LengthFilter(VALID_ACCOUNT_NUMBER_LENGTH);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_CLASS_PHONE);
		setupDefaultHeight();
	}

	/**
	 * Sets the max length of the input to the maximum username length.
	 * Also changes the input type to be alphanum
	 */
	private void setupUsernameInputRestrictions() {
		filterArray[0] = new InputFilter.LengthFilter(MAX_USERNAME_LENGTH);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		setupDefaultHeight();
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
		
		return InputValidator.isCardAccountNumberValid(CommonMethods.getSpacelessString(cardAccountNumber));
	}
	
    /**
     * Sets the text cursor position to the end of the field. 
     */
    private void setCursorPositionToEnd() {
    	this.setSelection(this.length(), this.length());
    }
	    
}
