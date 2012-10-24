package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.common.base.Strings;

/**
 * An abstract wrapper for network calls that should simplify common HTTP connection-related patterns for implementors.
 * 
 * @param <R> The <u>r</u>esult type that this service call will return
 */
public abstract class NetworkServiceCall<R> {
	
	static final String TAG = NetworkServiceCall.class.getSimpleName();
	
	static final int STATUS_SUCCESS = 0;
	
	private final ServiceCallParams params;
	private final String BASE_URL;
	
	private Context context;
	
	protected NetworkServiceCall(final Context context, final ServiceCallParams params) {
		checkPreconditions(context, params);
		
		this.context = context;
		this.params = params;
		
		BASE_URL = ContextNetworkUtility.getBaseUrl(context);
	}

	protected abstract Handler getHandler();
	/**
	 * Executed in a background thread, needs to be thread-safe.
	 * 
	 * @param status 
	 * @param headers 
	 * @param body 
	 * @return The parsed result from the response
	 * @throws IOException 
	 */
	protected abstract R parseResponse(int status, Map<String,List<String>> headers, InputStream body) throws IOException;
	
	/**
	 * Submit the service call for asynchronous execution and call the callback when completed.
	 */
	public final void submit() {		
		// TODO throw a ConnectionFailureException
		try {
			if(!ContextNetworkUtility.isActiveNetworkConnected(context)) {
				Log.d(TAG, "No network connection available, dropping call");
				return;
			}
		} finally {
			context = null; // allow garbage collection no matter what the result is
		}
		
		NetworkTrafficExecutorHolder.networkTrafficExecutor.submit(new Runnable() {
			@Override
			public void run() {
				
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
	
	private static void checkPreconditions(final Context context, final ServiceCallParams params) {
		checkNotNull(context, "context cannot be null");
		checkNotNull(params.method, "params.method cannot be null");
		checkArgument(!Strings.isNullOrEmpty(params.path), "params.path should never be empty");
		checkArgument(params.readTimeoutSeconds > 0, "invalid params.readTimeoutSeconds: " + params.readTimeoutSeconds);
		checkCurrentThreadHasLooper();
	}
	
	// Executes in the background thread, does actual connection and delegates parsing to subclass
	private void executeRequest() throws IOException {
		final HttpURLConnection conn = createConnection();
		prepareConnection(conn);
		
		// TODO figure out if conn.connect() and prepareConnection() need to be called within the try block
		final R result;
		conn.connect();
		try {
			final int statusCode = conn.getResponseCode();
			final InputStream responseStream = getResponseStream(conn, statusCode);
			
			result = parseResponse(statusCode, conn.getHeaderFields(), responseStream);
		} finally {
			conn.disconnect();
		}
		
		sendSuccessfulResultToHandler(result);
	}
	
	private HttpURLConnection createConnection() throws IOException {
		final URL fullUrl = getFullUrl();
		return (HttpURLConnection) fullUrl.openConnection();
	}
	
	private void prepareConnection(final HttpURLConnection conn) throws IOException {
		conn.setRequestMethod(params.method.name());
		
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
		if(params.headers == null || params.headers.isEmpty())
			return;
		
		for(final Map.Entry<String,String> headerEntry : params.headers.entrySet())
			conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
	}
	
	private static InputStream getResponseStream(final HttpURLConnection conn, final int statusCode) throws IOException {
		if(isErrorStatus(statusCode))
			return conn.getErrorStream();
		
		return conn.getInputStream();
	}
	
	// TODO determine if there is a better way to do this
	private static boolean isErrorStatus(final int statusCode) {
		return statusCode >= 400;
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
	
	public static class ServiceCallParams {
		// Required
		public HttpMethod method;
		public String path;
		
		// Optional/Defaulted
		public Map<String,String> headers = null;
		public int readTimeoutSeconds = 15;  // TODO other timeouts
	}
	
}
