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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.discover.mobile.common.Struct;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.error.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.error.ErrorResponseParser;
import com.discover.mobile.common.net.json.JsonMappingRequestBodySerializer;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * NetworkServiceCall<> is a parameterized abstract class that allows to make
 * web service request via a java.net.HttpURlConnection object. A sub-class
 * would be required to implement the following: 
 * 
 * 		protected abstract TypedReferenceHandler<R> getHandler(); 
 * 		protected abstract R parseSuccessResponse(int status, Map<String,List<String>> headers,InputStream body)
 * 
 * A sub-class of NetworkServiceCall<>, that implements these two methods,
 * should have a data member that holds a reference to a TypeReferenceHandler<>
 * type. TypeReferenceHandler<> extends android.os.Handler
 * (http://developer.android.com/reference/android/os/Handler.html) and is what
 * allows to send an HTTP response asynchronously to the application layer using
 * Android Message/MessageQueue mechanism. A request is sent in a worker thread
 * that is destroyed once a request has completed. After a request has been sent
 * and the response parsed, the result is packaged into a Message object
 * (http://developer.android.com/reference/android/os/Message.html) and enqueued
 * in the TypeReferenceHandler's MessageQueue
 * (http://developer.android.com/reference/android/os/MessageQueue.html). The
 * message is then dequeued in the thread where the TypeReferenceHandler<> was
 * instantiated. Using the contents in the Message, the TypeReferenceHandler<>
 * then communicates the result to the application via an AsyncCallback<> type.
 * 
 * Creation and Setup: 
 * 
 * In the constructor of sub-class of NetworkServiceCall<>
 * some flags are set to control how a request is constructed prior to sending
 * out the request via the NetworkServiceCall<> submit method. These flags are
 * part Of a ServiceCallParams object which is provided in the constructor of a
 * NetworkServiceCall<> as seen here:
 * 
 * 		protected NetworkServiceCall(final Context context, final ServiceCallParams params) protected NetworkServiceCall(final Context context, final ServiceCallParams params, final boolean isCard)
 * 
 * The ServiceCallParams consists of the following data members which control
 * how the NetworkServiceCall constructs and handles a request: 
 * <pre> 
 * httpMethod  Specifies HTTP Method to use for the request, either GET or POST path  Specifies the REST web-service relative URL path. This path is concatenated with the BASE_URL to form the full url. The BASE_URL is a data member of the NetworkServiceCall<> class and is set up at instantiation in the constructor. The BASE_URL is determined based on whether the current session is for Bank or Card 
 * errorResponseParser  Holds a reference to the parser that will parse an error response. Most useful when the error response has a JSON body. By default the NetworkServiceCall will use the DelegatingErrorResponseParse which is found in com.discover.mobile.common.net.error, if this data member is set to null. 
 * headers  Contains the collection of headers that should be included in the request message 
 * connectionTimeout  Specifies how long to wait for a data connection to be setup before raising an Exception. By default set to 15 seconds. 
 * readTimeoutSeconds  Specifies how long to wait for a response to a request. By default set to 15 seconds.
 * clearSessionBeforeRequest  Set to TRUE to destroy the session token after a request has completed. The session token is cached in ServiceCallSessionManager, a singleton class object that holds a token that is provided at login. 
 * requireSessionForRequest  Specifies whether a special Header with a token has to be added to the request. 
 * sendDeviceIdentifiers  Specifies whether X-DID, X-SID, and X-OID has to be included in the request.
 * </pre>
 * 
 * Submitting A Request: 
 * 
 * In order to send a request the application must invoke
 * the submit method in the NetworkServiceCall<>. This will start a worker
 * thread from a thread pool of three threads. The worker thread is destroyed
 * once the request has completed. Upon calling submit on the
 * NetworkServiceCall<> a sequence of internal calls will be made: The
 * TypeReferenceHandler<> start method is called, which notifies the calling
 * application that the NetworkServiceCall<> has started to send the request The
 * NetworkServiceCall<> will check if the request has already been submitted, if
 * it has then it will raise an AssertionError exception. The
 * NetworkServiceCall<> will check for network connectivity, if there is no
 * connection a ConnectionFailureException exception will be raised The
 * NetworkServiceCall<> will check if it should send device identifiers and if
 * so include the required header values
 * 
 * If steps 1 through 4 execute successfully, then it will send the request via
 * the private method executeRequest(). A connection will be established via an
 * HttpURLConnection and the request sent via the same and the worker-thread
 * blocks while waiting for the response Once the response has been received the
 * status code is checked and if it successful it calls the
 * parseSuccessResponse() otherwise calls the parseErrorResponse via the
 * errorResponseParser provided in the ServiceCallParams via the constructor.
 * 
 * Retransmitting A Request: 
 * 
 * In order to retransmit a request the application
 * cannot call submit, it would have to call retransmit method as a new context
 * may need to provided. In addition, this protects a submit call be called
 * twice from within the application.
 * 
 * Handling A Response: 
 * 
 * All responses are parsed via the NetworkServiceCall<>
 * via the parseResponseAndSendResult() method. This method would look at the
 * status code provided in the response and decide whether to call
 * TypeReferenceHandler<> parseSuccessResponse method or to call the
 * errorResponseParser parseErrorResponse. The errorResponseParser is specified
 * in the ServiceCallParams object which is provided in the constructor. The
 * TypeReferenceHandler<> is provided via the sub-class implementation of
 * getHandler().
 * 
 * Closing a Connection:
 * 
 * The HttpURLConnection is created at the moment of
 * sending a request and destroyed after receiving a response or if an exception
 * occurs.
 * 
 * @param <R>
 *            The <u>r</u>esult type that this service call will return
 */
public abstract class NetworkServiceCall<R> {

	private static final String TAG = NetworkServiceCall.class.getSimpleName();

	private static final String ID_PREFIX = "%&(()!12["; //$NON-NLS-1$

	private static final List<RequestBodySerializer> REQUEST_BODY_SERIALIZERS = createRequestBodySerializers();

	private static List<RequestBodySerializer> createRequestBodySerializers() {
		return ImmutableList.<RequestBodySerializer> builder().add(new StringRequestBodySerializer())
				.add(new JsonMappingRequestBodySerializer()).build();
	}

	static final int RESULT_SUCCESS = 0;
	static final int RESULT_EXCEPTION = 1;
	static final int RESULT_PARSED_ERROR = 2;
	static final int RESULT_START = 3;

	private final ServiceCallParams params;
	private final String BASE_URL;
	private final String X_APP_VERSION;
	private final String X_CLIENT_PLATFORM;

	private Context context;
	private HttpURLConnection conn;
	private DeviceIdentifiers deviceIdentifiers;
	private RequestBodySerializer requestBodySerializer;

	private volatile boolean submitted = false;

	/**
	 * Override this in child classes to change the mechanism for what to store in cache after a service call
	 */
	protected boolean cacheResult = false;

	/**
	 * Network service call used with the base url defaulted to card.
	 * 
	 * @param context
	 * @param params
	 */
	protected NetworkServiceCall(final Context context, final ServiceCallParams params) {
		validateConstructorArgs(context, params);
		this.context = context;
		this.params = params;

		BASE_URL = getBaseUrl();
		X_APP_VERSION = ContextNetworkUtility.getStringResource(context, com.discover.mobile.common.R.string.xApplicationVersion);
		X_CLIENT_PLATFORM = ContextNetworkUtility.getStringResource(context, com.discover.mobile.common.R.string.xClientPlatform);

	}

	/**
	 * Network service call used with the base url defaulted to card.
	 * 
	 * @param context
	 * @param params
	 * @param url
	 */
	protected NetworkServiceCall(final Context context, final ServiceCallParams params, final String url) {
		validateConstructorArgs(context, params);
		this.context = context;
		this.params = params;

		BASE_URL = url;
		X_APP_VERSION = ContextNetworkUtility.getStringResource(context, com.discover.mobile.common.R.string.xApplicationVersion);
		X_CLIENT_PLATFORM = ContextNetworkUtility.getStringResource(context, com.discover.mobile.common.R.string.xClientPlatform);

	}



	private static void validateConstructorArgs(final Context context, final ServiceCallParams params) {
		checkNotNull(context, "context cannot be null");

		checkArgument(!Strings.isNullOrEmpty(params.path), "params.path should never be empty");

		checkArgument(params.connectTimeoutSeconds > 0, "invalid params.connectTimeoutSeconds: " + params.connectTimeoutSeconds);
		checkArgument(params.readTimeoutSeconds > 0, "invalid params.readTimeoutSeconds: " + params.readTimeoutSeconds);

		checkArgument(!(params.clearsSessionBeforeRequest && params.requiresSessionForRequest),
				"params.clearsSessionBeforeRequest and params.requiresSessionForRequest cannot both be true");

		assertCurrentThreadHasLooper();
	}

	protected abstract TypedReferenceHandler<R> getHandler();

	/**
	 * Allows to fetch the Handler used for responses to an HTTP request via
	 * this class. Can only be called before or after a NetworkServiceCall<> has
	 * been processed. Calling this function during the processing of an HTTP
	 * request will result in a null return value.
	 * 
	 * @return Returns the TypeReferenceHandler<> instance provided in the
	 *         constructor of this class.
	 */
	public TypedReferenceHandler<R> getHandlerSafe() {
		// Check if request is not in process otherwise return null
		if (conn == null) {
			return getHandler();
		} else {
			return null;
		}
	}

	/**
	 * Resends the same request using the same information provided during
	 * instantiation. It will not allow to retransmit if there is a transmission
	 * already being processed.
	 * 
	 * @param context
	 *            Reference to context instance from where the
	 *            NetworkServiceCall<> is being called.
	 */
	public void retransmit(final Context context) {
		// Check if request is not in the middle of processing a request already
		if (conn == null) {
			this.context = context;
			submitted = false;

			submit();
		} else {
			if (Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Unable to retransmit NetworkServiceCall because it is transmitting");
			}
		}

	}

	/**
	 * Executed in a background thread, needs to be thread-safe.
	 * 
	 * @param status
	 * @param headers
	 * @param body
	 * @return The parsed result from the response
	 * @throws IOException
	 */
	protected abstract R parseSuccessResponse(int status, Map<String, List<String>> headers, InputStream body) throws IOException;

	/**
	 * Submit the service call for asynchronous execution and call the callback
	 * when completed.
	 */
	public final void submit() {
		// Notify application that the network service call has started
		this.getHandler().getCallback().start(this);

		final boolean shouldContinue = useAndClearContext();
		if (!shouldContinue) {
			return;
		}

		NetworkTrafficExecutorHolder.networkTrafficExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					executeRequest();
				} catch (final Exception e) {
					Log.w(TAG, "caught exception during network call execution", e);
					sendResultToHandler(e, RESULT_EXCEPTION);
				}
			}
		});
	}

	private boolean useAndClearContext() {
		final TypedReferenceHandler<R> handler = this.getHandler();

		// Set network service call in handler to be able to share request call
		// information in callbacks
		if (null != handler) {
			handler.setNetworkServiceCall(this);
		}

		try {
			checkAndUpdateSubmittedState();
			checkNetworkConnected();
			setupDeviceIdentifiers();
		} catch (final ConnectionFailureException e) {
			// We don't need to catch anything else because this should be
			// executing on the main thread
			assertMainThreadExecution(e);

			sendResultToHandler(e, RESULT_EXCEPTION);
			return false;
		} finally {
			context = null; // allow context garbage collection no matter what
			// the result is
		}

		return true;
	}

	private void checkAndUpdateSubmittedState() {
		if (submitted) {
			throw new AssertionError("This call has already been submitted, it cannot be re-used");
		}

		submitted = true;
	}

	private void checkNetworkConnected() throws ConnectionFailureException {
		if (!ContextNetworkUtility.isActiveNetworkConnected(context)) {
			Log.i(TAG, "No network connection available, dropping call");
			throw new ConnectionFailureException("no active, connected network available");
		}
	}

	// TODO Move this out and put into a POJO maybe
	private void setupDeviceIdentifiers() {
		if (!params.sendDeviceIdentifiers) {
			return;
		}

		deviceIdentifiers = new DeviceIdentifiers() {
			{
				final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

				did = telephonyManager.getDeviceId();
				sid = telephonyManager.getSimSerialNumber();
				oid = telephonyManager.getDeviceId();
			}
		};
	}

	// Executes in the background thread, performs the HTTP connection and
	// delegates parsing to subclass
	private void executeRequest() throws IOException, NoSuchAlgorithmException {
		prepareGlobalSessionForConnection();

		try {
			executeConnection();
		} finally {
			postRequestClearGlobalSessionIfRequested();
		}
	}

	private void executeConnection() throws IOException, NoSuchAlgorithmException {
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
		if (params.clearsSessionBeforeRequest) {
			ServiceCallSessionManager.clearSession();
		}
	}

	private void postRequestClearGlobalSessionIfRequested() {
		if (params.clearsSessionAfterRequest) {
			ServiceCallSessionManager.clearSession();
		}
	}

	private HttpURLConnection createConnection() throws IOException {
		final URL fullUrl = getFullUrl();
		// if (fullUrl.getProtocol().toLowerCase().equals("https")) {
		// trustAllHosts();
		// HttpsURLConnection https = (HttpsURLConnection)
		// fullUrl.openConnection();
		// https.setHostnameVerifier(DO_NOT_VERIFY);
		// conn = https;
		// } else {
		conn = (HttpURLConnection) fullUrl.openConnection();
		// }
		return conn;
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(final String hostname, final SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	@SuppressWarnings("unused")
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			final SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (final Exception e) {
			if (Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Failure occurred in NetworkServiceCall");
			}
		}
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

	public boolean isPostCall() {
		return params instanceof PostCallParams;
	}

	public boolean isGetCall() {
		return params instanceof GetCallParams;
	}

	private void doHttpMethodSpecificSetup() throws IOException {
		conn.setRequestMethod(params.httpMethod);

		if (isPostCall()) {
			doPostSpecificSetup();
		}
	}

	private void doPostSpecificSetup() {
		final PostCallParams postParams = (PostCallParams) params;
		requestBodySerializer = findRequestBodySerializer(postParams, postParams.body);

		if (requestBodySerializer == null) {
			throw new UnsupportedOperationException("Unable to serialize body: " + postParams.body);
		}

		final String contentType = requestBodySerializer.getContentType();
		if (!Strings.isNullOrEmpty(contentType)) {
			conn.setRequestProperty("Content-Type", contentType);
		}

		conn.setDoOutput(true);
	}

	private void setDefaultHeaders() {
		conn.setRequestProperty("X-Client-Platform", X_CLIENT_PLATFORM);
		conn.setRequestProperty("X-Application-Version", X_APP_VERSION);
	}
	
	public Map<String, String> getHeaders() {
		return params.headers;
	}

	private void setSessionHeaders() throws IOException {
		final boolean foundToken = ServiceCallSessionManager.prepareWithSecurityToken(conn);

		if (!foundToken && params.requiresSessionForRequest) {
			throw new IOException("No session available when one was required for NetworkServiceCall to url: " + conn.getURL());
		}
	}

	private void setCustomHeaders() {
		if (params.headers == null || params.headers.isEmpty()) {
			return;
		}

		for (final Map.Entry<String, String> headerEntry : params.headers.entrySet()) {
			conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
		}
	}

	private void setDeviceIdentifierHeaders() throws NoSuchAlgorithmException {
		if (deviceIdentifiers == null) {
			return;
		}

		// TODO consider not setting headers if did/oid/sid is null/empty
		conn.setRequestProperty("X-DID", getSha256Hash(deviceIdentifiers.did));
		conn.setRequestProperty("X-SID", getSha256Hash(deviceIdentifiers.sid));
		conn.setRequestProperty("X-OID", getSha256Hash(deviceIdentifiers.oid));
	}

	private static String getSha256Hash(final String toHash) throws NoSuchAlgorithmException {
		final String safeToHash = toHash == null ? ID_PREFIX : ID_PREFIX + toHash;

		final MessageDigest digester = MessageDigest.getInstance("SHA-256");
		final byte[] preHash = safeToHash.getBytes(); // TODO consider
		// specifying charset

		// Reset happens automatically after digester.digest() but we don't know
		// its state beforehand so call reset()
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
		if (!isPostCall()) {
			return;
		}

		final PostCallParams postParams = (PostCallParams) params;
		final OutputStream requestStream = conn.getOutputStream();
		try {
			requestBodySerializer.serializeBody(postParams.body, requestStream);
		} finally {
			requestStream.close();
		}
	}

	private static RequestBodySerializer findRequestBodySerializer(final PostCallParams postParams, final Object body) {
		if (postParams.customBodySerializer != null && postParams.customBodySerializer.canSerialize(body)) {
			return postParams.customBodySerializer;
		}

		return findCapableDefaultRequestBodySerializer(body);
	}

	private static RequestBodySerializer findCapableDefaultRequestBodySerializer(final Object body) {
		for (final RequestBodySerializer serializer : REQUEST_BODY_SERIALIZERS) {
			if (serializer.canSerialize(body)) {
				return serializer;
			}
		}

		return null;
	}

	private int getResponseCode() throws IOException {
		try {
			return conn.getResponseCode();
		} catch (final IOException e) {
			// Unfortunately necessary hack - Jelly Bean's HttpURLConnection
			// implementation tries to parse out
			// information about the authentication challenge that doesn't exist
			// and throws an IOException when it isn't
			// found (the authentication scheme is "DCRDBasic", not a standard
			// scheme). Luckily between pooling
			// connections and the automated redirect support the implementation
			// seems to keep its state despite
			// exiting its control via an exception, so we try to get the
			// response code again before giving up.
			if ("No authentication challenges found".equals(e.getMessage())) {
				Log.v(TAG, "getResponseCode() threw DCRDBasic-caused IOException, reattempting once", e);
				return conn.getResponseCode();
			}
			throw e;
		}
	}

	/**
	 * Parses response (success or failure) and sends result to the handler
	 * 
	 * @param statusCode
	 * @throws IOException
	 */
	private void parseResponseAndSendResult(final int statusCode) throws IOException {
		if (DelegatingErrorResponseParser.isErrorStatus(statusCode)) {

			final ErrorResponseParser<?> chosenErrorParser = getErrorResponseParser();

			final ErrorResponse<?> errorResult;
			final InputStream errorStream = getMarkSupportedErrorStream(conn);
			try {
				errorResult = chosenErrorParser.parseErrorResponse(statusCode, errorStream, conn);
			} finally {
				errorStream.close();
			}

			sendResultToHandler(errorResult, RESULT_PARSED_ERROR);

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

	private ErrorResponseParser<?> getErrorResponseParser() {
		return params.errorResponseParser == null ? DelegatingErrorResponseParser.getSharedInstance()
				: params.errorResponseParser;
	}

	private static InputStream getMarkSupportedErrorStream(final HttpURLConnection conn) {
		final InputStream orig = conn.getErrorStream();

		if (!orig.markSupported()) {
			return new BufferedInputStream(orig);
		}

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

	/**
	 * Returns the baseUrl for the call
	 * @return
	 */
	protected abstract String getBaseUrl();

	/**
	 * 
	 * @return
	 */
	public boolean cacheResults(){
		return cacheResult;
	}

}
