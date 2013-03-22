package com.discover.mobile.common.net.error.bank;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.discover.mobile.common.net.error.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.error.ErrorResponseParser;
import com.discover.mobile.common.net.json.JacksonObjectMapperHolder;

/**
 * This class is used to parse SSO Login responses to Discover Bank service
 * requests. A single instance of this class is created in the static
 * constructor. This singleton instance should be used in every subclass of
 * JsonResponseMappingNetworkServiceCall created for BankServices. In order to
 * use this class as the error response parser, set the
 * ServiceCallParams.errorResponseParser to BANK_ERROR_RESPONSE_PARSER in the
 * constructor definition of a JsonResponseMappingNetworkServiceCall sub-class.
 * 
 */
public class BankErrorSSOResponseParser implements
		ErrorResponseParser<BankErrorSSOResponse> {
	/**
	 * Used to print logs using Android logcat API - Class Simplename was too long to log.
	 */
	private static final String TAG = "BankErrorSSOParse";
	/**
	 * Singleton instance of parser so it can be reused in any Bank Network
	 * Service Call object
	 */
	private static final ErrorResponseParser<?> BANK_ERROR_SSO_RESPONSE_PARSER;
	
	/**
	 * Creates singleton instance of BankErrorSSOResponseParser and adds it to the
	 * list of parsers that are tried when a response to a Network Service Call
	 * request is received. If the response has a JSON body in the error format
	 * specified by the BANK REST API, then when the NetworkServiceCall<> calls
	 * parseErrorResponse an ErrorResponse object is returned. Otherwise, an
	 * IOException is thrown.
	 */
	static {
		final int size = DelegatingErrorResponseParser.DEFAULT_PARSER_DELEGATES.size() + 1;
		final List<ErrorResponseParser<?>> errorResponseParsers = new ArrayList<ErrorResponseParser<?>>(size);
		errorResponseParsers.add(new BankErrorSSOResponseParser());
		errorResponseParsers.add(new BankErrorResponseParser());
		errorResponseParsers.addAll(DelegatingErrorResponseParser.DEFAULT_PARSER_DELEGATES);
		BANK_ERROR_SSO_RESPONSE_PARSER = new DelegatingErrorResponseParser(
				errorResponseParsers);
	}

	/**
	 * 
	 * @return Singleton instance of the ErrorResponseParser
	 */
	public static ErrorResponseParser<?> instance() {
		return BANK_ERROR_SSO_RESPONSE_PARSER;
	}

	/**
	 * Default constructor which can only be used within this class as there can
	 * only be one instance of this class.
	 */
	private BankErrorSSOResponseParser() {

	}

	/**
	 * Called by a NetworkServiceCall<> in order to deserialize an incoming JSON
	 * error response into a BankErrorResponse. If the response is NOT in the
	 * format of a BankErrorResponse, then throws an IOException
	 * 
	 * @param httpStatusCode
	 *            HTTP status code in the incoming response message
	 * @param in
	 *            input stream used to read the body of an incoming response
	 * @param conn
	 *            HTTP connection used to send the request and receive the
	 *            response
	 * 
	 * @return Returns an instance of BankErrorResponse to be used by the
	 *         application to handle the response accordingly
	 */
	@Override
	public BankErrorSSOResponse parseErrorResponse(final int httpStatusCode,
			final InputStream in, final HttpURLConnection conn) throws IOException {
		BankErrorSSOResponse ret = null;

		try {
			ret = JacksonObjectMapperHolder.getMapper().readValue(in,
					BankErrorSSOResponse.class);

			if (ret == null && Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to map error response to an object");
			} 
		} catch (final Exception ex) {
			Log.e(TAG, ex.toString());
			if (Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG,
						"Exception occured trying to map error response to an object");
			}
		}

		return ret;

	}
}
