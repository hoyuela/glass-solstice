package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.DeleteSessionCall;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;

@ContentView(R.layout.logged_in_landing)
public class LoggedInLandingPage extends RoboActivity {
	private static final String TAG = LoggedInLandingPage.class.getSimpleName();
	
	@InjectView(R.id.optionsTable)
	ListView listView;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LANDING);
		
		listView.setAdapter(null);
	}
	
	@Override
	public void onBackPressed() {
		final Intent navToMain = new Intent(this, LoginActivity.class);
		startActivity(navToMain);
	}
	
	public void logout(View v) {
		final AsyncCallback<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				Log.d(TAG, "Logout Success");
				navigateToSecureLoginScreen();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				
				Log.w(TAG, "RegistrationCallOne.errorResponse(ErrorResponse): " + errorResponse);
				
				switch (errorResponse.getHttpStatusCode()) {
					
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());

				// FIXME add "assertions" for what the HTTP status code should be
				switch(messageErrorResponse.getMessageStatusCode()){
				
						
				}
				return false;
			}
		};

		final DeleteSessionCall logOut = new DeleteSessionCall(this, callback);
		logOut.submit();
		
	}
	
	private void navigateToSecureLoginScreen() {
		Intent loginScreen = new Intent(this, LoginActivity.class);
		startActivity(loginScreen);
	
	}

	
}
