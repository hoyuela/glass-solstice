package com.discover.mobile.common.net;

import static com.discover.mobile.common.ThreadUtility.assertCurrentThreadHasLooper;
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
import android.os.Message;
import android.util.Log;

import com.google.common.base.Strings;

/**
 * An abstract wrapper for network calls that should simplify common HTTP connection-related patterns for implementors.
 * 
 * @param <R> The <u>r</u>esult type that this service call will return
 */
public abstract class NetworkServiceCall<R> {
	
	private final String TAG = getClass().getSimpleName();

	static final int RESULT_SUCCESS = 0;
	static final int RESULT_EXCEPTION = 1;
	
	private final ServiceCallParams params;
	private final String BASE_URL;
	
	private Context context;
	
	protected NetworkServiceCall(final Context context, final ServiceCallParams params) {
		validateConstruction(context, params);
		
		this.context = context;
		this.params = params;
		
		BASE_URL = ContextNetworkUtility.getBaseUrl(context);
	}

	protected abstract TypedReferenceHandler<R> getHandler();
	/**
	 * Executed in a background thread, needs to be thread-safe.
	 * 
	 * @param status 
	 * @param headers 
	 * @param body 
	 * @return The parsed result from the response
	 * @throws IOException 
	 */
	protected abstract R parseSuccessResponse(int status, Map<String,List<String>> headers, InputStream body)
			throws IOException;
	
	/**
	 * Submit the service call for asynchronous execution and call the callback when completed.
	 */
	public final void submit() {
		try {
			checkNetworkConnected();
		} catch(final ConnectionFailureException e) {
			sendResultToHandler(e, RESULT_EXCEPTION);
			return;
		} finally {
			context = null; // allow context garbage collection no matter what the result is
		}
		
		NetworkTrafficExecutorHolder.networkTrafficExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					executeRequest();
				} catch(final Throwable t) {
					Log.w(TAG, "caught throwable during network call execution", t);
					sendResultToHandler(t, RESULT_EXCEPTION);
				}
			}
		});
	}
	
	private static void validateConstruction(final Context context, final ServiceCallParams params) {
		checkNotNull(context, "context cannot be null");
		
		checkNotNull(params.method, "params.method cannot be null");
		checkArgument(!Strings.isNullOrEmpty(params.path), "params.path should never be empty");
		checkArgument(params.readTimeoutSeconds > 0, "invalid params.readTimeoutSeconds: " + params.readTimeoutSeconds);
		
		assertCurrentThreadHasLooper();
	}
	
	private void checkNetworkConnected() throws ConnectionFailureException {
		if(!ContextNetworkUtility.isActiveNetworkConnected(context)) {
			Log.i(TAG, "No network connection available, dropping call");
			throw new ConnectionFailureException("no active, connected network available");
		}
	}
	
	// Executes in the background thread, performs the HTTP connection and delegates parsing to subclass
	private void executeRequest() throws IOException {
		final HttpURLConnection conn = createConnection();
		prepareConnection(conn);
		
		final R result;
		conn.connect();
		try {
			final int statusCode = conn.getResponseCode();
			final InputStream responseStream = getResponseStream(conn, statusCode);
			
			result = parseSuccessResponse(statusCode, conn.getHeaderFields(), responseStream);
		} finally {
			conn.disconnect();
		}
		
		sendResultToHandler(result, RESULT_SUCCESS);
	}
	
	private HttpURLConnection createConnection() throws IOException {
		final URL fullUrl = getFullUrl();
		return (HttpURLConnection) fullUrl.openConnection();
	}
	
	private void prepareConnection(final HttpURLConnection conn) throws IOException {
		conn.setRequestMethod(params.method.name());
		
		setDefaultHeaders(conn);
		setSessionHeaders(conn);
		setCustomHeaders(conn);
		
		conn.setReadTimeout(params.readTimeoutSeconds * 1000);
	}
	
	private URL getFullUrl() throws IOException {
		return new URL(BASE_URL + params.path);
	}
	
	private void setDefaultHeaders(final HttpURLConnection conn) {
		conn.setRequestProperty("X-Client-Platform", "Android");
		conn.setRequestProperty("X-Application-Version", "4.00");
	}
	
	private void setSessionHeaders(final HttpURLConnection conn) {
		ServiceCallSessionManager.prepareWithSecurityToken(conn);
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
	
	private void sendResultToHandler(final Object result, final int status) {
		final Handler handler = getHandler();
		final Message message = Message.obtain(handler, status, result);
		handler.sendMessage(message);
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
