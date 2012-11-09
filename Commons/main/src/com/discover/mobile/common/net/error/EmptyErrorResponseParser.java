package com.discover.mobile.common.net.error;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class EmptyErrorResponseParser implements ErrorResponseParser<ErrorResponse> {

	@Override
	public ErrorResponse parseErrorResponse(final int httpStatusCode, final InputStream in,
			final HttpURLConnection conn) throws IOException {
		
		return new ErrorResponse();
	}
	
}
