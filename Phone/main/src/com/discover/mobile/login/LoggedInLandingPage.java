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
import com.discover.mobile.common.net.response.AsyncCallback;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;

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
			//TODO Determine how errors are to be handled - no official word on it yet.
			// Phone gap application doesn't handle any errors. Expects 204 success only.
			@Override
			public void success(final Object value) {
				Log.d(TAG, "Logout Success");
				navigateToSecureLoginScreen();
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
