package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

public interface AsyncCallback<V> {
	
	void success(V value);
	
	void failure(ErrorResponse<?> errorResponse);
	
	/**
	 * 
	 * @param executionException Reference to the exception that was thrown
	 * @param networkServiceCall Reference to the network service call where the exception occurred
	 */
	void failure(Throwable executionException, final NetworkServiceCall<V> networkServiceCall );
	
	/**
	 * Called when the {@link NetworkServiceCall} finishes, no matter what the result was. This will be called before
	 * {@link #success(Object)}, {@link #failure(Throwable)} or {@link #failure(ErrorResponse)} is called. If an
	 * {@code Exception} is thrown during the execution of this method it will prevent the status-specific method from
	 * being called.
	 * 
	 * @param result The result of the call before it is passed to the more specific, status-related methods
	 */
	void complete(Object result);
	
}
