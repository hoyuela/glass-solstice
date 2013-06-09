package com.discover.mobile.bank.framework;

import java.util.List;

import com.discover.mobile.bank.services.account.Account;

/**
 * Interface that defines the method signatures to implemented for receiving event change notifications from a BankUser
 * object.
 * 
 * @author henryoyuela
 * 
 */
public interface BankUserListener {
	/**
	 * Method called by BankUser when the list of accounts change.
	 * 
	 * @param sender
	 *            Reference to the instance of the BankUser that invoked the callback.
	 * @param accounts
	 *            Reference to the BankUser's updated list of accounts.
	 */
	public void onAccountsUpdate(BankUser sender, List<Account> accounts);

	/**
	 * Method called by BankUser when the current account has changed.
	 * 
	 * @param sender
	 *            Reference to the instance of the BankUser that invoked the callback.
	 * @param account
	 *            Reference to the new current account object.
	 */
	public void onCurrentAccountUpdate(BankUser sender, Account account);
}
