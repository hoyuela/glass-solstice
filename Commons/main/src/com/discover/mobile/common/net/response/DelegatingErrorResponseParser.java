package com.discover.mobile.common.net.response;

import static com.discover.mobile.common.ThreadUtility.assertNonMainThreadExecution;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.discover.mobile.common.net.json.JsonMessageErrorResponseParser;
import com.google.common.collect.ImmutableList;

public class DelegatingErrorResponseParser implements ErrorResponseParser<ErrorResponse> {

	private static final Object SHARED_INSTANCE_LOCK = new Object();
	
	private static final int HTTP_ERROR_STATUS_MINIMUM = 400;
	
	private static DelegatingErrorResponseParser SHARED_INSTANCE;
	
	public static DelegatingErrorResponseParser getSharedInstance() {
		assertNonMainThreadExecution(); // using synchronization, don't want to block on UI thread
		
		return getOrCreateSharedInstance();
	}
	
	private static DelegatingErrorResponseParser getOrCreateSharedInstance() {
		synchronized(SHARED_INSTANCE_LOCK) {
			if(SHARED_INSTANCE == null)
				SHARED_INSTANCE = new DelegatingErrorResponseParser(DEFAULT_PARSER_DELEGATES);
			
			return SHARED_INSTANCE;
		}
	}
	
	protected static final ImmutableList<ErrorResponseParser<?>> DEFAULT_PARSER_DELEGATES = createDefaultDelegates();
	
	private static ImmutableList<ErrorResponseParser<?>> createDefaultDelegates() {
		return ImmutableList.<ErrorResponseParser<?>>builder()
				.add(new JsonMessageErrorResponseParser())
				.add(new EmptyErrorResponseParser()).build();
	}
	
	private ImmutableList<ErrorResponseParser<?>> parserDelegates;
	
	protected DelegatingErrorResponseParser(final List<ErrorResponseParser<?>> parserDelegates) {
		checkNotNull(parserDelegates, "parserDelegates cannot be null");
		checkArgument(!parserDelegates.isEmpty(), "parserDelegates cannot be empty");
		
		if(DEFAULT_PARSER_DELEGATES == parserDelegates)
			this.parserDelegates = DEFAULT_PARSER_DELEGATES;  // since its immutable already
		else
			this.parserDelegates = ImmutableList.<ErrorResponseParser<?>>copyOf(parserDelegates);
	}
	
	public static boolean isErrorStatus(final int httpStatusCode) {
		return httpStatusCode >= HTTP_ERROR_STATUS_MINIMUM;
	}
	
	@Override
	public ErrorResponse parseErrorResponse(final int httpStatusCode, final InputStream in,
			final HttpURLConnection conn) throws IOException {
		
		final ErrorResponse errorResponse = findDelegateAndParseErrorResponse(httpStatusCode, in, conn);
		setProtectedFields(errorResponse, httpStatusCode);
		return errorResponse;
	}
	
	private ErrorResponse findDelegateAndParseErrorResponse(final int httpStatusCode, final InputStream in,
			final HttpURLConnection conn) throws IOException {
		
		for(final ErrorResponseParser<?> parser : parserDelegates) {
			final ErrorResponse response = parser.parseErrorResponse(httpStatusCode, in, conn);
			if(response != null)
				return response;
		}
		
		throw new UnsupportedOperationException("Unable to parse error response, no compatible parser found");
	}
	
	private static void setProtectedFields(final ErrorResponse response, final int httpStatusCode) {
		response.setHttpStatusCode(httpStatusCode);
	}
	
}
