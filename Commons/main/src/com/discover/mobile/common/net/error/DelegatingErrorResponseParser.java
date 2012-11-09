package com.discover.mobile.common.net.error;

import static com.discover.mobile.common.ThreadUtility.assertNonMainThreadExecution;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.discover.mobile.common.net.json.JsonMessageErrorResponseParser;
import com.google.common.collect.ImmutableList;

public class DelegatingErrorResponseParser implements ErrorResponseParser<ErrorResponse<?>> {
	
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
	
	protected static final List<ErrorResponseParser<? extends AbstractErrorResponse<?>>> defaultParserDelegates =
			createDefaultDelegates();
	
	private static List<ErrorResponseParser<? extends AbstractErrorResponse<?>>> createDefaultDelegates() {
		return ImmutableList.<ErrorResponseParser<? extends AbstractErrorResponse<?>>>builder()
				.add(new JsonMessageErrorResponseParser())
				.add(new EmptyErrorResponseParser()).build();
	}
	
	private List<ErrorResponseParser<? extends AbstractErrorResponse<?>>> parserDelegates;
	
	protected DelegatingErrorResponseParser(
			final List<ErrorResponseParser<? extends AbstractErrorResponse<?>>> parserDelegates) {
		
		checkNotNull(parserDelegates, "parserDelegates cannot be null");
		checkArgument(!parserDelegates.isEmpty(), "parserDelegates cannot be empty");
		
		if(defaultParserDelegates == parserDelegates)
			this.parserDelegates = defaultParserDelegates;  // since its immutable already
		else
			this.parserDelegates =
					ImmutableList.<ErrorResponseParser<? extends AbstractErrorResponse<?>>>copyOf(parserDelegates);
	}
	
	public static boolean isErrorStatus(final int httpStatusCode) {
		return httpStatusCode >= 400;
	}
	
	@Override
	public ErrorResponse<?> parseErrorResponse(final int httpStatusCode, final InputStream in,
			final HttpURLConnection conn) throws IOException {
		
		for(final ErrorResponseParser<? extends AbstractErrorResponse<?>> parser : parserDelegates) {
			final ErrorResponse<?> response = tryDelegateParse(parser, httpStatusCode, in, conn);
			if(response != null)
				return response;
		}
		
		throw new UnsupportedOperationException("Unable to parse error response, no compatible parser found");
	}
	
	private static <E extends AbstractErrorResponse<?>> ErrorResponse<?> tryDelegateParse(
			final ErrorResponseParser<E> parser, final int httpStatusCode, final InputStream in,
			final HttpURLConnection conn) throws IOException {
		
		final AbstractErrorResponse<?> response = parser.parseErrorResponse(httpStatusCode, in, conn);
		if(response != null)
			setProtectedFields(response, httpStatusCode);
		return response;
	}
	
	private static void setProtectedFields(final AbstractErrorResponse<?> response, final int httpStatusCode) {
		response.setHttpStatusCode(httpStatusCode);
	}
	
}
