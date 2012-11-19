package com.discover.mobile.common.net.response;

import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * Provides default, no-op implementations of the {@link AsyncCallback} methods to make implementing easier.
 * 
 * @param <V>
 */
public abstract class AsyncCallbackAdapter<V> implements AsyncCallback<V> {
	
	@Override
	public void success(final V value) {
		// TODO consider forcing some implementation of success (why have any calls that we don't care about a 200?)
		// Intentional no-op
	}
	
	@Override
	public void failure(final Throwable executionException) {
		throw new UnsupportedOperationException("Unhandled execution exception", executionException);
	}
	
	@SuppressWarnings("unused")
	public boolean handleErrorResponse(final ErrorResponse errorResponse) {
		return false;
	}
	
	@SuppressWarnings("unused")
	public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
		return false;
	}
	
	@Override
	public final void failure(final ErrorResponse errorResponse) {
		boolean handled;
		if(errorResponse instanceof JsonMessageErrorResponse) {
			handled = handleMessageErrorResponse((JsonMessageErrorResponse)errorResponse);
			if(handled)
				return;
		}
		
		handled = handleErrorResponse(errorResponse);
		if(handled)
			return;
		
		throw new UnsupportedOperationException("Unhandled errorResponse: " + errorResponse);
	}
	
}
