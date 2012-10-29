package com.discover.mobile.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;

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
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<PreAuthResult> callback = new AsyncCallbackAdapter<PreAuthResult>() {
			@Override
			public void success(final PreAuthResult value) {
				progress.dismiss();
				Log.d(TAG, "pre-auth check successful");
				Log.e(TAG, "Status code: " + value.statusCode);
			}

			@Override
			public void failure(final Throwable error) {
				progress.dismiss();
				Log.e(TAG, "UpdateSessionCall.failure(Throwable): " + error);
			}

			@Override
			public void errorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				Log.e(TAG, "UpdateSessionCall.errorResponse(ErrorResponse): " + errorResponse);
			}

			@Override
			public void messageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				Log.e(TAG, "UpdateSessionCall.messageErrorResponse(MessageErrorResponse): " + messageErrorResponse);
				switch(messageErrorResponse.getMessageStatusCode()) {
					case 1002: 
						Log.e(TAG, "Invalid client version");
						break;
					case 1006:
					case 1007: 
						Log.e(TAG, "Send to maintainance page.");
						break;
				}
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
