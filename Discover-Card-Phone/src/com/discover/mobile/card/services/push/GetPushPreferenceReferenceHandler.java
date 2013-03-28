package com.discover.mobile.card.services.push;

import static com.google.common.base.Preconditions.checkNotNull;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class GetPushPreferenceReferenceHandler  <V> extends TypedReferenceHandler<V> {
	
	private final AsyncCallback<V> callback;
	
	public GetPushPreferenceReferenceHandler(final AsyncCallback<V> callback) {
		checkNotNull(callback, "callback cannot be null");
		
		this.callback = callback;
	}
	
	protected AsyncCallback<V> getCallback() {
		return callback;
	}
	
}