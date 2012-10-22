package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkNotNull;

public class StrongReferenceHandler<V> extends TypedReferenceHandler<V> {
	
	private final AsyncCallback<V> callback;
	
	public StrongReferenceHandler(final AsyncCallback<V> callback) {
		checkNotNull(callback, "callback cannot be null");
		
		this.callback = callback;
	}
	
	@Override
	AsyncCallback<V> getCallback() {
		return callback;
	}
	
}
