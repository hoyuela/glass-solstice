package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.response.ErrorResponse;

public interface AsyncCallback<V> {
	
	void success(V value);
	
	void failure(ErrorResponse errorResponse);
	void failure(Throwable executionException);
	
}
