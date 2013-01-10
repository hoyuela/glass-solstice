package com.discover.mobile.common.url;
/**
 * This class is used for adding and getting URL's for card services. Any bank URL's should go into the bank Url Manager class
 * @author ajleeds
 *
 */
public class UrlManagerCard {
	
	
	/**
	 * Pre Auth URL
	 */
	private static final String PRE_AUTH_URL = "/cardsvcs/acs/session/preauthcheck";
	
	/**
	 * Forgot User ID/Password URLs
	 */
	private static final String FORGOT_PASSWORD_URL = "/cardsvcs/acs/reg/v1/user/pwd/auth";
	private static final String FORGOT_PASSWORD_TWO_URL	 = "/cardsvcs/acs/reg/v1/user/pwd";
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
	
	/**
	 * Strong Authentication URLs
	 */
	private static final String STRONG_AUTH_ANSWER_URL = "/cardsvcs/acs/strongauth/v1/authenticate";
	private static final String STRONG_AUTH_CHECK_URL = "/cardsvcs/acs/reg/v1/user/sa/check";
	private static final String STRONG_AUTH_QUESTION_URL = "/cardsvcs/acs/strongauth/v1/challenge";
	private static final String STRONG_AUTH_URL = "/cardsvcs/acs/reg/v1/user/sa/check";
	
	/**
	 * Push Notification URLs
	 */
	private static final String PUSH_ALERT_HISTORY_URL = "/cardsvcs/acs/msghist/v1/notification/history?start=";// TODO move into a method +begin+"&size="+amount
	private static final String PUSH_READ_NOTIFICATION_URL = "/cardsvcs/acs/msghist/v1/notification";
	private static final String PUSH_GET_NOTIFICATION_PREF_URL = "/cardsvcs/acs/contact/v1/preferences/enrollments?vid="; //TODO move into getter method  + XtifySDK.getXidKey(context) "
	private static final String PUSH_SET_NOTIFICATION_PREF_URL = "/cardsvcs/acs/contact/v1/preferences/enrollments";
	private static final String PUSH_REGISTRATION_STATUS_URL = "/cardsvcs/acs/contact/v1/registration/status?vid="; //TODO move into getter mothod  + XtifySDK.getXidKey(context)"
	private static final String PUSH_REGISTER_VENDOR_URL = "/cardsvcs/acs/contact/v1/registration/status";
	
	

	/**
	 * @return the preAuthUrl
	 */
	protected static String getPreAuthUrl() {
		return PRE_AUTH_URL;
	}



	/**
	 * @return the forgotPasswordUrl
	 */
	protected static String getForgotPasswordUrl() {
		return FORGOT_PASSWORD_URL;
	}



	/**
	 * @return the forgotPasswordTwoUrl
	 */
	protected static String getForgotPasswordTwoUrl() {
		return FORGOT_PASSWORD_TWO_URL;
	}



	/**
	 * @return the forgotUserIdUrl
	 */
	protected static String getForgotUserIdUrl() {
		return FORGOT_USER_ID_URL;
	}



	/**
	 * @return the loginUrl
	 */
	protected static String getLoginUrl() {
		return LOGIN_URL;
	}



	/**
	 * @return the logoutUrl
	 */
	protected static String getLogoutUrl() {
		return LOGOUT_URL;
	}



	/**
	 * @return the sessionUrl
	 */
	protected static String getSessionUrl() {
		return SESSION_URL;
	}



	/**
	 * @return the strongAuthAnswerUrl
	 */
	protected static String getStrongAuthAnswerUrl() {
		return STRONG_AUTH_ANSWER_URL;
	}



	/**
	 * @return the strongAuthCheckUrl
	 */
	protected static String getStrongAuthCheckUrl() {
		return STRONG_AUTH_CHECK_URL;
	}



	/**
	 * @return the strongAuthQuestionUrl
	 */
	protected static String getStrongAuthQuestionUrl() {
		return STRONG_AUTH_QUESTION_URL;
	}



	/**
	 * @return the strongAuthUrl
	 */
	protected static String getStrongAuthUrl() {
		return STRONG_AUTH_URL;
	}



	/**
	 * @return the pushAlertHistoryUrl
	 */
	protected static String getPushAlertHistoryUrl(int start, int size) {
		return PUSH_ALERT_HISTORY_URL + start + "&size=" + size;
	}



	/**
	 * @return the pushReadNotificationUrl
	 */
	protected static String getPushReadNotificationUrl() {
		return PUSH_READ_NOTIFICATION_URL;
	}



	/**
	 * @return the pushGetNotificationPrefUrl
	 */
	protected static String getPushGetNotificationPrefUrl(String device) {
		return PUSH_GET_NOTIFICATION_PREF_URL + device;
	}



	/**
	 * @return the pushSetNotificationPrefUrl
	 */
	protected static String getPushSetNotificationPrefUrl() {
		return PUSH_SET_NOTIFICATION_PREF_URL;
	}



	/**
	 * @return the pushRegistrationStatusUrl
	 */
	protected static String getPushRegistrationStatusUrl(String device) {
		return PUSH_REGISTRATION_STATUS_URL + device;
	}



	/**
	 * @return the pushRegisterVendorUrl
	 */
	protected static String getPushRegisterVendorUrl() {
		return PUSH_REGISTER_VENDOR_URL;
	}



	/**
	 * @return the accountInfoUrl
	 */
	protected static String getAccountInfoUrl() {
		return ACCOUNT_INFO_URL;
	}



	/**
	 * @return the authenticateCall
	 */
	protected static String getAuthenticateCall() {
		return AUTHENTICATE_CALL;
	}
	
}
