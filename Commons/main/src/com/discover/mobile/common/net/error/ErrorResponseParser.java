package com.discover.mobile.common.net.error;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public interface ErrorResponseParser<E extends ErrorResponse> {
	
	E parseErrorResponse(int httpStatusCode, InputStream in, HttpURLConnection conn) throws IOException;
	
}
