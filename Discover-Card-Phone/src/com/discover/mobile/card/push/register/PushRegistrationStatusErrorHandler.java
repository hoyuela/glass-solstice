package com.discover.mobile.card.push.register;

import android.app.Activity;
import android.content.Intent;

import com.discover.mobile.card.error.CardBaseErrorResponseHandler;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * This is the error handler for when the app tries to get information from
 * Discover's server about the vendor id (xid - XTIFY) and the current user. If
 * the call errors out, then we do not want the app to crash, so we send it to
 * the account home so that the app will still be able function normally.
 * 
 * @author jthornton
 * 
 */
public class PushRegistrationStatusErrorHandler extends CardBaseErrorResponseHandler  {

	Activity activity;
	
	/**
	 * Constructor for the class
	 * 
	 * @param loginActivity
	 *            - activity context
	 */
	public PushRegistrationStatusErrorHandler(final BaseFragmentActivity loginActivity) {
		super(loginActivity);
		this.activity=loginActivity;
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
	protected boolean handleJsonErrorCode(final JsonMessageErrorResponse messageErrorResponse) {
		
		//FIXME are we sure we want to suppress this error code
		final Intent intent = new Intent(activity, CardNavigationRootActivity.class);
		activity.startActivity(intent);
		activity = null;
		return true;
	}

}