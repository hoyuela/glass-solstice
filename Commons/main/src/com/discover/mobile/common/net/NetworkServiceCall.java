package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.common.base.Strings;

public abstract class NetworkServiceCall<R> {
	
	static final String TAG = NetworkServiceCall.class.getSimpleName();
	
	// TODO externalize
	public static final String BASE_URL = "https://www.discovercard.com";
	
	static final int STATUS_SUCCESS = 0;
	
	private final ServiceCallParams params;
	
	private Context context;
	
	protected NetworkServiceCall(final Context context, final ServiceCallParams params) {
		checkPreconditions(context, params);
		
		this.context = context;
		
		this.params = params;
	}
	
	private static void checkPreconditions(final Context context, final ServiceCallParams params) {
		checkNotNull(context, "context cannot be null");
		checkNotNull(params.method, "params.method cannot be null");
		checkArgument(!Strings.isNullOrEmpty(params.path), "params.path should never be empty");
		checkArgument(params.readTimeoutSeconds > 0, "invalid params.readTimeoutSeconds: " + params.readTimeoutSeconds);
		checkCurrentThreadHasLooper();
	}
	
	/**
	 * Submit the service call for asynchronous execution and call the callback when completed.
	 */
	public final void submit() {
		// TEMP
		Log.e(TAG, "submit()");
		
		try {
			if(!isActiveNetworkConnected(context)) {
				Log.d(TAG, "No network connection available, dropping call");
				return;
			}
		} finally {
			context = null; // allow garbage collection no matter what the result is
		}
		
		NetworkTrafficExecutorHolder.networkTrafficExecutor.submit(new Runnable() {
			@Override
			public void run() {
				// TEMP
				Log.e(TAG, "run()");
				
				try {
					executeRequest();
				} catch(final Exception e) {
					// TEMP
					Log.w(TAG, "caught exception", e);
					e.printStackTrace();
					
					// TODO send error result
				}
			}
		});
	}

	protected abstract Handler getHandler();
	/**
	 * Executed in a background thread, needs to be thread-safe.
	 * 
	 * @param responseStream
	 * @param statusCode
	 * @return The parsed result from the response
	 */
	protected abstract R parseResponse(final InputStream responseStream, int statusCode);
	
	// Executes in the background thread, does actual connection and delegates parsing to subclass
	private void executeRequest() throws IOException {
		final HttpURLConnection conn = createConnection();
		prepareConnection(conn);
		
		// TODO figure out if conn.connect() and prepareConnection() need to be called within the try block
		final R result;
		conn.connect();
		try {
			final int statusCode = conn.getResponseCode();
			final InputStream in = conn.getInputStream();
			
			result = parseResponse(in, statusCode);
			
			in.close();
		} finally {
			conn.disconnect();
		}
		
		sendSuccessfulResultToHandler(result);
		
		// TODO
	}
	
	private HttpURLConnection createConnection() throws IOException {
		final URL fullUrl = getFullUrl();
		return (HttpURLConnection) fullUrl.openConnection();
	}
	
	private void prepareConnection(final HttpURLConnection conn) {
		setDefaultHeaders(conn);
		setCustomHeaders(conn);
		
		conn.setReadTimeout(params.readTimeoutSeconds * 1000);
	}
	
	private URL getFullUrl() throws IOException {
		return new URL(BASE_URL + params.path);
	}
	
	private static void setDefaultHeaders(final HttpURLConnection conn) {
		conn.setRequestProperty("X-Client-Platform", "Android");
		conn.setRequestProperty("X-Application-Version", "4.00");
	}
	
	private void setCustomHeaders(final HttpURLConnection conn) {
		if(params.headers == null)
			return;
		
		for(final Map.Entry<String,String> headerEntry : params.headers.entrySet())
			conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
	}
	
	private void sendSuccessfulResultToHandler(final R result) {
		final Handler handler = getHandler();
		final Message message = Message.obtain(handler, STATUS_SUCCESS, result);
		handler.sendMessage(message);
	}
	
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
	
	protected static class ServiceCallParams {
		// Required
		public HttpMethod method;
		public String path;
		
		// Optional/Defaulted
		public Map<String,String> headers = null;
		public int readTimeoutSeconds = 15;
	}
	
}
