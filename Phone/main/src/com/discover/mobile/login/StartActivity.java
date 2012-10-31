package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

@ContentView(R.layout.landing)
public class StartActivity extends RoboActivity {
	
	private static final String TAG = StartActivity.class.getSimpleName();
	
	private static final int OPTIONAL_UPGRADE = 1;
	private static final int FORCED_UPGRADE = 2;
	
	@InjectView(R.id.card_login_button)
	private Button creditCardLoginButton;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TODO consider running this after a brief delay so that involved
		// classloading/initialization doesn't delay startup
		startPreAuthCheck();
		
		setupButtons();
	}
	
	private void setupButtons() {
		//Setup the button that takes us to the login screen.
		creditCardLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				navigateToLogin();
			}
		});
	}
	
	private void startPreAuthCheck() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<PreAuthResult> callback = new AsyncCallbackAdapter<PreAuthResult>() {
			@Override
			public void success(final PreAuthResult value) {
				progress.dismiss();
				Log.e(TAG, "Status code: " + value.statusCode);
				
				//check if optional upgrade available
				if (value.upgradeDescription != null) {
					if(shouldPresentOptionalUpdate(value.upgradeDescription)) {
						showUpgradeAlertDialog("Upgrade", 
								"You are currently not running the latest version of the Discover app. Would you like to upgrade?", 
								OPTIONAL_UPGRADE);
					}
				}
			}

			@Override
			public void failure(final Throwable error) {
				progress.dismiss();
				showOkAlertDialog("Error", error.getMessage());
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				// TEMP
				return true;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				switch(messageErrorResponse.getMessageStatusCode()) {
					case 1002: 
						showUpgradeAlertDialog("Upgrade", 
								"Your Discover app is out of date. You must update before continuing.", 
								FORCED_UPGRADE);
						break;
						
					case 1006:
					case 1007: 
						// TODO
						Log.e(TAG, "Send to maintainance page.");
						break;
				}
				
				// TEMP
				return true;
			}
		};
		final PreAuthCheckCall preAuthCall = new PreAuthCheckCall(this, callback);
		preAuthCall.submit();
	}
	
	private static boolean shouldPresentOptionalUpdate(final String updateDescription) {
		// TODO check if there is saved date
		// TODO if there is, see if 30 days have passed
		// TODO if 30 days have passed, return true
		// TODO if there is no saved date, save it to local storage
		
		// TEMP
		return true;
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
		// TODO send to upgrade URI
	}
	
	private void navigateToLogin() {
		final Intent loginActivity = new Intent(this, LoginActivity.class);
		this.startActivity(loginActivity);
	}
	
}
