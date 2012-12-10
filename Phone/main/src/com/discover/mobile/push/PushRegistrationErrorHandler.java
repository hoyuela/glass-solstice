package com.discover.mobile.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * This is the error handler for when the app tries to let Discover's server if the 
 * user accepts or declines the use of push notifications.  If the call errors out,
 * then we do not want the app to crash, so we send it to the account home so that 
 * the app will still be able function normally.
 * 
 * @author jthornton
 *
 */
public class PushRegistrationErrorHandler implements ErrorResponseHandler{
	
	/**Tag representing the name of the class*/
	private static final String TAG = PushRegistrationErrorHandler.class.getSimpleName();
	
	/**Activity context*/
	private Context context;

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
		Log.e(TAG, "Error registering the user for push.  Error code: " + errorResponse.getHttpStatusCode());
		final Intent intent = new Intent(context, NavigationRootActivity.class);
		context.startActivity(intent);
		context = null;
		return true;
	}

}
