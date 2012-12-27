package com.discover.mobile.common;

import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;

public final class CurrentSessionDetails {
	
	private static CurrentSessionDetails currentSessionDetails;
	
	private AccountDetails accountDetails;
	
	private boolean isNotCurrentUserRegisteredForPush = false;
	
	/**Push prefs retrieved from the server*/
	private PushNotificationPrefsDetail prefs; 
	
	private CurrentSessionDetails(){
		
	}
	
	public static CurrentSessionDetails getCurrentSessionDetails(){
		if(null == currentSessionDetails){
			currentSessionDetails = new CurrentSessionDetails();
		}
		
		return currentSessionDetails;
	}
	
	public AccountDetails getAccountDetails() {
		return accountDetails;
	}
	
	public void setAccountDetails(final AccountDetails accountDetails) {
		this.accountDetails = accountDetails;
	}

	public boolean isNotCurrentUserRegisteredForPush() {
		return isNotCurrentUserRegisteredForPush;
	}

	public void setNotCurrentUserRegisteredForPush(
			final boolean isNotCurrentUserRegisteredForPush) {
		this.isNotCurrentUserRegisteredForPush = isNotCurrentUserRegisteredForPush;
	}

	public PushNotificationPrefsDetail getPrefs() {
		return prefs;
	}

	public void setPrefs(PushNotificationPrefsDetail prefs) {
		this.prefs = prefs;
	}
	
}
