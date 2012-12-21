package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkNotNull;

import com.discover.mobile.common.callback.AsyncCallback;

public class PostPushPreferencesReferenceHandler   <V> extends TypedReferenceHandler<V> {
	
	private final AsyncCallback<V> callback;
	
	public PostPushPreferencesReferenceHandler(final AsyncCallback<V> callback) {
		checkNotNull(callback, "callback cannot be null");
		
		this.callback = callback;
	}
	
	@Override
	AsyncCallback<V> getCallback() {
		return callback;
	}
	
}