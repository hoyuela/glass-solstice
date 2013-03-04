package com.discover.mobile.common.net;

import static com.discover.mobile.common.net.NetworkServiceCall.RESULT_EXCEPTION;
import static com.discover.mobile.common.net.NetworkServiceCall.RESULT_PARSED_ERROR;
import static com.discover.mobile.common.net.NetworkServiceCall.RESULT_SUCCESS;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * A Handler sub-class that allows a NetworkServiceCall to communicate events in asynchronously with its
 * associated listener via the use of Android's MessageQueue. This class serves as a dispatcher of 
 * incoming NetworkServiceCall events.
 * 
 * @author henryoyuela
 *
 * @param <V>
 */
public abstract class TypedReferenceHandler<V> extends Handler {
	/**
	 * Used to send logs to Android logcat
	 */
	private final String TAG = getClass().getSimpleName();
	/**
	 * Reference to NetworkServiceCall<> that is the owner of this class
	 */
	protected NetworkServiceCall<V> networkServiceCall;
	/**
	 * @return Returns reference to associated AsynCallback which will handle incoming events from NetworkServiceCall
	 */
	protected abstract AsyncCallback<V> getCallback();
	/**
	 * Method used to process incoming message objects sent from a NetworkServiceCall<> and dispatch them
	 * to their respective listener.
	 */
	@Override
	public void handleMessage(final Message message) {
		final AsyncCallback<V> callback = getCallback();
		if(callback == null) {
			Log.d(TAG, "Callback was null, dropping callback");
			return;
		}

		callback.complete(networkServiceCall, message.obj);
		
		switch(message.what) {			
			case RESULT_SUCCESS:
				handleSuccess(message, callback);
				break;
				
			case RESULT_EXCEPTION:
				handleException(message, callback);
				break;
				
			case RESULT_PARSED_ERROR:
				handleErrorResponse(message, callback);
				break;
			
			default:
				throw new AssertionError("Unexpected result status: " + message.what);
		}
	}
	/**
	 * Used to set the NetworkServiceCall that will be used as the sender of events
	 * 
	 * @param networkServiceCall Reference to NetworkServiceCall<> that will be used as the sender 
	 * in all callback
	 */
	void setNetworkServiceCall(final NetworkServiceCall<V> networkServiceCall) {
		this.networkServiceCall = networkServiceCall;
	}
	/**
	 * 
	 * @param message Reference to message object sent from NetworkServiceCall
	 * @param callback Reference to AsyncCallback whose start method should be called
	 */
	void handleStart(final Message message, final AsyncCallback<V> callback) {
		callback.start(networkServiceCall);
	}
	/**
	 * 
	 * @param message Reference to message object sent from NetworkServiceCall
	 * @param callback Reference to AsyncCallback whose success method should be called
	 */
	void handleSuccess(final Message message, final AsyncCallback<V> callback) {
		@SuppressWarnings("unchecked")
		final V value = (V) message.obj;
		callback.success(networkServiceCall, value);
	}
	/**
	 * 
	 * @param message Reference to message object sent from NetworkServiceCall
	 * @param callback Reference to AsyncCallback whose failure method should be called
	 */
	@SuppressWarnings("static-method")
	void handleException(final Message message, final AsyncCallback<V> callback) {
		final Throwable exception = (Throwable) message.obj;
		callback.failure(networkServiceCall, exception);
	}
	/**
	 * 
	 * @param message Reference to message object sent from NetworkServiceCall
	 * @param callback Reference to AsyncCallback whose failure method should be called
	 */
	@SuppressWarnings("static-method")
	void handleErrorResponse(final Message message, final AsyncCallback<V> callback) {
		final ErrorResponse errorResponse = (ErrorResponse) message.obj;
		callback.failure(networkServiceCall, errorResponse);
	}
	
}
