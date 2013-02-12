package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkNotNull;

import com.discover.mobile.common.callback.AsyncCallback;

/**
 * Simple reference handler that can be used on most calls
 * @author jthornton
 *
 * @param <V> - class to map the detial to
 */
public class SimpleReferenceHandler <V> extends TypedReferenceHandler<V> {
	
	/**Callback associated with this handler*/
	private final AsyncCallback<V> callback;
	
	/**
	 * Constructor for the class
	 * @param callback - callback associated with this handler
	 */
	public SimpleReferenceHandler(final AsyncCallback<V> callback) {
		checkNotNull(callback, "callback cannot be null");
		this.callback = callback;
	}
	
	/**
	 * Get the callback
	 * @return the callback
	 */
	@Override
	protected AsyncCallback<V> getCallback() {
		return callback;
	}
	
}