package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * An interface that defines method signatures used by the NetworkServiceCall<> class for communicating events 
 * with a listener class that implements the method signatures.  
 * 
 * @author henryoyuela
 *
 * @param <V>
 */
public interface AsyncCallback<V> {
	/**
	 * Callback invoked when a NetworkServiceCall<> starts
	 * @param sender Reference to calling NetworkServiceCall<>
	 */
	void start(final NetworkServiceCall<?> sender);
	/**
	 * Callback invoked when a NetworkServiceCall<> receives a 200 OK response
	 * 
	 * @param sender Reference to calling NetworkServiceCall<>
	 * @param value Contains any content that was found in the body of the 200 OK response
	 */
	void success(final NetworkServiceCall<?> sender, V value);
	/**
	 * Callback invoked when a NetworkServiceCall<> receives an HTTP error response. 
	 * 
	 * @param sender Reference to calling NetworkServiceCall<>
	 * @param errorResponse Contains any content that was provided in the body of the HTTP error response.
	 */
	void failure(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse);
	
	/**
	 * Callback invoked when an exception occurs during sending a request or processing a response for a
	 * NetworkServiceCall<>. 
	 * 
	 * @param executionException Reference to the exception that was thrown
	 * @param networkServiceCall Reference to the network service call where the exception occurred
	 */
	void failure(final NetworkServiceCall<?> networkServiceCall, final Throwable executionException );
	
	/**
	 * Called when the {@link NetworkServiceCall} finishes, no matter what the result was. This will be called before
	 * {@link #success(Object)}, {@link #failure(Throwable)} or {@link #failure(ErrorResponse)} is called. If an
	 * {@code Exception} is thrown during the execution of this method it will prevent the status-specific method from
	 * being called.
	 * 
	 * @param sender Reference to calling NetworkServiceCall<>
	 * @param result The result of the call before it is passed to the more specific, status-related methods
	 */
	void complete(final NetworkServiceCall<?> sender, final Object result);

	
}
