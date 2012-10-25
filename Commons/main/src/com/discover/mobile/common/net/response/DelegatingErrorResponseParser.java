package com.discover.mobile.common.net.response;

import static com.discover.mobile.common.ThreadUtility.assertNonMainThreadExecution;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import com.discover.mobile.common.net.json.JsonMessageErrorResponseParser;
import com.google.common.collect.ImmutableList;

public class DelegatingErrorResponseParser implements ErrorResponseParser<ErrorResponse> {

	private static final Object SHARED_INSTANCE_LOCK = new Object();
	
	private static DelegatingErrorResponseParser sharedInstance;
	
	public static DelegatingErrorResponseParser getSharedInstance() {
		assertNonMainThreadExecution(); // using synchronization, don't want to block on UI thread
		
		return getOrCreateSharedInstance();
	}
	
	private static DelegatingErrorResponseParser getOrCreateSharedInstance() {
		synchronized(SHARED_INSTANCE_LOCK) {
			if(sharedInstance == null)
				sharedInstance = new DelegatingErrorResponseParser(defaultParserDelegates);
			
			return sharedInstance;
		}
	}
	
	protected static final ImmutableList<ErrorResponseParser<?>> defaultParserDelegates = createDefaultDelegates();
	
	private static ImmutableList<ErrorResponseParser<?>> createDefaultDelegates() {
		return ImmutableList.<ErrorResponseParser<?>>builder()
				.add(new JsonMessageErrorResponseParser())
				.add(new EmptyErrorResponseParser()).build();
	}
	
	private ImmutableList<ErrorResponseParser<?>> parserDelegates;
	
	protected DelegatingErrorResponseParser(final List<ErrorResponseParser<?>> parserDelegates) {
		checkNotNull(parserDelegates, "parserDelegates cannot be null");
		checkArgument(!parserDelegates.isEmpty(), "parserDelegates cannot be empty");
		
		if(defaultParserDelegates == parserDelegates)
			this.parserDelegates = defaultParserDelegates;  // since its immutable already
		else
			this.parserDelegates = ImmutableList.<ErrorResponseParser<?>>copyOf(parserDelegates);
	}
	
	@Override
	public boolean shouldParseResponse(final int httpStatusCode, final HttpURLConnection conn) {
		return isErrorStatus(httpStatusCode);
	}
	
	public static boolean isErrorStatus(final int httpStatusCode) {
		return httpStatusCode >= 400;
	}
	
	@Override
	public ErrorResponse parseErrorResponse(final int httpStatusCode, final HttpURLConnection conn) throws IOException {
		final ErrorResponse errorResponse = findDelegateAndParseErrorResponse(httpStatusCode, conn);
		setProtectedFields(errorResponse, httpStatusCode);
		return errorResponse;
	}
	
	private ErrorResponse findDelegateAndParseErrorResponse(final int httpStatusCode, final HttpURLConnection conn)
			throws IOException {
		
		for(final ErrorResponseParser<?> parser : parserDelegates) {
			if(parser.shouldParseResponse(httpStatusCode, conn))
				return parser.parseErrorResponse(httpStatusCode, conn);
		}
		
		throw new UnsupportedOperationException("Unable to parse error response, no compatible parser found");
	}
	
	private void setProtectedFields(final ErrorResponse response, final int httpStatusCode) {
		response.setHttpStatusCode(httpStatusCode);
	}
	
}
