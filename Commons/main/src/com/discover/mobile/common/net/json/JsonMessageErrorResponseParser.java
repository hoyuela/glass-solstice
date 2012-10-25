package com.discover.mobile.common.net.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.discover.mobile.common.net.response.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.response.ErrorResponseParser;

public class JsonMessageErrorResponseParser implements ErrorResponseParser<MessageErrorResponse> {
	
	private static final String MIME_JSON = "application/json";
	
	@Override
	public boolean shouldParseResponse(final int httpStatusCode, final HttpURLConnection conn) {
		return DelegatingErrorResponseParser.isErrorStatus(httpStatusCode) &&
				MIME_JSON.equalsIgnoreCase(conn.getContentType());
	}

	@Override
	public MessageErrorResponse parseErrorResponse(final int httpStatusCode, final HttpURLConnection conn)
			throws IOException {
		
		// Don't have to close, NetworkServiceCall handles closing HttpURLConnection
		final InputStream errorStream = conn.getErrorStream();
		
		return JacksonObjectMapperHolder.mapper.readValue(errorStream, MessageErrorResponse.class);
	}
	
}
