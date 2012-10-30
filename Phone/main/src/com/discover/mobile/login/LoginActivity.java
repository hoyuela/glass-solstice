package com.discover.mobile.login;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends Activity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();
		
	private EditText uidField, passField;
	private TextView errorTextView;
	private String uid, pass;
	
	// currently not used for testing (does not apply to test id's)
	
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
		
		setupViews();
		setupButtons();
	}
	
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<AccountDetails> callback = new AsyncCallbackAdapter<AccountDetails>() {
			@Override
			public void success(final AccountDetails value) {
				progress.dismiss();
				handleSuccessfulAuth();
			}

			// TODO use or remove (commented because AsyncCallbackAdapter now has default handlers for this)
//			@Override
//			public void failure(final Throwable error) {
//				Log.e(TAG, "Error: " + error);
//			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
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
				
				progress.dismiss();
				nullifyInputs();
				errorTextView.setText(messageErrorResponse.getMessage());
				
				return true;
			}
		};
		
		final AuthenticateCall authenticateCall = new AuthenticateCall(this, callback, username, password);
		authenticateCall.submit();
	}
	
	private void handleSuccessfulAuth() {
		nullifyInputs();
		final Intent logIn = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(logIn);
	}
	
	private void setupViews() {
		setContentView(R.layout.login);
		passField = (EditText)findViewById(R.id.password);
		uidField = (EditText)findViewById(R.id.username);
		errorTextView = (TextView)findViewById(R.id.error_text_view);
	}
	
	private void setupButtons(){
		final Button loginButton = (Button)findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(null);
				logIn();
			}
		});
	}
	
	private void logIn(){
		uid = uidField.getText().toString();
		pass = passField.getText().toString();
		
		// TODO production error handling (validator doesn't work with test uid's)
		runAuthWithUsernameAndPassword(uid, pass);
	}
	
	public void registerNewUser(final View v){
		final Intent accountInformationActivity = new Intent(
				this, AccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	// TODO create Credentials object to handle any Model-like properties
	public void nullifyInputs(){
		uid = null;
		pass = null;
		errorTextView.setText(null);
		passField.setText("");
		uidField.setText("");
	}
}
