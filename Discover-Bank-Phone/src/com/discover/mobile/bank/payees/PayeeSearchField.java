package com.discover.mobile.bank.payees;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.AttributeSet;

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
public class PayeeSearchField extends ValidatedInputField {
	/**
	 * Pattern used to determine whether any of the invalid characters have been entered 
	 * by the user. It is used by the isValid() method
	 */
	public static Pattern INVALIDCHARACTERS = Pattern.compile("[<>\\(\\)&;\'\"\\[\\]{}]");
	/**
	 * Default constructor 
	 * @param context the context of use for the EditText.
	 */ 
	public PayeeSearchField(final Context context) {
		super(context);	
	}

	public PayeeSearchField(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
	}

	public PayeeSearchField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Method called by ValidatedInputField when evaluating whether the attached
	 * error label's visibility should be set to visible or gone. In addition, if this 
	 * method returns false the text field's border is changed to red.
	 */
	@Override
	public boolean isValid() {
		boolean valid = false;
		final String text = this.getText().toString();
		
		//Validate text field Is Not Empty or null
		valid = !Strings.isNullOrEmpty(text);
		
		//Validate that a minimum of 2 characters has been entered
		if( valid ) {
			valid &= (text.length() >= 2);
		}
		
		//Validate non of the Invalid characters "<>()&;'"[]{}" have been entered
		if( valid ) {
			//Error string to invalid characters so that if this condition fails this is the string displayed
			this.errorLabel.setText(R.string.bank_invalid_characters);
			
			final Matcher m = INVALIDCHARACTERS.matcher(text);
			valid &= !m.find();
		} else {
			//Set error string to invalid number of characters 
			this.errorLabel.setText(R.string.bank_invalid_num_characters);
		}
		
		
		return valid;
	}
}
