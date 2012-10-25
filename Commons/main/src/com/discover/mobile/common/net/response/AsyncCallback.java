package com.discover.mobile.common.net.response;

public interface AsyncCallback<V> {
	
	void success(V value);

	void failure(ErrorResponse errorResponse);
	void failure(Throwable executionException);
	
}
