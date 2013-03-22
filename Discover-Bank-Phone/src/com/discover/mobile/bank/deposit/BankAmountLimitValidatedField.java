package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.deposit.AccountLimits;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.net.json.bank.Limit;
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
		
		return limit.error.message.replace("{0}", strLimit);
		
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
		
		final String amountText = this.getText().toString().replaceAll("[,.]+","");
		
		/**Verify the user has entered an non-empty value*/
		if( !Strings.isNullOrEmpty(amountText) ) {
			final double amount = Double.parseDouble(amountText);
			
			/**Verify a AccountLimits object has been associated with this object otherwise mark input as invalid*/
			if( limits != null ) {
				/**Verify Total amount allowed to be deposited in this account per month has not been exceeded*/
				if(limits.monthlyDepositAmount == null || !limits.monthlyDepositAmount.isValidAmount(amount) ) {
					this.errorLabel.setText(getErrorTextWithDollarAmount(limits.monthlyDepositAmount));
				} 
				/** Verify Number of deposits allowed on this account per month has not been exceeded*/
				else if(limits.monthlyDepositCount == null || limits.monthlyDepositCount.remaining <= 0 ) {
					this.errorLabel.setText(getErrorTextWithLimitCount(limits.monthlyDepositCount));
				}
				/**Verify Total amount allowed to be deposited in this account per day has not been exceeded*/
				else if( limits.dailyDepositAmount == null || !limits.dailyDepositAmount.isValidAmount(amount) ) {					
					this.errorLabel.setText(getErrorTextWithDollarAmount(limits.dailyDepositAmount));
				} 
				/**Verify Maximum amount allowed to be deposited in this account per transaction has not been exceeded.*/
				else if(limits.depositAmount == null || !limits.depositAmount.isValidAmount(amount) ) {
					this.errorLabel.setText(getErrorTextWithDollarAmount(limits.depositAmount));
				}
				/**Verify Number of deposits allowed on this account per day has not been exceeded*/
				else if(limits.dailyDepositCount == null || limits.dailyDepositCount.remaining <= 0 ) {
					this.errorLabel.setText(getErrorTextWithLimitCount(limits.dailyDepositCount));
				} else {
					ret = true;
				}		
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Unable to verify amount entered by user");
				}
				this.errorLabel.setText(R.string.bank_deposit_unknown_limit);
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Invalid amount entered by user");
			}
			this.errorLabel.setText(R.string.bank_deposit_invalid_amount);
		}
		return ret;
	}
}
