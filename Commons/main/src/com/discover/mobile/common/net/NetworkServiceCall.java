package com.discover.mobile.common.net;

import static com.discover.mobile.common.ThreadUtility.assertCurrentThreadHasLooper;
import static com.discover.mobile.common.ThreadUtility.assertMainThreadExecution;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.discover.mobile.common.Struct;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.json.JsonMappingRequestBodySerializer;
import com.discover.mobile.common.net.response.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.common.net.response.ErrorResponseParser;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * An abstract wrapper for network calls that should simplify common HTTP connection-related patterns for implementors.
 * 
 * @param <R> The <u>r</u>esult type that this service call will return
 */
public abstract class NetworkServiceCall<R> {
	
	private final String TAG = getClass().getSimpleName();
	
	private static final String ID_PREFIX = "%&(()!12[";
	
	private static final List<RequestBodySerializer> REQUEST_BODY_SERIALIZERS = createRequestBodySerializers();
	
	private static List<RequestBodySerializer> createRequestBodySerializers() {
		return ImmutableList.<RequestBodySerializer>builder()
				.add(new StringRequestBodySerializer())
				.add(new JsonMappingRequestBodySerializer()).build();
	}
	
	static final int RESULT_SUCCESS = 0;
	static final int RESULT_EXCEPTION = 1;
	static final int RESULT_PARSED_ERROR = 2;
	
	private final ServiceCallParams params;
	private final String BASE_URL;
	
	private Context context;
	private HttpURLConnection conn;
	private DeviceIdentifiers deviceIdentifiers;
	private RequestBodySerializer requestBodySerializer;
	
	private volatile boolean submitted = false;
	
	protected NetworkServiceCall(final Context context, final ServiceCallParams params) {
		validateConstructorArgs(context, params);
		
		this.context = context;
		this.params = params;
		
		BASE_URL = ContextNetworkUtility.getBaseUrl(context);
	}
	
