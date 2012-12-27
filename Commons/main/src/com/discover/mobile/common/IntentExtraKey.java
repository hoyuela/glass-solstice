package com.discover.mobile.common;

/**
 * Constants for Intent Extras 
 * 
 * @author ekaram
 *
 */
public final class IntentExtraKey {
	
	public static final String EMAIL = "Email";
	public static final String UID = "User-ID";
	public static final String ACCOUNT_LAST4 = "Account-Last4";
	public static final String REGISTRATION1_DETAILS = "Registration1-Details";
	public static final String FORGOT_PASS_DETAILS = "Forgot-Pass-Details";
	public static final String HELP_TYPE = "Uid-Pass-Help-Type";
	public static final String STRONG_AUTH_QUESTION = "SA-Question";
	public static final String STRONG_AUTH_QUESTION_ID = "SA-Question-ID";
	public static final String ACCOUNT_LOCKED_FAILED_ATTEMPTS = "Account-Locked-Failed-Attempts";
	public static final String SHOW_SUCESSFUL_LOGOUT_MESSAGE = "Show-Successful-Logout";
	
	/**
	 * ERROR TEXT is used to add resource error text to an activity to display on error Page
	 */
	public static final String ERROR_TEXT_KEY = "error-text";
	
	/**
	 * ERROR TEXT TITLE is used to add a resource error text to an activity to display on error page
	 */
	public static final String ERROR_TITLE_TEXT_KEY = "error-title-text";
	
	/**
	 * NOT INSTANTIABLE
	 */
	private IntentExtraKey() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}
