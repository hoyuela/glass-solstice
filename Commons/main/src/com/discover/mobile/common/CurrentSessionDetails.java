package com.discover.mobile.common;

import com.discover.mobile.common.auth.AccountDetails;
import com.google.inject.Singleton;

@Singleton
public class CurrentSessionDetails {
	
	private AccountDetails accountDetails;
	
	public AccountDetails getAccountDetails() {
		return accountDetails;
	}
	
	public void setAccountDetails(AccountDetails accountDetails) {
		this.accountDetails = accountDetails;
	}
	
}
