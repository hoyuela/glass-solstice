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
	
	public static interface ExceptionalFailureListener extends GenericCallbackListener {
		void failure(Throwable executionException);
	}
	
	public static interface ErrorResponseFailureListener extends GenericCallbackListener {
		void failure(ErrorResponse errorResponse);
	}
	
}
