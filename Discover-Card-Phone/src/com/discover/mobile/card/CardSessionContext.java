package com.discover.mobile.card;

import java.util.List;

import com.discover.mobile.card.services.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.push.history.NotificationDetail;
import com.discover.mobile.card.services.push.manage.PushNotificationPrefsDetail;

@Deprecated
public final class CardSessionContext {
	
	private static CardSessionContext currentSessionDetails;
	
	private AccountDetails accountDetails;
	
	private boolean isNotCurrentUserRegisteredForPush = false;
	
	/**Push prefs retrieved from the server*/
	private PushNotificationPrefsDetail prefs; 
	
	/**List of notifications (used to help with rotation)*/
	private List<NotificationDetail> notifications;
	
	
	/**Time periods that can be displayed for the recent account activity*/
	private RecentActivityPeriodsDetail periods;
	
	/**Boolean used to determine if the user is registering or changing their info*/
	private boolean forgotCreds = false;
	
	private CardSessionContext(){
		
	}
	
	public static CardSessionContext getCurrentSessionDetails(){
		if(null == currentSessionDetails){
			currentSessionDetails = new CardSessionContext();
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

	/**
	 * @return the forgotCreds
	 */
	public boolean isForgotCreds() {
		return forgotCreds;
	}

	/**
	 * @param forgotCreds the forgotCreds to set
	 */
	public void setForgotCreds(boolean forgotCreds) {
		this.forgotCreds = forgotCreds;
	}
}
