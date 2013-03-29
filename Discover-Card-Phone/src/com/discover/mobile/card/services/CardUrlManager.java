package com.discover.mobile.card.services;
/**
 * This class is used for adding and getting URL's for card services. Any bank
 * URL's should go into the bank Url Manager class
 * 
 * @author ajleeds
 * 
 */

public class CardUrlManager {

	private static final String BASE_URL = "https://mst0.m.discovercard.com";

	/**
	 * Pre Auth URL
	 */
	private static final String PRE_AUTH_URL = "/cardsvcs/acs/session/preauthcheck";

	/**
	 * Forgot User ID/Password URLs
	 */
	private static final String FORGOT_PASSWORD_URL = "/cardsvcs/acs/reg/v1/user/pwd/auth";
	private static final String FORGOT_PASSWORD_TWO_URL = "/cardsvcs/acs/reg/v1/user/pwd";
	private static final String FORGOT_USER_ID_URL = "/cardsvcs/acs/reg/v1/user/id";

	/**
	 * Login/Logout URLs
	 */
	private static final String LOGIN_URL = "/cardsvcs/acs/reg/v1/user/reg";
	private static final String LOGOUT_URL = "/cardsvcs/acs/session/v1/delete";

	/**
	 * Account URLs
	 */
	private static final String ACCOUNT_INFO_URL = "/cardsvcs/acs/reg/v1/user/reg/auth";
	private static final String AUTHENTICATE_CALL = "/cardsvcs/acs/acct/v1/account";
	private static final String SESSION_URL = "/cardsvcs/acs/session/v1/update";
	private static final String SSO_AUTHENTICATE_CALL = "/cardsvcs/acs/acct/v1/tokensignin";
	
	/**URL to get the late payment information*/
	private static final String LATE_PAYMENT_WARNING_URL = "/cardsvcs/acs/stmt/v1/paymentwarning";
	
	/**URL to get late payment text information*/
	private static final String LATE_PAYMENT_WARNING_TEXT_URL = "/json/statements/latePayWarn.json";

	/**
	 * Strong Authentication URLs
	 */
	private static final String STRONG_AUTH_ANSWER_URL = "/cardsvcs/acs/strongauth/v1/authenticate";
	private static final String STRONG_AUTH_CHECK_URL = "/cardsvcs/acs/reg/v1/user/sa/check";
	private static final String STRONG_AUTH_QUESTION_URL = "/cardsvcs/acs/strongauth/v1/challenge";
	private static final String STRONG_AUTH_URL = "/cardsvcs/acs/reg/v1/user/sa/check";
	
	/**
	 * URL to get the dates ranges to be shown when the users tries to select 
	 * from a range of dates to show recent transactions.
	 */
	private static final String STATEMENT_IDENTIFIERS = "/cardsvcs/acs/stmt/v1/identifiers";
	
	/**URL to get recent account activity transactions*/
	private static final String GET_RECENT_ACCOUNT_TRANSACTIONS = "/cardsvcs/acs/stmt/v1/transaction?stmtDate=";

	/**
	 * Push Notification URLs
	 */
	private static final String PUSH_ALERT_HISTORY_URL = "/cardsvcs/acs/msghist/v1/notification/history?start=";
	private static final String PUSH_READ_NOTIFICATION_URL = "/cardsvcs/acs/msghist/v1/notification";
	private static final String PUSH_GET_NOTIFICATION_PREF_URL = "/cardsvcs/acs/contact/v1/preferences/enrollments?vid=";
	private static final String PUSH_SET_NOTIFICATION_PREF_URL = "/cardsvcs/acs/contact/v1/preferences/enrollments";
	private static final String PUSH_REGISTRATION_STATUS_URL = "/cardsvcs/acs/contact/v1/registration/status?vid=";
	private static final String PUSH_REGISTER_VENDOR_URL = "/cardsvcs/acs/contact/v1/registration/status";
	
	/**
	 * Search Transaction URLs
	 */
	private static final String SEARCH_TRANS_CATEGORY_URL = "/cardsvcs/acs/stmt/v1/category";

	/**
	 * @return the baseUrl
	 */
	public static String getBaseUrl() {
		return BASE_URL;
	}

