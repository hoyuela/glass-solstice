package com.discover.mobile.logout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.login.LoginActivity;

/**
 * Success listener for the log out call.
 * 
 * @author jthornton
 *
 */
public class LogOutSuccessListener implements SuccessListener<Object>{
	
	/**Activity running*/
	private Activity activity;
	
	/**
	 * Constructor for the class
	 * @param activity - the current activity running
	 */
	public LogOutSuccessListener(final Activity activity){
		this.activity = activity;
	}

	/**
	 * Get the priority of the call
	 * @ return the priority of the call
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the success of the call.  In this case send the user to the login screen with a 
	 * special intent to the logout message is shown.
	 * @param successObject - object retrieved from the success call
	 */
	@Override
	public void success(final Object successObject) {
		//TODO: Handle this appropriately, this may change in the future
		final Intent intent = new Intent(activity, LoginActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.finish();
	}
}
