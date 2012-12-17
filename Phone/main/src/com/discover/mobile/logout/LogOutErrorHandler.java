package com.discover.mobile.logout;

import com.discover.mobile.RoboSlidingFragmentActivity;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * The error handler for the log out call
 * 
 * @author jthornton
 *
 */
public class LogOutErrorHandler implements ErrorResponseHandler{

	/**Activity running*/
	private RoboSlidingFragmentActivity activity;
	
	/**
	 * Constructor for the class
	 * @param activity - the current activity running
	 */
	public LogOutErrorHandler(final RoboSlidingFragmentActivity activity){
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
	 * Handle the call failing
	 * @param errorResponse - error object
	 */
	@Override
	public boolean handleFailure(final ErrorResponse<?> errorResponse) {
		//TODO: Handle this appropriately, this may change in the future
		//Maybe even show an error alert
		activity.finish();
		return true;
	}

}
