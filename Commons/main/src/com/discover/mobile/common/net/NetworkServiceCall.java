package com.discover.mobile.common.net;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

public abstract class NetworkServiceCall<R> {
	
	private final WeakReference<Context> contextRef;
	private final WeakReferenceHandler<R> handler;
	
	protected NetworkServiceCall(final Context context, final AsyncCallback<R> callback) {
		contextRef = new WeakReference<Context>(context);

		checkCurrentThreadHasLooper();
		handler = new WeakReferenceHandler<R>(callback);
	}
	
	/**
	 * Submit the service call for asynchronous execution and call the callback when completed.
	 */
	public final void submit() {
		NetworkTrafficExecutorHolder.networkTrafficExecutor.submit(new Runnable() {
			@Override
			public void run() {
				executeRequest();
			}
		});
	}
	
	protected abstract R parseResponse(final InputStream responseStream);
	
	private static void checkCurrentThreadHasLooper() {
		if(Looper.myLooper() == null)
			throw new AssertionError("Current thread does not have an associated Looper, callbacks can't be scheduled");
	}
	
	// TODO check before firing network request
	public static boolean isActiveNetworkConnected(final Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	private void executeRequest() {
		// TODO execute network request
		// TODO send message
		
		// TODO
	}
	
}
