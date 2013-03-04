package com.discover.mobile.bank.services.payee;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * Used for invoking the Bank - Payee Search Service API invoked via GET /api/payees/search. The JSON
 * response to this web-service API is de-serialized into a SearchPayeeResult object and passed to the
 * application layer.
 * 
 * The following is an example of the Search JSON response:
 * [{
 *	"name": "VERIZON COMMUNICATIONS",
 *	"nickName": "VERIZON COMMUNICATIONS",
 *	"merchantNumber": "19",
 *	"isZipRequired": "true"
 * },
 * {
 *	 "name": "VERIZON WIRELESS",
 *	 "merchantNumber": "VERIZON WIRELESS",
 *	 "id": "2082",
 *	 "isZipRequired": "true"
 * }]
 * 
 */
public class SearchPayeeServiceCall extends
		BankUnamedListJsonResponseMappingNetworkServiceCall<SearchPayeeResultList, SearchPayeeResult> {

	private static final String TAG = "SearchPayeeService";

	/** Reference handler to return the data to the UI */
	private final TypedReferenceHandler<SearchPayeeResultList> handler;

	/** Reference to string used to run a search for a payee using the Bank Payee Search API*/
	private final String search;
	
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

		/**Generate the ServiceCall Params and provide the Paramter Types to be used by the Super Class**/
		super(context, generateCallParams(name), SearchPayeeResultList.class, SearchPayeeResult.class);

		/**Search Criteria to use and send to the Bank Payee Search API to get a list of Managed Verified Payees*/
		search = name;
		
		/**Create the handler for the response for this request*/
		this.handler = new SimpleReferenceHandler<SearchPayeeResultList>(callback);
	}
	
	/**
	 * Method used to generate the ServiceCallParams used to configure the handling of a Service Call when
	 * a request is sent; in addition to when a response is received.
	 * 
	 * @param name Reference to a string which contains the search criteria to use with the Bank Payee Search API
	 * 
	 * @return Returns a GetCallParams that is to be provided to the NetworkServiceCall<> base class in the constructor.
	 */
	private static GetCallParams generateCallParams(final String name) {
		final String url = BankUrlManager.getUrl(BankUrlManager.PAYEES_URL_KEY)+"search?q=" +name;
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
	 * @param status - response status
	 * @param header  - map of headers
	 * @param body - response body
	 * @return Returns List of Search Results returned from the Web Service API GET /api/payees/search
	 */
	@Override
	protected SearchPayeeResultList parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body) throws IOException {

		final SearchPayeeResultList search = new SearchPayeeResultList();
		
		try {
			search.results = super.parseUnamedList(body);
		} catch(final Exception ex) {
			
			
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to parse Search Payee list result");
			}
		}
		
		//Generate an empty list to return to the application if no results 
		if( search.results == null ) {			
			search.results = new  ArrayList<SearchPayeeResult>();
		}
		
		return search;

	}

	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<SearchPayeeResultList> getHandler() {
		return this.handler;
	}
	
	/**
	 * Returns the text used to run a search using the Bank Payee search API.
	 */
	public String getSearchText() {
		return search;
	}

}
