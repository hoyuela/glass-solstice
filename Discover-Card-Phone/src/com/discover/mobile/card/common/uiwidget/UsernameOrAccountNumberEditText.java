package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.discover.mobile.card.common.InputValidator;
import com.discover.mobile.common.utils.CommonUtils;

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
	private static final int MAX_USERNAME_LENGTH = 16;
	private static final int MIN_USERNAME_LENGTH = 6;

	private int lengthBefore = 0;

	private int cursorStartPosition = 0;
	private int cursorEndPosition = 0;

	private boolean needsToRestore = false;
	private boolean isDeleting = false;

	/**
	 * Default constructors. Initially sets up the input field as a username field.
	 */
	public UsernameOrAccountNumberEditText(final Context context) {
		super(context);
		setFieldUsername();
	}

	public UsernameOrAccountNumberEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setFieldUsername();
	}

	public UsernameOrAccountNumberEditText(final Context context, final AttributeSet attrs, final int defStyle){
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
	public void setupDefaultAppearance() {
		super.setupDefaultAppearance();
		this.setEms(DEFAULT_EMS);
	}
	/**
	 * Listens for hardware keyboard inputs and stylizes the input for account numbers.
	 * 
	 * When text is changed in the input field it has to do the following.
	 * Save the current input that has just changed and get the properly formatted version of the current input.
	 * Compare the current input to a properly formatted version of the current input.
	 * If they differ in formatting, restore the input field with the properly formatted String.
	 * Then restore the position of the cursor.
	 * 
	 * This also needs to know when a deletion is being made, by comparing before and after lengths of the String.
	 * Also it needs to know where spaces are being inserted so that it can account for cursor position.
	 */
	private void setupInputStylizer() {
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(final Editable s) {
				restoreCursorPosition();
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
				lengthBefore = s.length();
			}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				final String currentText = s.toString();
				String currentTextStylized = 
						CommonUtils.getStringWithSpacesEvery4Characters(CommonUtils.getSpacelessString(currentText));
				//remove the trailing space at the end of the number
				if(currentTextStylized.length() == VALID_ACCOUNT_NUMBER_LENGTH + 1)
					currentTextStylized = currentTextStylized.trim();

				final int lengthAfter = currentTextStylized.length();

				if(currentTextStylized.length() <= VALID_ACCOUNT_NUMBER_LENGTH && !currentText.equals(currentTextStylized)){
					saveCursorPosition();
					if(lengthBefore > lengthAfter)
						isDeleting = true;

					updateInputWithString(currentTextStylized);
				}
			}

		});

	}

	/**
	 * Save the current position of the text cursor.
	 */
	private void saveCursorPosition() {
		cursorStartPosition = this.getSelectionStart();
		cursorEndPosition = this.getSelectionEnd();
		needsToRestore = true;
	}

	/**
	 * Restore the position of the text cursor.
	 */
	private void restoreCursorPosition() {
		if(needsToRestore){
			//If were are at a space and are NOT deleting a character,
			//increment the cursor position so that it restores to the right spot 
			//ahead of the space.
			if(cursorStartPosition % 5 == 0 && !isDeleting){
				cursorStartPosition ++;
				cursorEndPosition ++;
			}

			isDeleting = false;

			this.setSelection(cursorStartPosition, cursorEndPosition);
			needsToRestore = false;
		}
	}

	/**
	 * Updates the current input to the passed String parameter.
	 * @param newInput
	 */
	private void updateInputWithString(final String newInput) {
		this.setText(newInput);
	}

	/**
	 * Sets the max length of the input to the maximum account number length.
	 * Also restricts input to numbers only.
	 */
	private void setupAccountNumberInputRestrictions() {
		filterArray[0] = new InputFilter.LengthFilter(VALID_ACCOUNT_NUMBER_LENGTH);
		this.setFilters(filterArray);
		this.setInputType(InputType.TYPE_CLASS_NUMBER);
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
		final int usernameLength = this.length();

		return InputValidator.valueBoundedBy(usernameLength, MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH);
	}

	/**
	 * Checks to see if the input looks like a valid account number. If it begins with 6011
	 * and is of length 16.
	 * 
	 * @return true if the input looks like a valid account number.
	 */
	public boolean isAccountNumberValid() {
		final String cardAccountNumber = this.getText().toString();

		return InputValidator.validateCardAccountNumber(CommonUtils.getSpacelessString(cardAccountNumber));
	}

}
