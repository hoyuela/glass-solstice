package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * An interface that defines method signatures used by the GenericAsyncCallback<> class for communicating 
 * NetworkServiceCall<> events with a listener class that implements the method signatures. 
 * 
 * @author henryoyuela
 *
 */
public interface GenericCallbackListener {
	/**
	 * Used to determine when the listener should be called based by priority. All listeners are sorted
	 * in a priority queue and are executed based on the priority returned by this function.
	 *  
	 * @return Method signature used by a class definition to communicate its priority to the GenericAsyncCallback
	 */
	CallbackPriority getCallbackPriority();
	
	public static enum CallbackPriority {
		FIRST,
		MIDDLE,
		LAST
	}
	
	public static interface StartListener extends GenericCallbackListener {
		void start(final NetworkServiceCall<?> sender);
	}
	
	public static interface CompletionListener extends GenericCallbackListener {
		void complete(final NetworkServiceCall<?> sender, final Object result);
	}
	
	public static interface SuccessListener<V> extends GenericCallbackListener {
		void success(final NetworkServiceCall<?> sender, final V value);
	}
	
	public static interface ExceptionFailureHandler extends GenericCallbackListener {
		/**
		 * @param executionException Reference to the exception that was thrown
		 * @param networkServiceCall Reference to the network service call where the exception occurred
		 * 
		 * @return Return true if failure was handles, false otherwise
		 */
		boolean handleFailure(final NetworkServiceCall<?> sender, final Throwable executionException);
	}
	
	public static interface ErrorResponseHandler extends GenericCallbackListener {
		boolean handleFailure(final NetworkServiceCall<?> sender, ErrorResponse<?> errorResponse);
	}
	
}
