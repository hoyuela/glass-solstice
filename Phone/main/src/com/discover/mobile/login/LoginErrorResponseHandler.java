package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_NUMBER_CHANGED;
import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_NUMBER_REREGISTERED;
import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_SETUP_PENDING;
import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;
import android.content.Context;

import com.discover.mobile.R;
import com.discover.mobile.RoboSlidingFragmentActivity;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * Error response handler for the login service call.
 * 
 * Handles errors provided by the server to either show appropriate error messages on the login screen, or
 * send the user to a different error screen.
 * 
 * @author scottseward, ekaram
 *
 */
public class LoginErrorResponseHandler extends BaseErrorResponseHandler {
	
	/**
	 * LoginErrorResponseHandler requires the context of use, input fields for id, and password, and error label
	 * to be updated in the event of certain errors.
	 * 
	 * @param context - the context of use, expected to be the login screen
	 */
	public LoginErrorResponseHandler(final Context context) {
		super((RoboSlidingFragmentActivity) context);
	}
	
	/**
	 * When the server responds with a specific error message, handle this error.
	 * 
	 * @param messageErrorResponse - the error returned from the server
	 * @return returns true if the given error was handled by this method, false otherwise.
	 */
	/* (non-Javadoc)
	 * @see com.discover.mobile.login.BaseErrorResponseHandler#handleJsonErrorCode(com.discover.mobile.common.net.json.JsonMessageErrorResponse)
	 */
	@Override
	protected boolean handleJsonErrorCode(JsonMessageErrorResponse messageErrorResponse) {
		clearInputs();
		
		switch(messageErrorResponse.getMessageStatusCode()) {
			
			case LAST_ATTEMPT_WARNING:
				setErrorText(R.string.login_attempt_warning);
				return true;
			
			case STRONG_AUTH_NOT_ENROLLED:
				sendToErrorPage(R.string.account_security_title_text,R.string.account_security_not_enrolled);
				return true;
				
			case AUTH_BAD_ACCOUNT_STATUS:
				sendToErrorPage(R.string.zluba_error);
				return true;
				
			case ACCOUNT_NUMBER_REREGISTERED:
				sendToErrorPage(R.string.account_number_reregistered);
				return true;
				
			case EXCEEDED_LOGIN_ATTEMPTS:
				sendToErrorPage(R.string.secure_login,R.string.max_attempts_exceeded_text);
				return true;
				
			case LOCKED_OUT_ACCOUNT:
				TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LOCKED);
				// TODO sseward: reference lock out text when error map set up
				sendToErrorPage(R.string.locked_account);
				return true;
				
			case ACCOUNT_SETUP_PENDING:
				sendToErrorPage(R.string.secure_login,R.string.account_setup_pending);
				return true;
				
			case ACCOUNT_NUMBER_CHANGED:
				sendToErrorPage(R.string.account_number_changed);
				return true;
			
		}
		return false;
	}
	
	
	
	
}
