package com.discover.mobile.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.AsyncCallback;

public class LandingActivity extends Activity{
	
	private static final String TAG = LoginActivity.class.getSimpleName();

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		startPreAuthCheck();
		
		setupViews();
		setupButtons();
	}
	
	private void startPreAuthCheck() {
		Log.d(TAG, "starting pre-auth check");
		
		final AsyncCallback<PreAuthResult> callback = new AsyncCallback<PreAuthResult>() {
			@Override
			public void success(final PreAuthResult value) {
				Log.d(TAG, "pre-auth check successful");
				
				// TODO
				
				// TEMP
				Log.e(TAG, "Status code: " + value.statusCode);
			}

			@Override
			public void failure(final Throwable error) {
				Log.d(TAG, "pre-auth check failed");
				
				// TODO
				
				// TEMP
				Log.e(TAG, "Error: " + error);
			}
		};
		final PreAuthCheckCall preAuthCall = new PreAuthCheckCall(this, callback);
		preAuthCall.submit();
	}
	
	private void setupViews() {
		setContentView(R.layout.landing);
	}
	
	private void setupButtons(){
		//Setup the button that takes us to the login screen.
		final Button creditCardLoginButton = (Button)findViewById(R.id.card_login_button);
		
		creditCardLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				navigateToLogin();
			}
		});
	}
	
	private void navigateToLogin(){
		final Intent loginActivity = new Intent(this, LoginActivity.class);
		this.startActivity(loginActivity);
	}
}
