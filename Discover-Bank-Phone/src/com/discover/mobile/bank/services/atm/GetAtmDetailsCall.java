/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

public class GetAtmDetailsCall extends BankJsonResponseMappingNetworkServiceCall<AtmResults> {

	private final TypedReferenceHandler<AtmResults> handler;

	static{

	}

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetAtmDetailsCall(final Context context, final AsyncCallback<AtmResults> callback, final AtmServiceHelper helper) {

		super(context, new GetCallParams(helper.getQueryString()) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;
				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = false;
				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;
				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
			}
		}, AtmResults.class, BankUrlManager.getAtmLocatorUrl());

		handler = new SimpleReferenceHandler<AtmResults>(callback);
	}

	@Override
	protected AtmResults parseSuccessResponse(final int status, final Map<String,List<String>> header, final InputStream body)
			throws IOException {

		AtmResults data = new AtmResults();
		InputStream stream = null;

		try {
			final String input = convertStreamToString(body);
			//Parse out the undefined, otherwise the parser cannot parse the data correctly
			final String str = input.replaceAll(":undefined", ":\"undefined\"");
			//Create a new input stream for parsing
			stream = new ByteArrayInputStream(str.getBytes());
		} catch (final Exception e) {
			Log.e("GetAtmDetailsCall", "exception" + e);
		}

		data = super.parseSuccessResponse(status, header, stream);
		return data;
	}

	@Override
	public TypedReferenceHandler<AtmResults> getHandler() {
		return handler;
	}

	/**
	 * Convert an input stream to a string
	 * @param is - input to convert to string
	 * @return the string
	 * @throws Exception
	 */
	public String convertStreamToString(final InputStream is) throws Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		final StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		is.close();

		return sb.toString();
	}
}