	/**
	 * @return the preAuthUrl
	 */
	public static String getPreAuthUrl() {
		return PRE_AUTH_URL;
	}

	/**
	 * @return the forgotPasswordUrl
	 */
	public static String getForgotPasswordUrl() {
		return FORGOT_PASSWORD_URL;
	}

	/**
	 * @return the forgotPasswordTwoUrl
	 */
	public static String getForgotPasswordTwoUrl() {
		return FORGOT_PASSWORD_TWO_URL;
	}

	/**
	 * @return the forgotUserIdUrl
	 */
	public static String getForgotUserIdUrl() {
		return FORGOT_USER_ID_URL;
	}

	/**
	 * @return the loginUrl
	 */
	public static String getLoginUrl() {
		return LOGIN_URL;
	}

	/**
	 * @return the logoutUrl
	 */
	public static String getLogoutUrl() {
		return LOGOUT_URL;
	}

	/**
	 * @return the sessionUrl
	 */
	public static String getSessionUrl() {
		return SESSION_URL;
	}

	/**
	 * @return the strongAuthAnswerUrl
	 */
	public static String getStrongAuthAnswerUrl() {
		return STRONG_AUTH_ANSWER_URL;
	}

	/**
	 * @return the strongAuthCheckUrl
	 */
	public static String getStrongAuthCheckUrl() {
		return STRONG_AUTH_CHECK_URL;
	}

	/**
	 * @return the strongAuthQuestionUrl
	 */
	public static String getStrongAuthQuestionUrl() {
		return STRONG_AUTH_QUESTION_URL;
	}

	/**
	 * @return the strongAuthUrl
	 */
	public static String getStrongAuthUrl() {
		return STRONG_AUTH_URL;
	}

	/**
	 * @return the pushAlertHistoryUrl
	 */
	public static String getPushAlertHistoryUrl(final int start, final int size) {
		return PUSH_ALERT_HISTORY_URL + start + "&size=" + size;
	}

	/**
	 * @return the pushReadNotificationUrl
	 */
	public static String getPushReadNotificationUrl() {
		return PUSH_READ_NOTIFICATION_URL;
	}

	/**
	 * @return the pushGetNotificationPrefUrl
	 */
	public static String getPushGetNotificationPrefUrl(final String device) {
		return PUSH_GET_NOTIFICATION_PREF_URL + device;
	}

	/**
	 * @return the pushSetNotificationPrefUrl
	 */
	public static String getPushSetNotificationPrefUrl() {
		return PUSH_SET_NOTIFICATION_PREF_URL;
	}

	/**
	 * @return the pushRegistrationStatusUrl
	 */
	public static String getPushRegistrationStatusUrl(final String device) {
		return PUSH_REGISTRATION_STATUS_URL + device;
	}

	/**
	 * @return the pushRegisterVendorUrl
	 */
	public static String getPushRegisterVendorUrl() {
		return PUSH_REGISTER_VENDOR_URL;
	}

	/**
	 * @return the accountInfoUrl
	 */
	public static String getAccountInfoUrl() {
		return ACCOUNT_INFO_URL;
	}

	/**
	 * @return the authenticateCall
	 */
	public static String getAuthenticateCall() {
		return AUTHENTICATE_CALL;
	}

	public static String getStatementIdentifiers() {
		return STATEMENT_IDENTIFIERS;
	}
	/**
	 * @return the late payment warning url
	 */
	public static String getLatePaymentWarningUrl() {
		return LATE_PAYMENT_WARNING_URL;
	}

	/**
	 * @return URL to get late payment text information
	 */
	public static String getLatePaymentWarningTextUrl() {
		return LATE_PAYMENT_WARNING_TEXT_URL;
	}

	/**
	 * @return URL to get recent account activity transactions
	 */
	public static String getGetRecentAccountTransactions(final String category) {
		return GET_RECENT_ACCOUNT_TRANSACTIONS + category;
	}
	
	/**
	 * @return URL to get categories for searching transactions
	 */
	public static String getSearchTransCategoryUrl() {
		return SEARCH_TRANS_CATEGORY_URL;
	}
	
	/**
	 * @return the ssoAuthenticateCall
	 */
	public static String getSSOAuthenticateCall() {
		return SSO_AUTHENTICATE_CALL;
	}
}
