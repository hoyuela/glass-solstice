package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_1;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_2;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
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

public class LoginErrorResponseHandler implements ErrorResponseHandler{
	
	private Context context;
	private TextView errorLabel;
	private EditText idField, passField;
	
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
	
	@Override
	public boolean handleFailure(final ErrorResponse<?> errorResponse) {
		if(errorResponse instanceof JsonMessageErrorResponse)
			return handleMessageErrorResponse((JsonMessageErrorResponse)errorResponse);
		
		switch(errorResponse.getHttpStatusCode()) {
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				showLabelWithTextResource(errorLabel, R.string.login_error);
				idField.setError("Please Check your ID and Try Again");
				passField.setError("Please Check your Password and Try Again");
				return true;
			
			// FIXME other cases
		}
		
		return false;
	}
	
	public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
		
		if(messageErrorResponse.getHttpStatusCode() != HttpURLConnection.HTTP_FORBIDDEN)
			return false;
		
		// FIXME convert other error codes to standard constants
		switch(messageErrorResponse.getMessageStatusCode()) {
			case MAINTENANCE_MODE_1:
			case MAINTENANCE_MODE_2: 
				sendToErrorPage(ScreenType.MAINTENANCE);
				return true;
			
			case STRONG_AUTH_NOT_ENROLLED:
				sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
				return true;
				
			case AUTH_BAD_ACCOUNT_STATUS:
				sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
				return true;
				
			case EXCEEDED_LOGIN_ATTEMPTS:
			case LOCKED_OUT_ACCOUNT:
				sendToErrorPage(ScreenType.LOCKED_OUT_USER);
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
	
	private void showLabelWithTextResource(TextView label, int stringResource) {
		label.setText(context.getResources().getString(stringResource));
		label.setVisibility(View.VISIBLE);	
	}
	
}
