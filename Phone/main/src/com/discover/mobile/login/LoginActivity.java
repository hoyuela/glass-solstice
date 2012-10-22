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

public class LoginActivity extends Activity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();
		
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupViews();
		setupButtons();
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
			public void error(final Object error) {
				Log.e(TAG, "Error: " + error);
			}
		};
		final PreAuthCheckCall preAuthCall = new PreAuthCheckCall(this, callback);
		preAuthCall.submit();
		Log.e(TAG, "OnStart() done");
	}
	
	private void setupViews() {
		setContentView(R.layout.login);
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
		final Intent logIn = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(logIn);
	}
}
