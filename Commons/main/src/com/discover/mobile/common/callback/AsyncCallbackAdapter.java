package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * Provides default, no-op implementations of the {@link AsyncCallback} methods to make implementing easier.
 * 
 * @param <V>
 * @deprecated Use {@link GenericAsyncCallback}s instead, with a {@link GenericCallbackListener} for more granular
 * handling.
 */
@Deprecated
public class AsyncCallbackAdapter<V> implements AsyncCallback<V> {
	
	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object result) {
		// Intentional no-op
	}
	
	@Override
	public void success(final NetworkServiceCall<?> sender, final V value) {
		// Intentional no-op
		
		// TODO consider forcing some implementation of success (why have any calls that we don't care about a 200?)
	}
	
	/**
	 * 
	 * @param executionException Reference to the exception that was thrown
	 * @param networkServiceCall Reference to the network service call where the exception occurred
	 */
	@Override
	public void failure(final NetworkServiceCall<?> networkServiceCall, final Throwable executionException) {
		throw new UnsupportedOperationException("Unhandled execution exception", executionException);
	}
	
	public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
		return false;
	}
	
	public boolean handleMessageErrorResponse(final NetworkServiceCall<?> sender, final JsonMessageErrorResponse messageErrorResponse) {
		return false;
	}
	
	@Override
	public final void failure(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
		boolean handled;
		if(errorResponse instanceof JsonMessageErrorResponse) {
			handled = handleMessageErrorResponse(sender, (JsonMessageErrorResponse)errorResponse);
			if(handled)
				return;
		}
		
		handled = handleErrorResponse(sender, errorResponse);
		if(handled)
			return;
		
		throw new UnsupportedOperationException("Unhandled errorResponse: " + errorResponse);
	}

	@Override
	public void start(final NetworkServiceCall<?> sender) {
		// TODO Auto-generated method stub
		
	}
	
}
