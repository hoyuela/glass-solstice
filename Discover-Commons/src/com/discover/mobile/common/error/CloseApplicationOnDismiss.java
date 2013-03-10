/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.error;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;


/**
 * DismissListener which can be applied to an alert dialog to close the
 * application when it is dismissed.
 * 
 * @author henryoyuela
 * 
 */
public class CloseApplicationOnDismiss implements OnDismissListener {
	
	static final String TAG = "CloseApp";
	
	// Reference of the activity to be able to close out the application
	private final Activity activity;

	public CloseApplicationOnDismiss(final Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "Closing Application...");
		}

		// close application
		this.activity.finish();

	}
}

