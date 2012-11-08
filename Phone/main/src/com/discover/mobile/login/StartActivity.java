package com.discover.mobile.login;

import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;

@ContentView(R.layout.landing)
public class StartActivity extends RoboActivity {
	
	private static final String TAG = StartActivity.class.getSimpleName();
	
	private static final String PACKAGE_NAME = "com.discover.mobile.DiscoverMobileActivity";
	private static final int OPTIONAL_UPGRADE_DAYS = 30;
	
	private static final int NO_DATE = 0;
	
	private static final int OPTIONAL_UPGRADE = 1;
	private static final int FORCED_UPGRADE = 2;
	
	private static final long DAY_IN_MILLISECONDS = 86400000;
	
	private static final String DATETIME_KEY = "com.discover.mobile.optionalupdatedate";
	
	@InjectView(R.id.card_login_button)
	private Button creditCardLoginButton;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TrackingHelper.startActivity(this);
		TrackingHelper.trackPageView("login-pg");
		
		setupButtons();
	}
	
	private void setupButtons() {
		//Setup the button that takes us to the login screen.
		creditCardLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startPreAuthCheck();
			}
		});
	}
	
	private void startPreAuthCheck() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<PreAuthResult> callback = new AsyncCallbackAdapter<PreAuthResult>() {
			@Override
			public void success(final PreAuthResult value) {
				progress.dismiss();
				Log.d(TAG, "Pre-auth status code: " + value.statusCode);
				
				//check if optional upgrade available
				if(shouldPresentOptionalUpdate(value.upgradeDescription)) {
					showUpgradeAlertDialog("Upgrade", 
							value.upgradeDescription, 
							OPTIONAL_UPGRADE);
				} else {
					navigateToLogin();
				}
			}

			@Override
			public void failure(final Throwable error) {
				progress.dismiss();
				showOkAlertDialog("Error", error.getMessage());
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				switch(messageErrorResponse.getMessageStatusCode()) {
					case 1002: 
						showUpgradeAlertDialog("Upgrade", 
								"Your Discover app is out of date. You must update before continuing.", 
								FORCED_UPGRADE);
						removeDateFromPrefs();
						return true;
						
					case 1006:
					case 1007: 
						sendToMaintenancePage();
						return true;
				}
				
				return false;
			}
		};
		final PreAuthCheckCall preAuthCall = new PreAuthCheckCall(this, callback);
		preAuthCall.submit();
	}
	
	private void sendToMaintenancePage() {
		final Intent maintenancePageIntent = new Intent(StartActivity.this, LockOutUserActivity.class);
		maintenancePageIntent.putExtra("ScreenType", ScreenType.MAINTENANCE);
		startActivity(maintenancePageIntent);
	}
	
	private boolean shouldPresentOptionalUpdate(final String updateDescription) {
		if(updateDescription != null) {
			final SharedPreferences prefs=getPreferences(Context.MODE_PRIVATE);
			
			final long savedDate = prefs.getLong(DATETIME_KEY, 0);
			
			if (savedDate == 0) {
				final SharedPreferences.Editor editor=prefs.edit();
				editor.putLong(DATETIME_KEY, new Date().getTime());
				editor.commit();
				return true;
			}
			
			final long currentDate = new Date().getTime();
			final long daysDifference = (currentDate - savedDate)/DAY_IN_MILLISECONDS;
			
			if(daysDifference >= OPTIONAL_UPGRADE_DAYS) {
				removeDateFromPrefs();
				return true;
			}
			
			return false;
		}
		
		return false;
	}
	
	private void removeDateFromPrefs() {
		final SharedPreferences prefs=getPreferences(Context.MODE_PRIVATE);
		
		final long savedDate = prefs.getLong(DATETIME_KEY, NO_DATE);
		
		if(savedDate != NO_DATE) {
			final SharedPreferences.Editor editor=prefs.edit();
			editor.remove(DATETIME_KEY);
			editor.commit();
		}
	}
	
	private void showOkAlertDialog(final String title, final String message) {
		new AlertDialog.Builder(this)
			    .setTitle(title)
			    .setMessage(message)
			    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
						finish();
					}
				})
			    .show();
	}
	
	private void showUpgradeAlertDialog(final String title, final String message, final int typeOfUpgrade) {
		final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		
		alertBuilder.setTitle(title)
	    		.setMessage(message)
	    		.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						upgrade();
					}
				});
				
		if (typeOfUpgrade == OPTIONAL_UPGRADE) alertBuilder.setNegativeButton("Cancel", null);
		
	    alertBuilder.show();
	}
	
	private void upgrade() {
		final Uri marketUri = Uri.parse("market://details?id=" + PACKAGE_NAME);
		final Intent androidMarketplaceIntent = new Intent(Intent.ACTION_VIEW, marketUri);
		startActivity(androidMarketplaceIntent);
	}
	
	private void navigateToLogin() {
		final Intent loginActivity = new Intent(this, LoginActivity.class);
		this.startActivity(loginActivity);
	}
	
}
