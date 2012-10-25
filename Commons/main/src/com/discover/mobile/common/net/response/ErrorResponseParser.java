package com.discover.mobile.common.net.response;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface ErrorResponseParser<E extends ErrorResponse> {
	
	boolean shouldParseResponse(int httpStatusCode, HttpURLConnection conn);
	
	E parseErrorResponse(int httpStatusCode, HttpURLConnection conn) throws IOException;
	
}
