package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

public interface GenericCallbackListener {
	
	CallbackPriority getCallbackPriority();
	
	public static enum CallbackPriority {
		FIRST,
		MIDDLE,
		LAST
	}
	
	public static interface CompletionListener extends GenericCallbackListener {
		void complete(Object result);
	}
	
	public static interface SuccessListener<V> extends GenericCallbackListener {
		void success(V value);
	}
	
	public static interface ExceptionFailureHandler extends GenericCallbackListener {
		/**
		 * @param executionException Reference to the exception that was thrown
		 * @param networkServiceCall Reference to the network service call where the exception occurred
		 * 
		 * @return Return true if failure was handles, false otherwise
		 */
		boolean handleFailure(Throwable executionException, final NetworkServiceCall<?> networkServiceCall);
	}
	
	public static interface ErrorResponseHandler extends GenericCallbackListener {
		boolean handleFailure(ErrorResponse<?> errorResponse);
	}
	
}