	private static void validateConstructorArgs(final Context context, final ServiceCallParams params) {
		checkNotNull(context, "context cannot be null");
		
		checkArgument(!Strings.isNullOrEmpty(params.path), "params.path should never be empty");
		
		checkArgument(params.connectTimeoutSeconds > 0,
				"invalid params.connectTimeoutSeconds: " + params.connectTimeoutSeconds);
		checkArgument(params.readTimeoutSeconds > 0, "invalid params.readTimeoutSeconds: " + params.readTimeoutSeconds);
		
		checkArgument(!(params.clearsSessionBeforeRequest && params.requiresSessionForRequest),
				"params.clearsSessionBeforeRequest and params.requiresSessionForRequest cannot both be true");
		
		assertCurrentThreadHasLooper();
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
		final boolean shouldContinue = useAndClearContext();
		if(!shouldContinue)
			return;
		
		firePreSubmit();
		
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
	
	private boolean useAndClearContext() {
		try {
			checkAndUpdateSubmittedState();
			checkNetworkConnected();
			setupDeviceIdentifiers();
		} catch(final ConnectionFailureException e) {
			// We don't need to catch anything else because this should be executing on the main thread
			assertMainThreadExecution(e);
			
			sendResultToHandler(e, RESULT_EXCEPTION);
			return false;
		} finally {
			context = null; // allow context garbage collection no matter what the result is
		}
		
		return true;
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
	
	private void setupDeviceIdentifiers() {
		if(!params.sendDeviceIdentifiers)
			return;
		
		deviceIdentifiers = new DeviceIdentifiers() {{
			final TelephonyManager telephonyManager =
					(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			
			did = telephonyManager.getDeviceId();
			sid = telephonyManager.getSimSerialNumber();
			oid = telephonyManager.getDeviceId();
		}};
	}
	
	private void firePreSubmit() {
		getHandler().preSubmit();
	}
	
	// Executes in the background thread, performs the HTTP connection and delegates parsing to subclass
	private void executeRequest() throws IOException, NoSuchAlgorithmException {
		prepareGlobalSessionForConnection();
		
		conn = createConnection();
		try {
			prepareConnection();
			
			conn.connect();
			try {
				sendRequestBody();
				
				final int statusCode = getResponseCode();
				parseResponseAndSendResult(statusCode);
			} finally {
				conn.disconnect();
			}
		} finally {
			conn = null;
		}
	}
	
	private void prepareGlobalSessionForConnection() {
		if(params.clearsSessionBeforeRequest)
			ServiceCallSessionManager.clearSession();
	}
	
	private HttpURLConnection createConnection() throws IOException {
		final URL fullUrl = getFullUrl();
		return (HttpURLConnection) fullUrl.openConnection();
	}
	
	private URL getFullUrl() throws IOException {
		return new URL(BASE_URL + params.path);
	}
	
	private void prepareConnection() throws IOException, NoSuchAlgorithmException {
		doHttpMethodSpecificSetup();
		
		setDefaultHeaders();
		setSessionHeaders();
		setCustomHeaders();
		setDeviceIdentifierHeaders();
		
		setupTimeouts();
	}
	
	private boolean isPostCall() {
		return params instanceof PostCallParams;
	}
	
	private void doHttpMethodSpecificSetup() throws IOException {
		conn.setRequestMethod(params.httpMethod);
		
		if(isPostCall())
			doPostSpecificSetup();
	}
	
	private void doPostSpecificSetup() {
		final PostCallParams postParams = (PostCallParams) params;
		requestBodySerializer = findRequestBodySerializer(postParams, postParams.body);
		
		if(requestBodySerializer == null)
			throw new UnsupportedOperationException("Unable to serialize body: " + postParams.body);
		
		final String contentType = requestBodySerializer.getContentType();
		if(!Strings.isNullOrEmpty(contentType))
			conn.setRequestProperty("Content-Type", contentType);
		
		conn.setDoOutput(true);
	}
	
	private void setDefaultHeaders() {
		conn.setRequestProperty("X-Client-Platform", "Android");
		conn.setRequestProperty("X-Application-Version", "4.00");
	}
	
	private void setSessionHeaders() throws IOException {
		final boolean foundToken = ServiceCallSessionManager.prepareWithSecurityToken(conn);
		
		if(!foundToken && params.requiresSessionForRequest)
			throw new IOException("No session available when one was required for NetworkServiceCall to url: " +
					conn.getURL());
	}
	
	private void setCustomHeaders() {
		if(params.headers == null || params.headers.isEmpty())
			return;
		
		for(final Map.Entry<String,String> headerEntry : params.headers.entrySet())
			conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
	}
	
	private void setDeviceIdentifierHeaders() throws NoSuchAlgorithmException {
		if(deviceIdentifiers == null)
			return;
		
		// TODO consider not setting headers if did/oid/sid is null/empty
		conn.setRequestProperty("X-DID", getSha256Hash(deviceIdentifiers.did));
		conn.setRequestProperty("X-SID", getSha256Hash(deviceIdentifiers.sid));
		conn.setRequestProperty("X-OID", getSha256Hash(deviceIdentifiers.oid));
	}
	
	private static String getSha256Hash(final String toHash) throws NoSuchAlgorithmException {
		final String safeToHash = toHash == null ? ID_PREFIX : ID_PREFIX + toHash;
		
		final MessageDigest digester = MessageDigest.getInstance("SHA-256");
		final byte[] preHash = safeToHash.getBytes(); // TODO consider specifying charset
		
		// Reset happens automatically after digester.digest() but we don't know its state beforehand so call reset()
		digester.reset();
		final byte[] postHash = digester.digest(preHash);
		
		return convertToHex(postHash);
	}
	
	private static String convertToHex(final byte[] data) {
		return String.format("%0" + data.length * 2 + 'x', new BigInteger(1, data));
	}
	
	private void setupTimeouts() {
		conn.setConnectTimeout(params.connectTimeoutSeconds * 1000);
		conn.setReadTimeout(params.readTimeoutSeconds * 1000);
	}
	
	private void sendRequestBody() throws IOException {
		if(!isPostCall())
			return;

		final PostCallParams postParams = (PostCallParams) params;
		final OutputStream requestStream = conn.getOutputStream();
		try {
			requestBodySerializer.serializeBody(postParams.body, requestStream);
		} finally {
			requestStream.close();
		}
	}
	
	private static RequestBodySerializer findRequestBodySerializer(final PostCallParams postParams, final Object body) {
		if(postParams.customBodySerializer != null && postParams.customBodySerializer.canSerialize(body))
			return postParams.customBodySerializer;
		
		return findCapableDefaultRequestBodySerializer(body);
	}
	
	private static RequestBodySerializer findCapableDefaultRequestBodySerializer(final Object body) {
		for(final RequestBodySerializer serializer : REQUEST_BODY_SERIALIZERS) {
			if(serializer.canSerialize(body))
				return serializer;
		}
		
		return null;
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
			if("No authentication challenges found".equals(e.getMessage())) {
				Log.v(TAG, "getResponseCode() threw DCRDBasic-caused IOException, reattempting once", e);
				return conn.getResponseCode();
			}
			throw e;
		}
	}
	
	private void parseResponseAndSendResult(final int statusCode) throws IOException {
		if(DelegatingErrorResponseParser.isErrorStatus(statusCode)) {
			parseErrorResponseAndSendResult(statusCode);
		} else {
			final R result;
			final InputStream in = conn.getInputStream();
			try {
				result = parseSuccessResponse(statusCode, conn.getHeaderFields(), in);
			} finally {
				in.close();
			}
			sendResultToHandler(result, RESULT_SUCCESS);
		}
	}
	
	private void parseErrorResponseAndSendResult(final int statusCode) throws IOException {
		final ErrorResponseParser<?> chosenErrorParser = getErrorResponseParser();
		
		final ErrorResponse errorResult;
		final InputStream errorStream = getMarkSupportedErrorStream(conn);
		try {
			errorResult = chosenErrorParser.parseErrorResponse(statusCode, errorStream, conn);
		} finally {
			errorStream.close();
		}
		sendResultToHandler(errorResult, RESULT_PARSED_ERROR);
	}
	
	private ErrorResponseParser<?> getErrorResponseParser() {
		return params.errorResponseParser == null ?
				DelegatingErrorResponseParser.getSharedInstance() : params.errorResponseParser;
	}
	
	private static InputStream getMarkSupportedErrorStream(final HttpURLConnection conn) {
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
	
	@Struct
	private static class DeviceIdentifiers {
		String did;
		String sid;
		String oid;
	}
	
}
