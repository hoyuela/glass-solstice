package com.discover.mobile.common.net;

import static com.discover.mobile.common.net.NetworkServiceCall.RESULT_EXCEPTION;
import static com.discover.mobile.common.net.NetworkServiceCall.RESULT_SUCCESS;


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
		
		switch(message.what) {
			case RESULT_SUCCESS:
				handleSuccess(message, callback);
				break;
				
			case RESULT_EXCEPTION:
				handleException(message, callback);
				break;
				
			// TODO
			
			default:
				throw new AssertionError("Unexpected result status: " + message.what);
		}
	}
	
	void handleSuccess(final Message message, final AsyncCallback<V> callback) {
		@SuppressWarnings("unchecked")
		final V value = (V) message.obj;
		callback.success(value);
	}
	
	void handleException(final Message message, final AsyncCallback<V> callback) {
		final Throwable exception = (Throwable) message.obj;
		callback.failure(exception);
	}
	
}
