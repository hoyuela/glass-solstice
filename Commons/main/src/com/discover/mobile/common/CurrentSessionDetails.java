package com.discover.mobile.common;

import java.util.List;

import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.push.history.NotificationDetail;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;

public final class CurrentSessionDetails {
	
	private static CurrentSessionDetails currentSessionDetails;
	
	private AccountDetails accountDetails;
	
	private boolean isNotCurrentUserRegisteredForPush = false;
	
	/**Push prefs retrieved from the server*/
	private PushNotificationPrefsDetail prefs; 
	
	/**List of notifications (used to help with rotation)*/
	private List<NotificationDetail> notifications;
	
	/**Time periods that can be displayed for the recent account activity*/
	private RecentActivityPeriodsDetail periods;
	
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

	public void setPrefs(final PushNotificationPrefsDetail prefs) {
		this.prefs = prefs;
	}

	public List<NotificationDetail> getNotifications() {
		return notifications;
	}

	public void setNotifications(final List<NotificationDetail> notifications) {
		this.notifications = notifications;
	}
	
	public void clearNotifications(){
		this.notifications.clear();
	}

	/**
	 * Get the time periods that can be displayed for the recent account activity
	 * @return the time periods that can be displayed for the recent account activity
	 */
	public RecentActivityPeriodsDetail getPeriods() {
		return periods;
	}

	/**
	 * Set the time periods that can be displayed for the recent account activity
	 * @param periods - the time periods that can be displayed for the recent account activity
	 */
	public void setPeriods(RecentActivityPeriodsDetail periods) {
		this.periods = periods;
	}
}
