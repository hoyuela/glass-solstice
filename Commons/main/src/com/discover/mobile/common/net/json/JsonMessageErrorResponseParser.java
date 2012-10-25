package com.discover.mobile.common.net.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.discover.mobile.common.net.response.ErrorResponseParser;

public class JsonMessageErrorResponseParser implements ErrorResponseParser<MessageErrorResponse> {
	
	private static final String MIME_JSON = "application/json";

	@Override
	public MessageErrorResponse parseErrorResponse(final int httpStatusCode, final InputStream in,
			final HttpURLConnection conn) throws IOException {
		
		if(!isParseableContentType(conn) || !inputStreamHasContent(in))
			return null;
		
		// Don't have to close, NetworkServiceCall handles closing HttpURLConnection
		final InputStream errorStream = conn.getErrorStream();
		
		return JacksonObjectMapperHolder.mapper.readValue(errorStream, MessageErrorResponse.class);
	}
	
	private boolean isParseableContentType(final HttpURLConnection conn) {
		return MIME_JSON.equalsIgnoreCase(conn.getContentType());
	}
	
	private boolean inputStreamHasContent(final InputStream in) throws IOException {
		if(!in.markSupported())
			throw new UnsupportedOperationException("Not able to handle non-markable InputStreams");
		
		in.mark(2);
		final int result = in.read();
		in.reset();
		
		return result >= 0;
	}
	
}
