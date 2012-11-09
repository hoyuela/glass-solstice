package com.discover.mobile.login;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.forgotuidpassword.ForgotCredentialsActivity;
import com.discover.mobile.register.AccountInformationActivity;
import com.google.common.base.Strings;

@ContentView(R.layout.login)
public class LoginActivity extends RoboActivity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();

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
		
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectDiskReads()
//        .detectDiskWrites()
//        .detectNetwork()   // or .detectAll() for all detectable problems
//        .penaltyLog()
//        .build());
//		
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectLeakedSqlLiteObjects()
//        .detectLeakedClosableObjects()
//        .penaltyLog()
//        .penaltyDeath()
//        .build());
		
		setupButtons();
	}
	
	private void setupButtons() {
		loginButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText("");
				logIn();
			}
		});
		
		registerText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText("");
				registerNewUser();
			}
		});
		
		forgotUserIdOrPassText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(null);
				forgotIdAndOrPass();
			}
		});
	}
	
	private void logIn() {
		//If the user id, or password field are effectively blank, do not allow a service call to be made
		//display the error message for id/pass not matching records.
		if(getResources().getString(R.string.username_placeholder).equals(uidField.getText().toString()) ||
			getResources().getString(R.string.password_placeholder).equals(passField.getText().toString()) ||
			Strings.isNullOrEmpty(uidField.getText().toString()) ||
			Strings.isNullOrEmpty(passField.getText().toString()))
			errorTextView.setText(getString(R.string.login_error));
		else
			runAuthWithUsernameAndPassword(uidField.getText().toString(), passField.getText().toString());
	}
	
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		final AsyncCallbackAdapter<AccountDetails> callbackDelegate = new AsyncCallbackAdapter<AccountDetails>() {
			@Override
			public void failure(final Throwable error) {
				Log.e(TAG, "Error: " + error.getMessage());
				showOkAlertDialog("Error", error.getMessage());
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						errorTextView.setText(getString(R.string.login_error));
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
				if(messageErrorResponse.getHttpStatusCode() != HttpURLConnection.HTTP_FORBIDDEN)
					return false;
				
				switch(messageErrorResponse.getMessageStatusCode()) {
					case 1006:
					case 1007: 
						sendToErrorPage(ScreenType.MAINTENANCE);
						return true;
					
					case 1101:
					case 1402:
						sendToErrorPage(ScreenType.LOCKED_OUT_USER);
						return true;
				}
				
				errorTextView.setText(messageErrorResponse.getMessage());
				
				return true;
			}
		};
		
		final AsyncCallback<AccountDetails> callback =
				GenericAsyncCallback.<AccountDetails>builder(this)
					.showProgressDialog("Discover", "Loading...", true)
					.clearTextViewsOnComplete(errorTextView, passField, uidField)
					.launchIntentOnSuccess(LoggedInLandingPage.class)
					.build();
		
		new AuthenticateCall(this, callback, username, password).submit();
	}
	
	private void sendToErrorPage(final int screenType) {
		final Intent maintenancePageIntent = new Intent(LoginActivity.this, LockOutUserActivity.class);
		maintenancePageIntent.putExtra(IntentExtraKey.SCREEN_TYPE, screenType);
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
		final Intent accountInformationActivity = new Intent(
				this, AccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	public void forgotIdAndOrPass(){
		final Intent forgotIdAndOrPassActivity = new Intent(this, ForgotCredentialsActivity.class);
		this.startActivity(forgotIdAndOrPassActivity);
	}
	
}
