package com.discover.mobile.bank.payees;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.widgets.ValidatedInputField;
import com.google.common.base.Strings;

/**
 * A sub-class of ValidatedInputField, an EditText based view that allows to validate
 * the text entered by a user. The method isValid() is used to determine whether
 * the text entered is valid or not. If the text entered is invalid then the textfield's
 * border is shown in read and the attach error label is shown (visibility changed from
 * gone to Visible). Otherwise, border is return to normal and error label is hidden.
 * 
 * A class that is owner of an instance of this class can use the isValid method to 
 * determine whether text entered has a minimum of 2 characters and non of the invalid 
 * characters "<>()&;'"[]{}" have been entered.
 * 
 * An error label must be attached via attachErrorLabel() in order to show or hide
 * an error message when the text is invalid.
 * 
 * @author henryoyuela
 *
 */
public class PayeeValidatedEditField extends ValidatedInputField {
	/**
	 * Pattern used to determine whether any of the invalid characters have been entered 
	 * by the user. It is used by the isValid() method
	 */
	public static Pattern INVALID_CHARACTERS = Pattern.compile("[<>\\(\\)&;\'\"\\[\\]{}]");
	/**
	 * Holds the minimum amount of characters allowed for this text field.
	 */
	private int minValue = 0;
	/**
	 * Holds the pattern for characters which are considered invalid for this text field.
	 */
	private Pattern pattern;
	/**
	 * Holds referent to a string resource identifier. If 0 then the default strings are used in isValid()
	 */
	private int errorTextResId = 0;
	/**
	 * Flag used to specify whether an inline error is being shown. If set to true isValid() returns false.
	 */
	private boolean showingError = false;
	
	/**
	 * Default constructor 
	 * @param context the context of use for the EditText.
	 */ 
	public PayeeValidatedEditField(final Context context) {
		super(context);	
	}

	public PayeeValidatedEditField(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
	}

	public PayeeValidatedEditField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Method used to set the minimum amount of characters for this text field. If length of string
	 * is not greater than or equal to the value, then the attached error label is displayed in red
	 * and the border of the text field is changed to red.
	 * 
	 * @param value Integer value which specifies minimum amount of characters allowed
	 */
	public void setMinimum(final int value) {
		minValue = value;
	}
	
	/**
	 * Method show the error label with the text provided.
	 * 
	 * @param text - Reference to a string that holds the error to be displayed to the user
	 */
	public void showErrorLabel(final int text) {
		if( errorLabel != null ) {
			this.errorLabel.setText(text);
			this.errorLabel.setVisibility(View.VISIBLE);
			this.showingError = true;
			this.updateAppearanceForInput();
		}
	}
	
	/**
	 * Method used to set what error string is displayed when text does not validate correctly.
	 * 
	 * @param text String Resource Identifier from res/string
	 */
	public void setError(final int text) {
		if( errorLabel != null ) {
			errorTextResId = text;
			this.errorLabel.setText(text);
		}
	}
	
	/**
	 * Method used to set the error string for an inline error label and make it visible.
	 * 
	 * @param view TextView that represents an inline error whose text will be set using the param text.
	 * @param text String to show to the user as an inline error
	 */
	public void showErrorLabel(final String text ) {
		if( errorLabel != null && !Strings.isNullOrEmpty(text)  ) {
			errorLabel.setText(text);
			errorLabel.setVisibility(View.VISIBLE);
			this.showingError = true;
			this.updateAppearanceForInput();
			this.showingError = true;
		}
	}
	
	/**
	 * Method used to set pattern for characters which are considered invalid for this text field.
	 * 
	 * @param pattern Reference to a pattern object which is to be used to generate a Matcher to validate the text
	 * 				  in this text field.
	 */
	public void setInvalidPattern(final Pattern pattern ) {
		this.pattern = pattern;
	}
	
	/**
	 * Method called by ValidatedInputField when evaluating whether the attached
	 * error label's visibility should be set to visible or gone. In addition, if this 
	 * method returns false the text field's border is changed to red.
	 */
	@Override
	public boolean isValid() {
		boolean valid = !showingError;
		
		if( valid ) {
			final String text = this.getText().toString();
			
			//Validate text field Is Not Empty or null
			valid = !Strings.isNullOrEmpty(text);
			
			//Validate that a minimum characters has been entered
			if( valid ) {
				valid &= (text.length() >= minValue);
			}
			
			//Validate non of the Invalid characters "<>()&;'"[]{}" have been entered
			if( valid ) {
				if( pattern != null ) {
					if( 0 == errorTextResId ) {
						//Error string to invalid characters so that if this condition fails this is the string displayed
						this.errorLabel.setText(R.string.bank_invalid_characters);
					} 
					
					final Matcher m = pattern.matcher(text);
					valid &= !m.find();
				}
			} else {
				if( 0 == errorTextResId ) {
					//Set error string to invalid number of characters 
					this.errorLabel.setText(R.string.bank_invalid_characters);
				}
			}
		}
		
				
		return valid;
	}
	
	/**
	 * Event handler used to clear focus from this EditText view, if user closes the keyboard by pressing back hardware key.
	 */
	@Override
	public boolean onKeyPreIme(final int keyCode, final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			this.clearFocus();

		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void clearErrors(){ 
		super.clearErrors();
		
		/**Reset flag indicating whether an inline error is being shown*/
		showingError = false;
	}
}
