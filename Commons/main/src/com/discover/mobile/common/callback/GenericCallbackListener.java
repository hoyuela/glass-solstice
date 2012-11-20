package com.discover.mobile.common.callback;

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
		boolean handleFailure(Throwable executionException);
	}
	
	public static interface ErrorResponseHandler extends GenericCallbackListener {
		boolean handleFailure(ErrorResponse<?> errorResponse);
	}
	
}
