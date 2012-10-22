package com.discover.mobile.common.net;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

// Nothing in this class should happen outside of the UI thread
public final class WeakReferenceHandler<V> extends Handler {
	
	private static final String TAG = WeakReferenceHandler.class.getSimpleName();
	
	private final WeakReference<AsyncCallback<V>> weakRef;
	
	public WeakReferenceHandler(final AsyncCallback<V> callback) {
		weakRef = new WeakReference<AsyncCallback<V>>(callback);
	}
	
	@Override
	public void handleMessage(final Message message) {
		final AsyncCallback<V> callback = weakRef.get();
		if(callback == null) {
			Log.d(TAG, "Weak reference released, dropping callback");
			return;
		}
		
		if(message.what == NetworkServiceCall.STATUS_SUCCESS)
			handleSuccess(message, callback);
		else
			throw new UnsupportedOperationException("Error status not implemented yet"); // TODO
	}
	
	private void handleSuccess(final Message message, final AsyncCallback<V> callback) {
		@SuppressWarnings("unchecked")
		final V value = (V) message.obj;
		callback.success(value);
	}
	
}
