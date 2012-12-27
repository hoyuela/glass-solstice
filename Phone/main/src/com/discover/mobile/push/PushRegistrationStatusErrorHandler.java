package com.discover.mobile.push;

import android.content.Intent;

import com.discover.mobile.RoboSlidingFragmentActivity;
import com.discover.mobile.login.BaseErrorResponseHandler;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * This is the error handler for when the app tries to get information from
 * Discover's server about the vendor id (xid - XTIFY) and the current user. If
 * the call errors out, then we do not want the app to crash, so we send it to
 * the account home so that the app will still be able function normally.
 * 
 * @author jthornton
 * 
 */
public class PushRegistrationStatusErrorHandler extends BaseErrorResponseHandler  {

	/**
	 * Constructor for the class
	 * 
	 * @param activity
	 *            - activity context
	 */
	public PushRegistrationStatusErrorHandler(RoboSlidingFragmentActivity activity) {
		super(activity);
	}

	/**
	 * Set the priority level of the error handler
	 * 
	 * @return CallbackPriority - the priority of the callback
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the error when the call fails with the server. Since the app just
	 * needs to continue log the error and send the user to the next screen.
	 * 
	 * @return true if the error has been handled
	 */
	@Override
	protected boolean handleJsonErrorCode(com.discover.mobile.common.net.json.JsonMessageErrorResponse messageErrorResponse) {
		//FIXME are we sure we want to suppress this error code
		final Intent intent = new Intent(activity, NavigationRootActivity.class);
		activity.startActivity(intent);
		activity = null;
		return true;
	}

}