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
	private final OnClickListener listener;
	
	public BankSelectAccountItem(final Context context, final Account account, final OnClickListener listener) {
		super(context, account);
		
		this.listener = listener;
	}

	
	/**
	 * 
	 * @param value Reference to a string that holds an Account's ending.
	 */
	@Override
	public void setEnding(final AccountNumber value) {
		try {
			getAccountEnding().setText("(..." + getAccount().accountNumber.ending +")");
		} catch(final Exception ex) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				getAccountEnding().setText("");
			}
		}
	}
	
	/**
	 * Sends a service call to download the posted transaction details for the account associated with this view.
	 */
	@Override
	public void onClick(final View v) {
		listener.onClick(this);
	}

}
