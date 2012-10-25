package com.discover.mobile.common.net;

import static com.discover.mobile.common.ThreadUtility.assertCurrentThreadHasLooper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
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

import com.discover.mobile.common.net.response.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.common.net.response.ErrorResponseParser;
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
	static final int RESULT_PARSED_ERROR = 2;
	
	private final ServiceCallParams params;
	private final String BASE_URL;
	
	private Context context;
	private HttpURLConnection conn;
	
	private volatile boolean submitted = false;
	
	protected NetworkServiceCall(final Context context, final ServiceCallParams params) {
		validateConstructorArgs(context, params);
		
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
			checkAndUpdateSubmittedState();
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
	
	private static void validateConstructorArgs(final Context context, final ServiceCallParams params) {
		checkNotNull(context, "context cannot be null");
		
		checkNotNull(params.method, "params.method cannot be null");
		checkArgument(!Strings.isNullOrEmpty(params.path), "params.path should never be empty");
		
		checkArgument(params.connectTimeoutSeconds > 0,
				"invalid params.connectTimeoutSeconds: " + params.connectTimeoutSeconds);
		checkArgument(params.readTimeoutSeconds > 0, "invalid params.readTimeoutSeconds: " + params.readTimeoutSeconds);
		
		assertCurrentThreadHasLooper();
	}
	
	private void checkAndUpdateSubmittedState() {
		if(submitted)
			throw new AssertionError("This call has already been submitted, it cannot be re-used");
		
		submitted = true;
	}
	
	private void checkNetworkConnected() throws ConnectionFailureException {
		if(!ContextNetworkUtility.isActiveNetworkConnected(context)) {
			Log.i(TAG, "No network connection available, dropping call");
			throw new ConnectionFailureException("no active, connected network available");
		}
	}
	
	// Executes in the background thread, performs the HTTP connection and delegates parsing to subclass
	private void executeRequest() throws IOException {
		createConnection();
		try {
			prepareConnection();
			
			conn.connect();
			try {
				final int statusCode = getResponseCode();
				parseResponseAndSendResult(statusCode);
			} finally {
				conn.disconnect();
			}
		} finally {
			conn = null;
		}
	}
	
	private void createConnection() throws IOException {
		final URL fullUrl = getFullUrl();
		conn = (HttpURLConnection) fullUrl.openConnection();
	}
	
	private URL getFullUrl() throws IOException {
		return new URL(BASE_URL + params.path);
	}
	
	private void prepareConnection() throws IOException {
		conn.setRequestMethod(params.method.name());
		
		setupDefaultHeaders();
		setupSessionHeaders();
		setupCustomHeaders();
		
		setupTimeouts();
	}
	
	private void setupDefaultHeaders() {
		conn.setRequestProperty("X-Client-Platform", "Android");
		conn.setRequestProperty("X-Application-Version", "4.00");
	}
	
	private void setupSessionHeaders() {
		ServiceCallSessionManager.prepareWithSecurityToken(conn);
	}
	
	private void setupCustomHeaders() {
		if(params.headers == null || params.headers.isEmpty())
			return;
		
		for(final Map.Entry<String,String> headerEntry : params.headers.entrySet())
			conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
	}
	
	private void setupTimeouts() {
		conn.setConnectTimeout(params.connectTimeoutSeconds * 1000);
		conn.setReadTimeout(params.readTimeoutSeconds * 1000);
	}
	
	private int getResponseCode() throws IOException {
		try {
			return conn.getResponseCode();
		} catch(final IOException e) {
			// Unfortunately necessary hack - Jelly Bean's HttpURLConnection implementation tries to parse out
			// information about the authentication challenge that doesn't exist and throws an IOException when it isn't
			// found (the authentication scheme is "DCRDBasic", not a standard scheme). Luckily between pooling
			// connections and the automated redirect support the implementation seems to keep its state despite
			// exiting its control via an exception, so we try to get the response code again before giving up.
			if("No authentication challenges found".equals(e.getMessage()))
				return conn.getResponseCode();
			throw e;
		}
	}
	
	private void parseResponseAndSendResult(final int statusCode) throws IOException {
		if(DelegatingErrorResponseParser.isErrorStatus(statusCode)) {
			parseErrorResponseAndSendResult(statusCode);
		} else {
			final R result = parseSuccessResponse(statusCode, conn.getHeaderFields(), conn.getInputStream());
			sendResultToHandler(result, RESULT_SUCCESS);
		}
	}
	
	private void parseErrorResponseAndSendResult(final int statusCode) throws IOException {
		final ErrorResponseParser<?> chosenErrorParser = getErrorResponseParser();
		final InputStream errorStream = getMarkSupportedErrorStream(conn);
		final ErrorResponse errorResult = chosenErrorParser.parseErrorResponse(statusCode, errorStream, conn);
		sendResultToHandler(errorResult, RESULT_PARSED_ERROR);
	}
	
	private ErrorResponseParser<?> getErrorResponseParser() {
		return params.errorResponseParser == null ?
				DelegatingErrorResponseParser.getSharedInstance() : params.errorResponseParser;
	}
	
	private InputStream getMarkSupportedErrorStream(final HttpURLConnection conn) {
		final InputStream orig = conn.getErrorStream();
		
		if(!orig.markSupported())
			return new BufferedInputStream(orig);
		
		return orig;
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
		public ErrorResponseParser<?> errorResponseParser = null;
		public Map<String,String> headers = null;
		// TODO consider other timeout defaults
		public int connectTimeoutSeconds = 15;
		public int readTimeoutSeconds = 15;
	}
	
}
