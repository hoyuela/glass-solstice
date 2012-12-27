package com.discover.mobile.push.manage;

import android.util.Log;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * Error Handler for the posting of the preferences after the user had decided to change their current settings. 
 * @author jthornton
 *
 */
public class PushPrefsErrorHandler implements ErrorResponseHandler{

	/**Tag labeling the class for errors*/
	private static final String TAG = PushPrefsErrorHandler.class.getSimpleName();

	/**
	 * Constructor for the class
	 */
	public PushPrefsErrorHandler(){

	}
	
	/**
	 * Set the priority of the handler
	 * @return the priority of the handler
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
		//TODO: Handle this appropriately
		return true;
	}

}
