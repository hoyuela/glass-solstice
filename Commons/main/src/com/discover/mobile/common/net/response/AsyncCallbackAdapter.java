package com.discover.mobile.common.net.response;

import android.util.Log;

import com.discover.mobile.common.net.json.MessageErrorResponse;

/**
 * Provides default, no-op implementations of the {@link AsyncCallback} methods to make implementing easier.
 * 
 * @param <V>
 */
public abstract class AsyncCallbackAdapter<V> implements AsyncCallback<V> {
	
	@Override public void success(final V value) { }
	@Override public void failure(final Throwable executionException) { }
	
	public void errorResponse(@SuppressWarnings("unused") final ErrorResponse errorResponse) { }
	public void messageErrorResponse(@SuppressWarnings("unused") final MessageErrorResponse messageErrorResponse) { }
	
	@Override
	public final void failure(final ErrorResponse errorResponse) {
		if(errorResponse instanceof MessageErrorResponse) {
			Log.e("AHHHH!", "This is a message error response...");
			messageErrorResponse((MessageErrorResponse)errorResponse);
		}
		
		// Called every time for implementors who don't care about MessageErrorResponse-specific stuff
		errorResponse(errorResponse);
	}
	
}
