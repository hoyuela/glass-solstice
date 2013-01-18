package com.discover.mobile.common.auth.bank;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.discover.mobile.common.auth.strong.StrongAuthErrorResponse;
import com.discover.mobile.common.auth.strong.StrongAuthErrorResponseParser;
import com.discover.mobile.common.net.error.ErrorResponseParser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BankErrorResponseParser implements ErrorResponseParser<BankErrorResponse> {
	private static final String TAG = StrongAuthErrorResponseParser.class.getSimpleName();
	
	private static final String NAME = "name";
	private static final String CODE = "code";
	private static final String MESSAGE = "message";
	private static final String ERRORS = "errors";
	

	@Override
	public BankErrorResponse parseErrorResponse(int httpStatusCode, InputStream in, HttpURLConnection conn) throws IOException {
		Map<String,List<String>> headers = conn.getHeaderFields();
		
		return null;
	}

	private boolean canParseErrorResponse(Map<String,List<String>> headers) {
		return headers.containsKey(NAME) && headers.containsKey(CODE) && headers.containsKey(MESSAGE);
	}
	
	private String getStrongAuthResult(Map<String,List<String>> headers) throws IOException {
		
		/*List<String> values = headers.get(ERRORS);
		
		if(values == null || values.isEmpty()) {
			String message = "No value for WWW-Authenticate header";
			Log.e(TAG, message);
			throw new IOException(message);
		}
		if(values.size() > 1)
			Log.e(TAG, "Unexpected number of WWW-Authenticate headers");
		
		return values.get(0);
		*/
		return null;
	}
}
