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
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.AsyncCallback;

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
		
		final AsyncCallback<PreAuthResult> callback = new AsyncCallback<PreAuthCheckCall.PreAuthResult>() {
			@Override
			public void success(final PreAuthResult value) {
				Log.e(TAG, "Status code: " + value.statusCode);
			}

			@Override
			public void failure(final Throwable error) {
				Log.e(TAG, "Error: " + error);
			}
		};
		final PreAuthCheckCall preAuthCall = new PreAuthCheckCall(this, callback);
		preAuthCall.submit();
		Log.e(TAG, "onStart() done");
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
			String errMsg = getString(R.string.login_error);
			errorTextView.setText(errMsg);
		}
	}
	
	public void nullifyInputs(){
		uid = null;
		pass = null;
		errorTextView.setText(null);
		passField.setText("");
		uidField.setText("");
	}
}
