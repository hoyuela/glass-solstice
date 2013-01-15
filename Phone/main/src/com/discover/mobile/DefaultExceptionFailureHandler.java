package com.discover.mobile;

import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;

/**
 * 
 * 
 * @author henryoyuela
 *
 */
public class DefaultExceptionFailureHandler implements ExceptionFailureHandler {
	public DefaultExceptionFailureHandler(){

	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public boolean handleFailure(Throwable arg0) {
		//TODO:
		return true;
	}

}
