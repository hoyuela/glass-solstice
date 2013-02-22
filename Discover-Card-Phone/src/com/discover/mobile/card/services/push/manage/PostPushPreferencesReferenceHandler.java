package com.discover.mobile.card.services.push.manage;

import static com.google.common.base.Preconditions.checkNotNull;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.TypedReferenceHandler;

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
	protected AsyncCallback<V> getCallback() {
		return callback;
	}
	
}