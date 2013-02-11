/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.error;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.delegates.DelegateFactory;

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
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
		DelegateFactory.getLoginDelegate().navToLoginWithMessage(activity, bundle);
	}
}
