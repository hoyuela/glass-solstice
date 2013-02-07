package com.discover.mobile.common;

import com.discover.mobile.common.bank.account.AccountList;
import com.discover.mobile.common.bank.customer.Customer;

/**
 * Class used to maintain session information for a user logged into a Bank account. This
 * class follows a singleton design pattern allowing only one instance of this class to ever exists. 
 * It's data members are set by NetworkServiceCall<> objects upon receiving a successful response. As
 * an example, the customerInfo object is updated by the CustomerServiceCall class.
 * @author henryoyuela
 *
 */
public final class BankUser {
	/**
	 * Singleton instance of this class
	 */
	private final static BankUser currentBankUser = new BankUser();
	/**
	 * Holds a reference to a AccountList set from the GetCustomerAccountServerCall 
	 * on a successful download of accounts.
	 */
	private AccountList accountList;
	/**
	 * Holds a reference to a Customer object set from the CustomerServiceCall on a
	 * successful download of customer information.
	 */
	private Customer customerInfo;

	/**
	 * Default constructor made private to allow a single instance
	 */
	private BankUser() {
		
	}
	/**
	 * 
	 * @return Returns reference to single instance of BankUser
	 */
	public static BankUser instance() {
		return currentBankUser;
	}
	/**
	 * 
	 * @return Returns list of accounts downloaded via GetCustomerAccountsServerCall
	 */
	public AccountList getAccounts() {
		return accountList;
	}
	/**
	 * 
	 * @param accounts Sets the list of accounts downloaded via GetCustomerAccountsServerCall
	 */
	public void setAccounts(final AccountList accounts) {
		this.accountList = accounts;
	}
	/**
	 * 
	 * @return Returns True if user has Bank accounts, false otherwise.
	 */
	public boolean hasAccounts() {
		return (accountList != null && !accountList.accounts.isEmpty());
	}
	/**
	 * 
	 * @return Returns reference to a Customer object that is set after a successful 
	 * response to Bank Customer Service API via CustomerServiceCall.
	 */
	public Customer getCustomerInfo() {
		return customerInfo;
	}
	/**
	 * 
	 * @param customerInfo Sets reference to Customer object downloaded via CustomerServiceCall
	 */
	public void setCustomerInfo(final Customer customerInfo) {
		this.customerInfo = customerInfo;
	}

}
