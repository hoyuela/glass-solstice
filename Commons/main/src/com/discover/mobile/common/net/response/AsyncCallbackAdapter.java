package com.discover.mobile.common.net.response;

import com.discover.mobile.common.net.json.MessageErrorResponse;

public abstract class AsyncCallbackAdapter<V> implements AsyncCallback<V> {
	
	// TODO decide if these should be kept (makes them optional)
//	@Override public void success(final V value) { }
//	@Override public void failure(final Throwable executionException) { }
	
	// TODO keep abstract vs. defaulted in sync with success() and failure() above
//	@SuppressWarnings("unused") public void errorResponse(final ErrorResponse errorResponse) { }
	public abstract void errorResponse(final ErrorResponse errorResponse);
	public void messageErrorResponse(@SuppressWarnings("unused") final MessageErrorResponse messageErrorResponse) { }
	
	@Override
	public final void failure(final ErrorResponse errorResponse) {
		if(errorResponse instanceof MessageErrorResponse)
			messageErrorResponse((MessageErrorResponse)errorResponse);
		else
			errorResponse(errorResponse);
	}
	
}
