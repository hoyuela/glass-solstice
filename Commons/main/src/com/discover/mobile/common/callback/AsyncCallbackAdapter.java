package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.MessageErrorResponse;

/**
 * Provides default, no-op implementations of the {@link AsyncCallback} and {@link ExtendedAsyncCallback} methods to
 * make implementing easier.
 * 
 * @param <V>
 */
public class AsyncCallbackAdapter<V> implements AsyncCallback<V> {
	
	@Override
	public void complete(final Object result) {
		// Intentional no-op
	}
	
	@Override
	public void success(final V value) {
		// Intentional no-op
		
		// TODO consider forcing some implementation of success (why have any calls that we don't care about a 200?)
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
	public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
		return false;
	}
	
	@Override
	public final void failure(final ErrorResponse errorResponse) {
		boolean handled;
		if(errorResponse instanceof MessageErrorResponse) {
			handled = handleMessageErrorResponse((MessageErrorResponse)errorResponse);
			if(handled)
				return;
		}
		
		handled = handleErrorResponse(errorResponse);
		if(handled)
			return;
		
		throw new UnsupportedOperationException("Unhandled errorResponse: " + errorResponse);
	}
	
}
