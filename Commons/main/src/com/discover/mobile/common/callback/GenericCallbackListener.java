package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.response.ErrorResponse;

public interface GenericCallbackListener {
	
	Order getOrder();
	
	public static enum Order {
		FIRST,
		MIDDLE,
		LAST
	}
	
	public static interface PreSubmitListener extends GenericCallbackListener {
		void preSubmit();
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
