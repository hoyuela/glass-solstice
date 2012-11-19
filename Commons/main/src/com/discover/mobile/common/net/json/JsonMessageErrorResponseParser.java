package com.discover.mobile.common.net.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.discover.mobile.common.net.response.ErrorResponseParser;

public class JsonMessageErrorResponseParser implements ErrorResponseParser<JsonMessageErrorResponse> {
	
	private static final String MIME_JSON = "application/json";

	@Override
	public JsonMessageErrorResponse parseErrorResponse(final int httpStatusCode, final InputStream errorStream,
			final HttpURLConnection conn) throws IOException {
		
		if(!isParseableContentType(conn) || doesntHaveDeclaredContent(conn) || !inputStreamHasContent(errorStream))
			return null;
		
		return JacksonObjectMapperHolder.mapper.readValue(errorStream, JsonMessageErrorResponse.class);
	}
	
	private static boolean isParseableContentType(final HttpURLConnection conn) {
		return MIME_JSON.equalsIgnoreCase(conn.getContentType());
	}
	
	private static boolean doesntHaveDeclaredContent(final HttpURLConnection conn) {
		return conn.getContentLength() == 0;
	}
	
	private static boolean inputStreamHasContent(final InputStream in) throws IOException {
		if(!in.markSupported())
			throw new UnsupportedOperationException("Not able to handle non-markable InputStreams");
		
		in.mark(2);
		final int result = in.read();
		in.reset();
		
		return result >= 0;
	}
	
}
