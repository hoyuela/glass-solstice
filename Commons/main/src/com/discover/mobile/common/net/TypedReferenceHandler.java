package com.discover.mobile.common.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class TypedReferenceHandler<V> extends Handler {
	
	private final String TAG = getClass().getSimpleName();
	
	abstract AsyncCallback<V> getCallback();
	
	@Override
	public void handleMessage(final Message message) {
		final AsyncCallback<V> callback = getCallback();
		if(callback == null) {
			Log.d(TAG, "Callback was null, dropping callback");
			return;
		}
		
		if(message.what == NetworkServiceCall.STATUS_SUCCESS)
			handleSuccess(message, callback);
		else
			throw new UnsupportedOperationException("Error status not implemented yet"); // TODO
	}
	
	void handleSuccess(final Message message, final AsyncCallback<V> callback) {
		@SuppressWarnings("unchecked")
		final V value = (V) message.obj;
		callback.success(value);
	}
	
}
