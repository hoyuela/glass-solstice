package com.discover.mobile.common;

/**
 * Constants for Intent Extras 
 * 
 * @author ekaram
 *
 */
public final class IntentExtraKey {
	
	/**Intent extra for email used to send the users email to the confirmation modal*/
	public static final String EMAIL = "Email";
	
	/**Intent extra for user ID used to send the users user id to the confirmation modal*/
	public static final String UID = "User-ID";

	/**Intent extra for account last 4 digits used to send the users last four to the confirmation modal*/
	public static final String ACCOUNT_LAST4 = "Account-Last4";
	
	/**Intent extra used to let the confirmation modal to display the correct text associated with registration*/
	public static final String SCREEN_REGISTRATION = "Registration-Screen";
	
	/**Intent extra used to let the confirmation modal to display the correct text associated with forgot password*/
	public static final String SCREEN_FORGOT_PASS = "Forgot-Pass-Screen-Type";
	
	/**Intent extra used to let the confirmation modal to display the correct text associated with forgot userID*/
	public static final String SCREEN_FOROGT_USER = "Forgot-User-Screen-Type";
	
	/**Intent extra used to let the confirmation modal to display the correct text associated with forgot both*/
	public static final String SCREEN_FORGOT_BOTH = "Forgot-Both-Screen-Type";
	
	/**Intent extra used to get the screen type out an intent*/
	public static final String SCREEN_TYPE = "Screen-Type";
	
	public static final String REGISTRATION1_DETAILS = "Registration1-Details";
	public static final String FORGOT_PASS_DETAILS = "Forgot-Pass-Details";
	public static final String HELP_TYPE = "Uid-Pass-Help-Type";
	public static final String STRONG_AUTH_QUESTION = "SA-Question";
	public static final String STRONG_AUTH_QUESTION_ID = "SA-Question-ID";
	public static final String ACCOUNT_LOCKED_FAILED_ATTEMPTS = "Account-Locked-Failed-Attempts";
	public static final String SHOW_SUCESSFUL_LOGOUT_MESSAGE = "Show-Successful-Logout";
	/**
	 * Used to specify to the application that the token for the session is no longer valid
	 */
	public static final String SESSION_EXPIRED = "Session-Expired";
	
	/**
	 * ERROR TEXT is used to add resource error text to an activity to display on error Page
	 */
	public static final String ERROR_TEXT_KEY = "error-text";
	
	/**
	 * ERROR TEXT TITLE is used to add a resource error text to an activity to display on error page
	 */
	public static final String ERROR_TITLE_TEXT_KEY = "error-title-text";
	
	public static final String IS_CARD_ACCOUNT = "Is-Card";
	
	
	/**
	 * NOT INSTANTIABLE
	 */
	private IntentExtraKey() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}
