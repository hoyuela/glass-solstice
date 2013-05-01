package com.discover.mobile.common.error;

import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * Class is a generic exception handler for NetworkServiceCall<>. There are three cases supported by 
 * this ExceptionHandler. If there is an exception during a PreAuthCheckCall then the application is 
 * sent to the Login Page, if there is an exception during a LogOutCall then it is handled as if logout 
 * was successful, any other exception displays the catch-all modal notifying the user that their request 
 * could not be processed. Exception can occur for a NetworkServiceCall<> when sending a request or when
 * processing a response.
 * 
 * @author henryoyuela
 *
 */
public class BaseExceptionFailureHandler implements ExceptionFailureHandler {
	public BaseExceptionFailureHandler(){

	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		/*Should be the last Exception Handler processed to allow other exception handler to be handled*/
		return CallbackPriority.LAST;
	}

	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender, final Throwable arg0) {	
		//FIXME push this down into PreAuthCheckCall override error handler
		
		return true;
	}

}
