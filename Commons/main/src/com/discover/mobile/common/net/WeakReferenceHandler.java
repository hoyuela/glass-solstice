package com.discover.mobile.common.net;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public final class WeakReferenceHandler<V> extends Handler {
	
	private final WeakReference<AsyncCallback<V>> weakRef;
	
	protected WeakReferenceHandler(final AsyncCallback<V> callback) {
		weakRef = new WeakReference<AsyncCallback<V>>(callback);
	}
	
	@Override
	public void handleMessage(final Message message) {
		final AsyncCallback<V> callback = weakRef.get();
		if(callback == null)
			return;
		
		@SuppressWarnings("unchecked")
		final V value = (V) message.obj;
		callback.callback(value);
	}
	
}
