package com.discover.mobile.error;

import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;

/**
 * 1) include context here so that this class can invoke the error modal dialog.
 * 2) request from Jon the text that needs to go in here for when the user lost connection.
 * 3) link this into base builder so that it is by default the exception failure handler. we 
 *    should not need more than one implementation here for 99% of scenarios. 
 * 4) invoke the error dialog for "generic error" for any runtime or other error. invoke the 
 *    "you've lost connection" error dialog for the checkConnection issue.
 *    make this listener the default in the base builder please.
 * 
 * @author henryoyuela
 *
 */
public class BaseExceptionFailureHandler implements ExceptionFailureHandler {
	public BaseExceptionFailureHandler(){

	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public boolean handleFailure(Throwable arg0) {
		//TODO: Handle Socket Timeout Exception
		//TODO: Handle Airplane Mode
		//TOOD: Handle MissingTokenException
		//TODO: Handle Malformed Message Exception
		//TODO: Handle IOException
		
		//TODO: If exception during Pre-Auth then show error modal and exit app
		//TODO: If exception during Login show error on screen
		//TODO: If exception during Strong auth show error on screen
		//TODO: If exception during Forget Password
		//TODO: If exception during During Logged in and in Fragment display modal
		
		
		ErrorHandlerFactory.getInstance().handleGenericError(0);
		return true;
	}

}
