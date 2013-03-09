package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.discover.mobile.bank.services.deposit.AccountLimits;
import com.discover.mobile.common.ui.widgets.ValidatedInputField;

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
public class AmountValidatedEditField extends ValidatedInputField {
	private AccountLimits limits = null;
	
	public AmountValidatedEditField(final Context context) {
		super(context);	
	}

	public AmountValidatedEditField(final Context context, final AttributeSet attrs) {
		super(context, attrs);	
	}

	public AmountValidatedEditField(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Set the visibility of the attached error label to GONE.
	 */
	@Override
	protected void hideErrorLabel() {
		if(errorLabel != null)
			this.errorLabel.setVisibility(View.INVISIBLE);
	}
	
	public void setAccountLimits(final AccountLimits limits) {
		this.limits = limits;
	}
	
	@Override
	public boolean isValid() {
		boolean ret = false;
		
		if( limits != null ) {
			if( limits.dailyDepositAmount.hasReachedLimit() ) {
				this.errorLabel.setText(limits.dailyDepositAmount.error.message);
			} else if( limits.dailyDepositCount.hasReachedLimit() ) {
				this.errorLabel.setText(limits.dailyDepositCount.error.message);
			} else if( limits.depositAmount.hasReachedLimit() ) {
				this.errorLabel.setText(limits.depositAmount.error.message);
			} else if( limits.monthlyDepositAmount.hasReachedLimit() ) {
				this.errorLabel.setText(limits.monthlyDepositAmount.error.message);
			} else if( limits.monthlyDepositCount.hasReachedLimit() ) {
				this.errorLabel.setText(limits.monthlyDepositCount.error.message);
			} else {
				ret = true;
			}		
		} else {
			//limits not set
		}
		return ret;
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

}
