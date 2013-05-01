package com.discover.mobile.bank.services.auth.strong;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.error.ErrorResponseParser;

public class StrongAuthErrorResponseParser implements ErrorResponseParser<StrongAuthErrorResponse> {
	
	private static final String TAG = StrongAuthErrorResponseParser.class.getSimpleName();
		
	@Override
	public StrongAuthErrorResponse parseErrorResponse(final int httpStatusCode,
			final InputStream in, final HttpURLConnection conn) throws IOException {
		
		final Map<String,List<String>> headers = conn.getHeaderFields();
		if(!canParseErrorResponse(headers)){
			return null;
		}
		
		final String result = getStrongAuthResult(headers);
		return new StrongAuthErrorResponse(result);
	}
	
	private boolean canParseErrorResponse(final Map<String,List<String>> headers) {
		return headers.containsKey(HttpHeaders.Authentication);
	}
	
	private String getStrongAuthResult(final Map<String,List<String>> headers) throws IOException {
		final List<String> values = headers.get(HttpHeaders.Authentication);
		
		if(values == null || values.isEmpty()) {
			final String message = "No value for WWW-Authenticate header";
			Log.e(TAG, message);
			throw new IOException(message);
		}
		if(values.size() > 1){
			Log.e(TAG, "Unexpected number of WWW-Authenticate headers");
		}
		
		return values.get(0);
	}
	
}
