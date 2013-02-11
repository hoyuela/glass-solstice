/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.error;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.common.IntentExtraKey;

/**
 * DismissListener which can be applied to an alert dialog to navigate back
 * to the login page when it is dismissed.
 * 
 * @author henryoyuela
 * 
 */
public class NavigateToLoginOnDismiss implements OnDismissListener {
	
	static final String TAG = CloseApplicationOnDismiss.class.getSimpleName();
	
	
	// Reference of the activity to be able to close out the application
	private final Activity activity;

	public NavigateToLoginOnDismiss(final Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		// Send an intent to open login activity if current activity is not
		// login
		if (this.activity.getClass() != LoginActivity.class) {
			final Intent intent = new Intent(activity, LoginActivity.class);
			final Bundle bundle = new Bundle();
			bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
			intent.putExtras(bundle);
			activity.startActivity(intent);

			// Close current activity
			activity.finish();
		} else {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "Application is already in login view");
			}
		}
	}
}
