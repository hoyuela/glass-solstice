package com.discover.mobile.common.net;

public interface AsyncCallback<V> {
	
	void success(V value);
	
	void failure(Throwable executionException);
	void failure(GenericErrorResponse errorResponse);

	// TODO
	
}
