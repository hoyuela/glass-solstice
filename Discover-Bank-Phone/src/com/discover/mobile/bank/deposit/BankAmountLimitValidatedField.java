package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.deposit.AccountLimits;
import com.discover.mobile.bank.services.json.Limit;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.google.common.base.Strings;

/**
* A sub-class of AmountValidatedEditField, an EditText based view that allows to validate
* the text entered by a user. The method isValid() is used to determine whether
* the text entered is valid or not. If the text entered is invalid then the textfield's
* border is shown in read and the attach error label is shown (visibility changed from
* gone to Visible). Otherwise, border is return to normal and error label is hidden.
* 
* A class that is owner of an instance of this class can use the isValid method to 
* determine whether text entered meets all limits requirements. Note that a maximum 
* of 8 numeric only characters can be entered including the decimal point. All limits
* are read from an AccountLimits object that is associated with an instance of this class
* via the method setAccountLimits(). 
* 
* An error label must be attached via attachErrorLabel() in order to show or hide
* an error message when the text is invalid.
* 
* @author henryoyuela
*
*/
public class BankAmountLimitValidatedField extends AmountValidatedEditField {
	/**
	 * Reference to a list of limits that are provided by the server for a specific account.
	 */
	private AccountLimits limits = null;
	
	/** Visibility to set the error label to when hidden. */
	private int hiddenErrorVisibility = View.GONE;
	
	public BankAmountLimitValidatedField(final Context context) {
		super(context);	
	}

	public BankAmountLimitValidatedField(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
	}

	public BankAmountLimitValidatedField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Method used to provide limits which are evaluated in the isValid() method to ensure
	 * that the numeric value entered by the user meets the limit requirements as specified
	 * by the server.
	 * 
	 * @param limits Reference to a list of limits downloaded from the server.
	 */
	public void setAccountLimits(final AccountLimits limits) {
		this.limits = limits;
	}
	
	/**
	 * Returns an error string with the limit amount specified within the error string.
	 * 
	 * @param limit Reference to a limit object that contains both the limit value in cents and the error string.
	 * 
	 * @return Error String to display
	 */
	private String getErrorTextWithDollarAmount(final Limit limit) {
		final String strLimit = BankStringFormatter.convertCentsToDollars(limit.limit);
		
		return limit.error.message.replace("{0}", strLimit.replace("$", ""));
		
	}
	
	/**
	 * Returns an error string wht the limit count specified within the error string
	 * @param limit Reference to a limit object that contains both the limit count and the error string.
	 * @return Error String to display
	 */
	private String getErrorTextWithLimitCount(final Limit limit) {
		return limit.error.message.replace("{0}", Integer.toString(limit.limit));
	}
	/**
	 * Compares the value entered by the user with the limits provided via setAccountLimits() to ensure
	 * that the user has not exceeded the limits specified.
	 */
	@Override
	public boolean isValid() {
		boolean ret = false;
		
		final String amountText = this.getText().toString().replace(",","");
		
		/**Verify the user has entered an non-empty value*/
		if( Strings.isNullOrEmpty(amountText) ) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Invalid amount entered by user");
			}
			if(errorLabel != null) {
				errorLabel.setText(R.string.bank_deposit_invalid_amount);
			}
			return false;
		}
		
		double amount = 0.00;
			
		/**Catch exception if it fails to format text entered by user into a double*/
		try {
			amount = Double.parseDouble(amountText);
		} catch (final Exception ex) {
			Log.e(TAG, "Unable to convert text to currency");
		}	
			
		/**Verify a AccountLimits object has been associated with this object (& it has all its values)*/
		if (limits != null && errorLabel != null && !limits.isMissingValues()) {
			/**Verify Total amount allowed to be deposited in this account per month has not been exceeded*/
			if (!limits.monthlyDepositAmount.isValidAmount(amount)) {
				errorLabel.setText(getErrorTextWithDollarAmount(limits.monthlyDepositAmount));
			}
			/** Verify Number of deposits allowed on this account per month has not been exceeded*/
			else if (limits.monthlyDepositCount.remaining <= 0) {
				errorLabel.setText(getErrorTextWithLimitCount(limits.monthlyDepositCount));
			}
			/**Verify Total amount allowed to be deposited in this account per day has not been exceeded*/
			else if (!limits.dailyDepositAmount.isValidAmount(amount)) {
				errorLabel.setText(getErrorTextWithDollarAmount(limits.dailyDepositAmount));
			}
			/**Verify Maximum amount allowed to be deposited in this account per transaction has not been exceeded.*/
			else if (!limits.depositAmount.isValidAmount(amount)) {
				errorLabel.setText(getErrorTextWithDollarAmount(limits.depositAmount));
			}
			/**Verify Number of deposits allowed on this account per day has not been exceeded*/
			else if (limits.dailyDepositCount.remaining <= 0) {
				errorLabel.setText(getErrorTextWithLimitCount(limits.dailyDepositCount));
			} else {
				ret = true; // Everything is valid
			}	
		} else {
			if (Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to verify amount entered by user");
			}
			if (errorLabel != null){
				errorLabel.setText(R.string.bank_deposit_unknown_limit);
			}
		}

		return ret;
	}
	
	/**
	 * Allows for a hidden error label to be GONE or INVISIBLE.
	 * @param isGone - {@code true} when hidden error labels should be of Visibility type GONE.
	 */
	public void setHiddenErrorVisibility(final boolean isGone) {
		hiddenErrorVisibility = isGone ? View.GONE : View.INVISIBLE;
	}
	
	/**
	 * Set the visibility of the attached error label to INVISIBLE or GONE.
	 * The default visibility is GONE and can be set via method setHiddenErrorVisibility(boolean)
	 */
	@Override
	protected void hideErrorLabel() {
		if(errorLabel != null){
			this.errorLabel.setVisibility(hiddenErrorVisibility);
		}
	}
}
