package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkNotNull;

import com.discover.mobile.common.callback.AsyncCallback;

/**
 * Reference hanadler for the posting of the push preferences
 * @author jthornton
 *
 * @param <V>
 */
public class PostPushPreferencesReferenceHandler   <V> extends TypedReferenceHandler<V> {
	
	/**Callback to return*/
	private final AsyncCallback<V> callback;
	
	/**
	 * Constructor for the class
	 * @param callback - callback to be used
	 */
	public PostPushPreferencesReferenceHandler(final AsyncCallback<V> callback) {
		checkNotNull(callback, "callback cannot be null");
		
		this.callback = callback;
	}
	
	/**
	 * Get the callback
	 * @return return the call back
	 */
	@Override
	AsyncCallback<V> getCallback() {
		return callback;
	}
	
}