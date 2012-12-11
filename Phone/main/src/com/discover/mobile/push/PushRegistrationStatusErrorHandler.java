package com.discover.mobile.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * This is the error handler for when the app tries to get information from Discover's server
 * about the vendor id (xid - XTIFY) and the current user.  If the call errors out,
 * then we do not want the app to crash, so we send it to the account home so that 
 * the app will still be able function normally.
 * 
 * @author jthornton
 *
 */
public class PushRegistrationStatusErrorHandler implements ErrorResponseHandler{
	
	/**Tag representing the name of the class*/
	private static final String TAG = PushRegistrationStatusErrorHandler.class.getSimpleName();
	
	/**Activity context*/
	private Context context;
	
	/**
	 * Constructor for the class
	 * @param context - activity context
	 */
	public PushRegistrationStatusErrorHandler(final Context context){
		this.context = context;
	}

	/**
	 * Set the priority level of the error handler
	 * @return CallbackPriority - the priority of the callback
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}
	
	/**
	 * Handle the error when the call fails with the server.  Since the app just needs to continue log the
	 * error and send the user to the next screen.
	 * @return true if the error has been handled
	 */
	@Override
	public boolean handleFailure(final ErrorResponse<?> errorResponse) {
		Log.e(TAG, "Error getting registration status.  Error code: " + errorResponse.getHttpStatusCode());
		final Intent intent = new Intent(context, NavigationRootActivity.class);
		context.startActivity(intent);
		context = null;
		return true;
	}
}