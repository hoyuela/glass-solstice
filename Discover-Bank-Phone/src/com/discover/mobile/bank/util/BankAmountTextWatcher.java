package com.discover.mobile.bank.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.common.base.Strings;

/**
 * TextWatcher to format text as the user types, only allowing numerical input. 
 * This TextWatcher will update the associated EditTect and add decimals to the field if user does not input decimals
 * add commas after every three numbers and only allow a maximum of 8 characters including decimal point. The
 * maximum number that a user can enter is 99999.99.
 * 
 * @author asheehan, hoyuela
 * 
 */
public class BankAmountTextWatcher implements TextWatcher {
	/**
	 * Holds the last string assigned to the watched EditText by this TextWatcher after formatting it.
	 */
	private String valueText;
	/**
	 * Holds Reference to the EditText which is being watched and whose text is formatted.
	 */
	private EditText watchee;
	/**
	 * Holds the numeric version of what is assigned to valueText
	 */
	private double value;
	/**
	 * Holds the maximum value that can be entered by the user in watchee.
	 */
	protected final static double MAX_VALUE = 99999.99;
	
	private final static double SHIFT_ONE = 10.0;
	private final static double SHIFT_TWO = 100.0;
	private final static double SHIFT_THREE = 1000.0;

	public BankAmountTextWatcher( final String startValue ) {
		if( !Strings.isNullOrEmpty(startValue) ) {
			valueText = startValue;
			value = Double.parseDouble(startValue.replace(",", ""));
		}
	}
	
	public void setWatchee(final EditText watchee) {
		this.watchee = watchee;
		valueText = this.watchee.getText().toString();
	}
	
	@Override
	public void beforeTextChanged(final CharSequence s, final int start,
			final int count, final int after) {
	}

	@Override
	public void afterTextChanged(final Editable s) {

		// The text of this edittext needs to be updated
		updateValueText();

	}

	@Override
	public void onTextChanged(final CharSequence s, final int start,
			final int before, final int count) {

		if (count == 0) {
			// If count is 0 then a span is being replace with "", aka a
			// character is being deleted
			chopOffLastDigit();
		} else if (count != valueText.length() || value == 0) {

			// If this character hasn't been handled, and the length of the text
			// being set is different from the length of the current text
			final String text = s.toString();

			if (text != null && text.length() > start) {

				final char c = text.charAt(start);

				if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4'
						|| c == '5' || c == '6' || c == '7' || c == '8'
						|| c == '9') {
					updateValueWithCharacter(c);
				}

			}

		}

	}

	/**
	 * Converts the given char to an integer and appends it to the value. Does
	 * not add the given char if it would increase the value above the max value
	 * 
	 * @param c
	 *            The character to be appended to the value
	 */
	private void updateValueWithCharacter(final char c) {

		final int val = Integer.parseInt(Character.toString(c));

		updateValueWithDigit(val);

		if (value > MAX_VALUE) {
			chopOffLastDigit();
		}

	}

	/**
	 * 
	 * Appends the given digit to the right of the amount, shifting all other
	 * 
	 * digits to the left
	 * 
	 * @param digit
	 *            The digit to be appended
	 */

	private void updateValueWithDigit(final int digit) {

		// 100 to get both decimal places to the left of the decimal place, 10
		// times more to put a 0
		// immediately left of the decimal
		double upped = value * SHIFT_THREE;

		// Add the int to the ones place
		upped += digit;

		// Divide by 100 to put the two digits left of the decimal back to the
		// right
		upped /= SHIFT_TWO;

		value = upped;
	}

	/**
	 * 
	 * Acts like delete, removes the right most digit in the amount
	 */

	private void chopOffLastDigit() {

		// Get the first digit right of the decimal place to the left of it
		double raised = value * SHIFT_ONE;

		// Floor this value to chop off all digits right of the decimal
		raised = Math.floor(raised);

		// Put the digits back to the right of the decimal place
		value = raised / SHIFT_TWO;

	}

	/**
	 * Uses the current value to set the value text
	 */
	private void updateValueText() {

		if (value > 0) {

			valueText = BankStringFormatter.convertStringFloatToDollars(Double.toString(value));
			valueText = valueText.replace("$", "");
			watchee.removeTextChangedListener(this);
			watchee.setText(valueText);
			watchee.addTextChangedListener(this);
			watchee.setSelection(valueText.length());
		} else {
			watchee.getText().clear();
		}

	}

}