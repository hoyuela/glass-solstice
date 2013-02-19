package com.discover.mobile.bank.services.payee;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

public class SearchPayeeServiceCall
		extends
		BankUnamedListJsonResponseMappingNetworkServiceCall<SearchPayeeResultList, SearchPayeeResult> {

	/** Reference handler to return the data to the UI */
	private final TypedReferenceHandler<SearchPayeeResultList> handler;

	/**
	 * 
	 * @param context
	 *            Reference to the context invoking the API
	 * @param callback
	 *            Reference to the Handler for the response
	 */
	public SearchPayeeServiceCall(final Context context,
			final AsyncCallback<SearchPayeeResultList> callback,
			final String name) {

		
		super(context, generateCallParams(name), SearchPayeeResultList.class, SearchPayeeResult.class);

		this.handler = new SimpleReferenceHandler<SearchPayeeResultList>(
				callback);
	}
	
	private static GetCallParams generateCallParams(final String name) {
		final String url = BankUrlManager.getUrl(BankUrlManager.PAYEES_URL_KEY)+"/search?name=" +name;
		final GetCallParams callParams = new GetCallParams(url);
		// This service call is made after authenticating and receiving
		// a token,
		// therefore the session should not be cleared otherwise the
		// token will be wiped out
		callParams.clearsSessionBeforeRequest = false;

		// This ensures the token is added to the HTTP Authorization
		// Header of the HTTP request
		callParams.requiresSessionForRequest = true;

		// This ensure the required device information is supplied in
		// the Headers of the HTTP request
		callParams.sendDeviceIdentifiers = true;

		// Specify what error parser to use when receiving an error
		// response is received
		callParams.errorResponseParser = BankErrorResponseParser.instance();
		
		return callParams;

	}

	/**
	 * Parse the success response. Take the unnamed table and then parses it
	 * correctly returning a list of the POJO model class to the UI.
	 * 
	 * @param status
	 *            - response status
	 * @param header
	 *            - map of headers
	 * @param body
	 *            - response body
	 * @return list of details
	 */
	@Override
	protected SearchPayeeResultList parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body) throws IOException {

		final SearchPayeeResultList search = new SearchPayeeResultList();
		search.results = super.parseUnamedList(body);
		return search;

	}

	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<SearchPayeeResultList> getHandler() {
		return this.handler;
	}

}
