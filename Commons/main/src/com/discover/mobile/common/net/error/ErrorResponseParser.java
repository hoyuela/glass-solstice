package com.discover.mobile.common.net.error;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

//Can define this directly (implement)
public interface ErrorResponseParser<E extends ErrorResponse<?>> {
	
	//Should return null if it cannot be parsed
	E parseErrorResponse(int httpStatusCode, InputStream in, HttpURLConnection conn) throws IOException;
	
}
