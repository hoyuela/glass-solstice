package com.discover.mobile.login;

import android.app.Activity;
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
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.UpdateSessionCall;
import com.discover.mobile.common.auth.UpdateSessionCall.UpdateSessionResult;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;

public class LoginActivity extends Activity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();
		
	private EditText uidField, passField;
	private TextView errorTextView;
	private String uid, pass;
	
	// currently not used for testing (does not apply to test id's)
	private static InputValidator validator;
	
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
		validator = new InputValidator();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// TEMP testing calls
//		testAuthAndUpdate();
	}
	
	// TEMP
	private void testAuthAndUpdate() {
		Log.e(TAG, "testAuthAndUpdate() start");
		new AuthenticateCall(this, new AsyncCallbackAdapter<AccountDetails>() {
			@Override
			public void success(final AccountDetails value) {
				new UpdateSessionCall(LoginActivity.this, new AsyncCallbackAdapter<UpdateSessionResult>() {
					@Override
					public void success(final UpdateSessionResult value) {
						Log.e(TAG, "Status code for update: " + value.statusCode);
					}

					@Override
					public void failure(final Throwable error) {
						Log.e(TAG, "UpdateSessionCall.failure(Throwable): " + error);
					}

					@Override
					public void errorResponse(final ErrorResponse errorResponse) {
						Log.e(TAG, "UpdateSessionCall.errorResponse(ErrorResponse): " + errorResponse);
					}

					@Override
					public void messageErrorResponse(final MessageErrorResponse messageErrorResponse) {
						Log.e(TAG, "UpdateSessionCall.messageErrorResponse(MessageErrorResponse): " + messageErrorResponse);
					}
				}).submit();
			}
			
			@Override
			public void failure(final Throwable error) {
				Log.e(TAG, "AuthenticateCall.failure(Throwable): " + error);
			}

			@Override
			public void errorResponse(final ErrorResponse errorResponse) {
				Log.e(TAG, "AuthenticateCall.errorResponse(ErrorResponse): " + errorResponse);
			}

			@Override
			public void messageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				Log.e(TAG, "AuthenticateCall.messageErrorResponse(MessageErrorResponse): " + messageErrorResponse);
			}
		}, "uid6478a", "ccccc").submit();
	}
	
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallback<AccountDetails> callback = new AsyncCallbackAdapter<AccountDetails>() {
			@Override
			public void success(final AccountDetails value) {
				Log.d(TAG, "Success");
				progress.dismiss();
				handleSuccessfulAuth();
			}

			@Override
			public void failure(final Throwable error) {
				Log.e(TAG, "Error: " + error);
			}

			@Override
			public void errorResponse(final ErrorResponse errorResponse) {
				Log.e(TAG, "AuthenticateCall.errorResponse(ErrorResponse): " + errorResponse);
				progress.dismiss();
				
				if(errorResponse.getHttpStatusCode() == 401)
					errorTextView.setText(getString(R.string.login_error));
				else if(errorResponse.getHttpStatusCode() == 400) {
					// TODO handle this some other way (crashes on Bedford's phone otherwise)
					errorTextView.setText(getString(R.string.login_error));
				} else
					throw new UnsupportedOperationException("Not able to handle status other than 401");
			}

			@Override
			public void messageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				Log.e(TAG, "AuthenticateCall.messageErrorResponse(MessageErrorResponse): " + messageErrorResponse);
				progress.dismiss();
				nullifyInputs();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				errorTextView.setText(messageErrorResponse.getMessage());
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
		
//		if(validator.validateCredentials(uid, pass)){
			runAuthWithUsernameAndPassword(uid, pass);
//		}
//		else{
//			nullifyInputs();
//			final String errMsg = getString(R.string.login_error);
//			errorTextView.setText(errMsg);
//		}
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
