package com.discover.mobile.common.bank.account;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used for storing Account information provided in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * @author henryoyuela
 *
 */
public class AccountList implements Serializable{

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize AccountList objects
	 */
	private static final long serialVersionUID = 4356333125128085209L;
	
	/**List of accounts that belong to a customer*/
	public List<Account> accounts;
}