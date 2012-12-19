package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_NUMBER_CHANGED;
import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_NUMBER_REREGISTERED;
import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_SETUP_PENDING;
import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.NO_DATA_FOUND;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.StandardErrorCodes.UNSCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;

import java.net.HttpURLConnection;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * Error response handler for the login service call.
 * 
 * Handles errors provided by the server to either show appropriate error messages on the login screen, or
 * send the user to a different error screen.
 * 
 * @author scottseward
 *
 */
public class LoginErrorResponseHandler implements ErrorResponseHandler{
	/** The login context */
	private Context context;
	
	/** The error label on the login screen that gets updated with error messages */
	private TextView errorLabel;
	
	/** The input fields on the login screen */
	private EditText idField, passField;
	
	/**
	 * LoginErrorResponseHandler requires the context of use, input fields for id, and password, and error label
	 * to be updated in the event of certain errors.
	 * 
	 * @param context - the context of use, expected to be the login screen
	 * @param errorLabel - a TextView to be used to display error messages.
	 * @param idField - the input field that will provide a users ID when logging in.
	 * @param passField - the input field that will provide a users password when logging in.
	 */
	public LoginErrorResponseHandler(final Context context, final TextView errorLabel, final EditText idField, 
			final EditText passField) {
		this.context = context;
		this.errorLabel = errorLabel;
		this.idField = idField;
		this.passField = passField;
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}
	
	/**
	 * If we were unable to handle a message error response, attempt to generally handle 
	 * the response by HTTP status codes.
	 */
	@Override
	public boolean handleFailure(final ErrorResponse<?> errorResponse) {
		if(errorResponse instanceof JsonMessageErrorResponse)
			return handleMessageErrorResponse((JsonMessageErrorResponse)errorResponse);
		
		switch(errorResponse.getHttpStatusCode()) {
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				showLabelWithTextResource(errorLabel, R.string.login_error);
				idField.setError(context.getResources().getString(R.string.id_invalid));
				passField.setError(context.getResources().getString(R.string.pass_invalid));
				return true;
				
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				sendToErrorPage(ScreenType.INTERNAL_SERVER_ERROR_500);
				return true;
				
			case HttpURLConnection.HTTP_UNAVAILABLE:
				sendToErrorPage(ScreenType.INTERNAL_SERVER_ERROR_503);
				return true;
				
			case HttpURLConnection.HTTP_FORBIDDEN:
				sendToErrorPage(ScreenType.HTTP_FORBIDDEN);
				return true;
				
			case 0:
				sendToErrorPage(ScreenType.TEMPORARY_OUTAGE);
				return true;
			// FIXME other cases
		}
		
		return false;
	}
	
	/**
	 * Clear the input fields.
	 */
	private void clearInputs() {
		idField.setText("");
		passField.setText("");
	}
	
	/**
	 * When the server responds with a specific error message, handle this error.
	 * 
	 * @param messageErrorResponse - the error returned from the server
	 * @return returns true if the given error was handled by this method, false otherwise.
	 */
	public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
		
		clearInputs();

		switch(messageErrorResponse.getMessageStatusCode()) {
			case UNSCHEDULED_MAINTENANCE:
				sendToErrorPage(ScreenType.UNSCHEDULED_MAINTENANCE);
				return true;
				
			case SCHEDULED_MAINTENANCE: 
				sendToErrorPage(ScreenType.SCHEDULED_MAINTENANCE);
				return true;
				
			case LAST_ATTEMPT_WARNING:
				showLabelWithTextResource(errorLabel, R.string.login_attempt_warning);
				return true;
			
			case STRONG_AUTH_NOT_ENROLLED:
				sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
				return true;
				
			case AUTH_BAD_ACCOUNT_STATUS:
				sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
				return true;
				
			case ACCOUNT_NUMBER_REREGISTERED:
				sendToErrorPage(ScreenType.ACCOUNT_NUMBER_REREGISTERED);
				return true;
				
			case EXCEEDED_LOGIN_ATTEMPTS:
				sendToErrorPage(ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
				return true;
				
			case LOCKED_OUT_ACCOUNT:
				sendToErrorPage(ScreenType.LOCKED_OUT_USER);
				return true;
				
			case ACCOUNT_SETUP_PENDING:
				sendToErrorPage(ScreenType.ACCOUNT_NOT_YET_SETUP);
				return true;
				
			case ACCOUNT_NUMBER_CHANGED:
				sendToErrorPage(ScreenType.ACCOUNT_NUMBER_CHANGED);
				return true;
				
			case PLANNED_OUTAGE:
				sendToErrorPage(ScreenType.SCHEDULED_MAINTENANCE);
				return true;
				
			case NO_DATA_FOUND:
				sendToErrorPage(ScreenType.NO_DATA_FOUND);
				return true;
				
			default:
				errorLabel.setText(messageErrorResponse.getMessage());
				return true;
		}
	}
	/**
	 * sendToErrorPage(final ScreenType screenType)
	 * This method, on a critical login error, will send the user to a screen that will prevent them
	 * from further action. This is used for various kinda of 'locked out' users.
	 */
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(context, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		context.startActivity(maintenancePageIntent);
	}
	
	/**
	 * Sets the text of and makes visible, a given TextView reference.
	 * 
	 * @param label A TextView reference to set the text of.
	 * @param stringResource The string resource to use to set the text of the TextView.
	 */
	private void showLabelWithTextResource(TextView label, int stringResource) {
		label.setText(context.getResources().getString(stringResource));
		label.setVisibility(View.VISIBLE);	
	}
	
}
