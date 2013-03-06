package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.discover.mobile.bank.account.BankAccountView;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountNumber;

/**
 * This class is used to display the account information within the Check Deposit - Select Account page
 * defined in BankDepositSelectAccount fragment.
 * 
 * @author henryoyuela
 *
 */
public class BankSelectAccountItem extends BankAccountView {
	public BankSelectAccountItem(final Context context, final Account account) {
		super(context, account);
	}
	/**
	 * Method used to retrieve the Account associated with this view.
	 * 
	 * @return Returns to account object passed in via the constructor at instantiation
	 */
	public Account getAccount() {
		return account;
	}
	
	/**
	 * 
	 * @param value Reference to a string that holds an Account's ending.
	 */
	@Override
	public void setEnding(final AccountNumber value) {
		try {
			acctEnding.setText("(..." +account.accountNumber.ending +")");
		} catch(final Exception ex) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				acctEnding.setText("");
			}
		}
	}
	
	/**
	 * Sends a service call to download the posted transaction details for the account associated with this view.
	 */
	@Override
	public void onClick(final View v) {
		//NOTHING TO DO HERE
	}

}
