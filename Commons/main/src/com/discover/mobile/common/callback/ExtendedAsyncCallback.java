package com.discover.mobile.common.callback;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

public interface ExtendedAsyncCallback<V> extends AsyncCallback<V> {
	
	/**
	 * Called on the UI thread immediately before the associated {@link NetworkServiceCall} is scheduled for execution
	 * on a background thread.
	 */
	void preSubmit();
	
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
