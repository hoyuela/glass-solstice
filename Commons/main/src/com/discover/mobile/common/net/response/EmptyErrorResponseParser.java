package com.discover.mobile.common.net.response;

import java.io.IOException;
import java.net.HttpURLConnection;

public class EmptyErrorResponseParser implements ErrorResponseParser<ErrorResponse> {

	@Override
	public boolean shouldParseResponse(final int httpStatusCode, final HttpURLConnection conn) {
		return DelegatingErrorResponseParser.isErrorStatus(httpStatusCode);
	}

	@Override
	public ErrorResponse parseErrorResponse(final int httpStatusCode, final HttpURLConnection conn) throws IOException {
		return new ErrorResponse();
	}
	
}
