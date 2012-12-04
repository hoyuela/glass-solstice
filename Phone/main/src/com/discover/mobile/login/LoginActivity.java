package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_1;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_2;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.discover.mobile.R;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.register.ForgotTypeSelectionActivity;
import com.discover.mobile.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.google.common.base.Strings;
import com.google.inject.Inject;

@ContentView(R.layout.login_start)
public class LoginActivity extends RoboActivity {
	
	@Inject
	private CurrentSessionDetails currentSessionDetails;

	@InjectView(R.id.toggle_button_save_user_id)
	private ToggleButton saveUserButton;
	
	@InjectView(R.id.username)
	private EditText uidField;
	
	@InjectView(R.id.password)
	private EditText passField;

	@InjectView(R.id.login_button)
	private Button loginButton;
	
	@InjectView(R.id.register_text)
	private TextView registerText;

	@InjectView(R.id.error_text_view)
	private TextView errorTextView;
	
	@InjectView(R.id.forgot_uid_or_pass_text)
	private TextView forgotUserIdOrPassText;
		
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
		
		setupButtons();
	}
	
	private void setupButtons() {
		loginButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(""); //$NON-NLS-1$
				logIn();
			}
		});
		
		registerText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(""); //$NON-NLS-1$
				registerNewUser();
			}
		});
		
		forgotUserIdOrPassText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(""); //$NON-NLS-1$
				forgotIdAndOrPass();
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		clearInputs();
	}
	
	private final static String emptyString = ""; //$NON-NLS-1$
	
	private void clearInputs() {
		uidField.setText(emptyString);
		passField.setText(emptyString);
	}
	
	private void logIn() {
		//If the user id, or password field are effectively blank, do not allow a service call to be made
		//display the error message for id/pass not matching records.
		if(Strings.isNullOrEmpty(uidField.getText().toString()) ||
			Strings.isNullOrEmpty(passField.getText().toString()))
			errorTextView.setText(getString(R.string.login_error));
		else
			runAuthWithUsernameAndPassword(uidField.getText().toString(), passField.getText().toString());
	}
	
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback.<AccountDetails>builder(this)
					.showProgressDialog("Discover", "Loading...", true)
					.clearTextViewsOnComplete(errorTextView, passField, uidField)
					.launchIntentOnSuccess(NavigationRootActivity.class)
					.withSuccessListener(new SuccessListener<AccountDetails>() {
						
						@Override
						public CallbackPriority getCallbackPriority() {
							return CallbackPriority.MIDDLE;
						}
						
						@Override
						public void success(AccountDetails value) {
							currentSessionDetails.setAccountDetails(value);
						}
					})
					
					// FIXME DO NOT COPY THIS CODE
					.withErrorResponseHandler(new ErrorResponseHandler() {
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
									errorTextView.setText(getString(R.string.login_error));
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
									
								case 1102:
									sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
									return true;
									
								case 1101:
								case 1402:
									sendToErrorPage(ScreenType.LOCKED_OUT_USER);
									return true;
									
								default:
									errorTextView.setText(messageErrorResponse.getMessage());
									return true;
							}
						}
					})
					
					.build();
		
		new AuthenticateCall(this, callback, username, password).submit();
	}
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(LoginActivity.this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
	}
	
	private void showOkAlertDialog(final String title, final String message) {
		new AlertDialog.Builder(this)
			    .setTitle(title)
			    .setMessage(message)
			    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
						finish();
					}
				})
			    .show();
	}
	
	public void registerNewUser() {
		final Intent accountInformationActivity = new Intent(this, RegistrationAccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	public void forgotIdAndOrPass(){
		final Intent forgotIdAndOrPassActivity = new Intent(this, ForgotTypeSelectionActivity.class);
		this.startActivity(forgotIdAndOrPassActivity);
	}
	
}
