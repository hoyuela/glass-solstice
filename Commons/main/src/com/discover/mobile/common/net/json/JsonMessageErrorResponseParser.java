package com.discover.mobile.common.net.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.discover.mobile.common.net.response.ErrorResponseParser;

public class JsonMessageErrorResponseParser implements ErrorResponseParser<MessageErrorResponse> {
	
	private static final String MIME_JSON = "application/json";

	@Override
	public MessageErrorResponse parseErrorResponse(final int httpStatusCode, final InputStream errorStream,
			final HttpURLConnection conn) throws IOException {
		
		if(!isParseableContentType(conn) || hasDeclaredContent(conn) || !inputStreamHasContent(errorStream))
			return null;
		
		return JacksonObjectMapperHolder.mapper.readValue(errorStream, MessageErrorResponse.class);
	}
	
	private boolean isParseableContentType(final HttpURLConnection conn) {
		return MIME_JSON.equalsIgnoreCase(conn.getContentType());
	}
	
	private boolean hasDeclaredContent(final HttpURLConnection conn) {
		return conn.getContentLength() > 0;
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
