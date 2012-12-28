package com.discover.mobile.push.history;

import android.util.Log;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.push.manage.GetPushPrefsErrorResponseHandler;

/**
 * Error handler for when the application contacts the server letting it know a notification was read
 * @author jthornton
 *
 */
public class ReadNotificationErrorHandler implements ErrorResponseHandler {

	/**Tag labeling the class for errors*/
	private static final String TAG = GetPushPrefsErrorResponseHandler.class.getSimpleName();
	
	/**
	 * Get the callback priority of the error handler
	 * @return the callback priority of the error handler
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the error response
	 * @error - error response from the server
	 * @return true if the error was handled
	 */
	@Override
	public boolean handleFailure(final ErrorResponse<?> error) {
		Log.e(TAG, Integer.toString(error.getHttpStatusCode()));
		//TODO:  Handle this correctly
		return true;
	}

}

