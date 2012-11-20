package com.discover.mobile.common.net;

import java.lang.ref.WeakReference;

import com.discover.mobile.common.callback.AsyncCallback;

// Nothing in this class should happen outside of the UI thread
public final class WeakReferenceHandler<V> extends TypedReferenceHandler<V> {
	
	private final WeakReference<AsyncCallback<V>> weakRef;
	
	public WeakReferenceHandler(final AsyncCallback<V> callback) {
		weakRef = new WeakReference<AsyncCallback<V>>(callback);
	}
	
	@Override
	AsyncCallback<V> getCallback() {
		return weakRef.get();
	}
	
}
