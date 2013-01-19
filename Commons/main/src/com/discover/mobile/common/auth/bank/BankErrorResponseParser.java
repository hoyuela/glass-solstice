package com.discover.mobile.common.auth.bank;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.discover.mobile.common.net.error.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.error.ErrorResponseParser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is used to parse error responses to Discover Bank service requests. A single instance of 
 * this class is created in the static constructor. This singleton instance should be used in every subclass 
 * of JsonResponseMappingNetworkServiceCall created for BankServices. In order to use this class as the 
 * error response parser, set the ServiceCallParams.errorResponseParser to BANK_ERROR_RESPONSE_PARSER
 * in the constructor definition of a JsonResponseMappingNetworkServiceCall sub-class.
 * 
 * @author henryoyuela
 *
 */
public class BankErrorResponseParser implements ErrorResponseParser<BankErrorResponse> {
	/**
	 * Used to print logs using Android logcat API
	 */
	private static final String TAG = BankErrorResponseParser.class.getSimpleName();
	/**
	 * Singleton instance of parser so it can be reused in any Bank Network Service Call object
	 */
	private static final ErrorResponseParser<?> BANK_ERROR_RESPONSE_PARSER;	
	/**
	 * Used to parse and deserialize a JSON Error response and store it in a BankErrorResponse object
	 */
	static final ObjectMapper mapper = createObjectMapper();
	/**
	 * Creates singleton instance of BankErrorResponseParser and adds it to the list of parsers that 
	 * are tried when a response to a Network Service Call request is received. If the response has 
	 * a JSON body in the error format specified by the BANK REST API, then when the NetworkServiceCall<> 
	 * calls parseErrorResponse an ErrorResponse object is returned. Otherwise, an IOException is thrown.
	 */
	static {
		int size = DelegatingErrorResponseParser.DEFAULT_PARSER_DELEGATES.size() + 1;
		List<ErrorResponseParser<?>> errorResponseParsers = new ArrayList<ErrorResponseParser<?>>(size);
		errorResponseParsers.add(new BankErrorResponseParser());
		errorResponseParsers.addAll(DelegatingErrorResponseParser.DEFAULT_PARSER_DELEGATES);
		BANK_ERROR_RESPONSE_PARSER = new DelegatingErrorResponseParser(errorResponseParsers);
	}
	/**
	 * 
	 * @return Singleton instance of the ErrorResponseParser
	 */
	public static ErrorResponseParser<?> instance() {
		return BANK_ERROR_RESPONSE_PARSER;
	}
	/**
	 * Default constructor which can only be used within this class as there can only
	 * be one instance of this class.
	 */
	private BankErrorResponseParser() {
		
	}
	/**
	 * 
	 * @return Returns a jackson mapper used to deserialize an incoming error response with a JSON body 
	 * 		   into an BankErrorResponse
	 */
	private static ObjectMapper createObjectMapper() {
		return new ObjectMapper()
				.disable(MapperFeature.AUTO_DETECT_GETTERS)
				.disable(MapperFeature.AUTO_DETECT_SETTERS)
				.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true)
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	/**
	 * Called by a NetworkServiceCall<> in order to deserialize an incoming JSON error response into a 
	 * BankErrorResponse. If the response is NOT in the format of a BankErrorResponse, then throws
	 * an IOException
	 * 
	 * @param httpStatusCode HTTP status code in the incoming response message
	 * @param in input stream used to read the body of an incoming response
	 * @param conn HTTP connection used to send the request and receive the response
	 * 
	 * @return Returns an instance of BankErrorResponse to be used by the application to handle the response accordingly
	 */
	@Override
	public BankErrorResponse parseErrorResponse(int httpStatusCode, InputStream in, HttpURLConnection conn) throws IOException {
		BankErrorResponse ret = null;
		
		try {
			ret = mapper.readValue(in, BankErrorResponse.class);
			
			if( ret == null && Log.isLoggable(TAG, Log.ERROR) ) {
				Log.e(TAG, "Unable to map error response to an object");
			}
		} catch(Exception ex) {
			if(Log.isLoggable(TAG, Log.ERROR) ) {
				Log.e(TAG, "Exception occured trying to map error response to an object");
			}
			
			throw new IOException();
		}
		
		return ret;
		
	}
}
