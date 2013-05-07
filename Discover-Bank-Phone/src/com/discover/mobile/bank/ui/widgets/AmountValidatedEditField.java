package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

import com.discover.mobile.bank.util.BankAmountTextWatcher;
import com.discover.mobile.common.ui.widgets.ValidatedInputField;

/**
* A sub-class of ValidatedInputField, an EditText based view that allows to validate
* the text entered by a user. The method isValid() is used to determine whether
* the text entered is valid or not. If the text entered is invalid then the textfield's
* border is shown in read and the attach error label is shown (visibility changed from
* gone to Visible). Otherwise, border is return to normal and error label is hidden.
*
* An error label must be attached via attachErrorLabel() in order to show or hide
* an error message when the text is invalid.
*
* @author henryoyuela
*
*/
public class AmountValidatedEditField extends ValidatedInputField {
	/**
	 * TAG used to print logs into log cat
	 */
	protected static final String TAG = "AmountValidate";
	/**
	 * Holds the value of the configurable maximum value allowed to be ented in the watched text field.
	 */
	private double maxValue = BankAmountTextWatcher.MAX_VALUE;
	
	/**
	 * Reference to TextWatcher which formats the text in amountField.
	 */
	private BankAmountTextWatcher textWatcher = null;
	
	

	public AmountValidatedEditField(final Context context) {
		super(context);
	}

	public AmountValidatedEditField(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public AmountValidatedEditField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onFocusChanged(final boolean focused, final int direction, final Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);

		final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		if( focused ) {
			imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
		} else {
			imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
		}
	}

	/**
	 * Method used to enable or disable the text watcher which formats the text into a decimal value.
	 *
	 * @param value Set to true to enable text watcher, false to disable.
	 */
	public void enableBankAmountTextWatcher(final boolean value) {
		if( value ) {
			if( null == textWatcher ) {
				/**Associate a text watcher that handles formatting of Amount Field into currency format*/
				textWatcher = new BankAmountTextWatcher(this.getText().toString());
				textWatcher.setMaxValue(maxValue);
				textWatcher.setWatchee(this);
				this.addTextChangedListener(textWatcher);
			}
		} else {
			if( null != textWatcher ) {
				removeTextChangedListener(textWatcher);
				textWatcher = null;
			}
		}
	}

	@Override
	protected void clearInputFieldState() {
		/**Temporarily disable text watcher to allow clearing of text field*/
		enableBankAmountTextWatcher(false);

		super.clearInputFieldState();

		/**Renable the text watcher to allow formatting again*/
		enableBankAmountTextWatcher(true);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	/**
	 * Method used to show and hide the keyboard.
	 *
	 * @param show True opens keyboard and false closes it.
	 */
	public void showKeyboard(final boolean show) {
		final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		if( show ) {
			imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
		} else {
			imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
		}
	}
	
	/**
	 * Method used to set the maximum valued allowed to be entered into the watched text view.
	 * 
	 * @param maxValue Maximum value allowed, must be greater than 0.
	 * 
	 */
	public void setMaximumValue(final double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return Returns the maximum value allowed to be entered into the watched text view.
	 */
	public double getMaximumValue() {
		return maxValue;
	}
}
