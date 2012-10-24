package com.discover.mobile.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.InputValidator;

public class LoginActivity extends Activity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();
		
	private EditText uidField, passField;
	private TextView errorTextView;
	private String uid, pass;
	
	private static InputValidator validator;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupViews();
		setupButtons();
		validator = new InputValidator();
	}
	
	// TEMP
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.e(TAG, "onStart()");
		
//		runPreAuth();
//		runAuth();
		
		Log.e(TAG, "onStart() done");
	}
	
//	private void runPreAuth() {
//		final AsyncCallback<PreAuthResult> callback = new AsyncCallback<PreAuthCheckCall.PreAuthResult>() {
//			@Override
//			public void success(final PreAuthResult value) {
//				Log.e(TAG, "Status code: " + value.statusCode);
//			}
//
//			@Override
//			public void failure(final Throwable error) {
//				Log.e(TAG, "Error: " + error);
//			}
//		};
//		final PreAuthCheckCall preAuthCall = new PreAuthCheckCall(this, callback);
//		preAuthCall.submit();
//	}
//	
//	private void runAuth() {
//		final AsyncCallback<Object> callback = new AsyncCallback<Object>() {
//			@Override
//			public void success(final Object value) {
//				Log.e(TAG, "Value: " + CookieData.getInstance().getSecToken());
//				Log.e(TAG, "running update now");
//				runUpdate();
//			}
//
//			@Override
//			public void failure(final Throwable error) {
//				Log.e(TAG, "Error: " + error);
//			}
//		};
//		final AuthenticateCall authCall = new AuthenticateCall(this, callback);
//		authCall.submit();
//	}
//	
//	private void runUpdate() {
//		final AsyncCallback<UpdateSessionResult> callback = new AsyncCallback<UpdateSessionCall.UpdateSessionResult>() {
//
//			@Override
//			public void success(UpdateSessionResult value) {
//				Log.e(TAG, "Status code for update: " + value.statusCode);
//			}
//
//			@Override
//			public void failure(Throwable error) {
//				Log.e(TAG, "Error: " + error);
//			}
//		};
//		
//		final UpdateSessionCall updateCall = new UpdateSessionCall(this, callback);
//		updateCall.submit();
//	}
//	
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
				logIn();
			}
		});
	}
	
	private void logIn(){
		uid = uidField.getText().toString();
		pass = passField.getText().toString();
		
		if(validator.validateCredentials(uid, pass)){
			nullifyInputs();
			final Intent logIn = new Intent(this, LoggedInLandingPage.class);
			this.startActivity(logIn);
		}
		else{
			nullifyInputs();
			final String errMsg = getString(R.string.login_error);
			errorTextView.setText(errMsg);
		}
	}
	
	public void registerNewUser(View v){
		final Intent accountInformationActivity = new Intent(
				this, AccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	private void nullifyInputs(){
		uid = null;
		pass = null;
		errorTextView.setText(null);
		passField.setText("");
		uidField.setText("");
	}
}
