package com.discover.mobile.login;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.register.AccountInformationActivity;

@ContentView(R.layout.login)
public class LoginActivity extends RoboActivity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();

	@InjectView(R.id.username)
	private EditText uidField;
	
	@InjectView(R.id.password)
	private EditText passField;

	@InjectView(R.id.login_button)
	private Button loginButton;

	@InjectView(R.id.error_text_view)
	private TextView errorTextView;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
				errorTextView.setText(null);
				logIn();
			}
		});
	}
	
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<AccountDetails> callback = new AsyncCallbackAdapter<AccountDetails>() {
			@Override
			public void success(final AccountDetails value) {
				Log.d(TAG, "Success");
				progress.dismiss();
				handleSuccessfulAuth();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				Log.w(TAG, "AuthenticateCall.errorResponse(ErrorResponse): " + errorResponse);
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
//					case HttpURLConnection.HTTP_BAD_REQUEST: // TODO figure out if this actually happens
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						errorTextView.setText(getString(R.string.login_error));
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				if(messageErrorResponse.getHttpStatusCode() != HttpURLConnection.HTTP_FORBIDDEN)
					return false;
				
				Log.e(TAG, "AuthenticateCall.messageErrorResponse(MessageErrorResponse): " + messageErrorResponse);
				progress.dismiss();
				clearInputs();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				errorTextView.setText(messageErrorResponse.getMessage());
				
				return true;
			}
		};
		
		final AuthenticateCall authenticateCall = new AuthenticateCall(this, callback, username, password);
		authenticateCall.submit();
	}
	
	private void handleSuccessfulAuth() {
		clearInputs();
		
		final Intent logIn = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(logIn);
	}
	
	private void logIn(){
		final String uid = uidField.getText().toString();
		final String pass = passField.getText().toString();
		
		// TODO production error handling (validator doesn't work with test uid's)
		runAuthWithUsernameAndPassword(uid, pass);
	}
	
	public void registerNewUser(@SuppressWarnings("unused") final View v) {
		final Intent accountInformationActivity = new Intent(
				this, AccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	// TODO create Credentials object to handle any Model-like properties
	public void clearInputs() {
		errorTextView.setText("");
		passField.setText("");
		uidField.setText("");
	}
	
}
