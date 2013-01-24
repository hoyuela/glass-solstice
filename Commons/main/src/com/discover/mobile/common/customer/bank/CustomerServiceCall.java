package com.discover.mobile.common.customer.bank;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.UrlManagerBank;

/**
 * Used for invoking the Bank - Customer Service API found at ./api/customers/current. The JSON
 * response to this web-service API is de-serialized into a Customer object and passed to the
 * application layer.
 * 
 * The following is an example of the Customer JSON response:
 * 
 * {
 *		"id" : "1",
 *		"name" : "Andrew Duckett",
 *		"email" : "andrewduckett@discover.com", "addresses" : [ {
 *			"type" : "work",
 *			"streetAddress" : "2600 Lake Cook Road", "locality" : "Riverwoods",
 *			"region" : "Illinois",
 *			"postalCode" : "60015"
 *		} ],
 *		"phoneNumbers" : [ {
 *			"type" : "work",
 *			"number" : "224.405.5446" 
 *		} ],
 *		"links" : { 
 *			"accounts" : {
 *				"ref" : "https://www.discoverbank.com/api/accounts",
 *				"allowed" : [ "GET" ] },
 *			"payees" : {
 *				"ref" : "https://www.discoverbank.com/api/payees", "allowed" :[ "GET", 	"POST" ]
 *			},
 *			"payments" : {
 *				"ref" : "https://www.discoverbank.com/api/payments",
 *				"allowed" : [ "GET", "POST" ] 
 *			},
 *			"self" : {
 *				"ref" : "https://www.discoverbank.com/api/customers/1", "allowed" : [ "GET" ]
 *			},
 *			"transfers" : {
 *				"ref" : "https://www.discoverbank.com/api/transfers",
 *				"allowed" : [ "GET", "POST" ]
 *			}
 *		}
 *	}
 * 
 * @author henryoyuela
 *
 */
public class CustomerServiceCall extends
		JsonResponseMappingNetworkServiceCall<Customer> {

	private final TypedReferenceHandler<Customer> handler;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public CustomerServiceCall(final Context context,
			final AsyncCallback<Customer> callback) {

		super(context, new GetCallParams(UrlManagerBank.getCustomerServiceUrl()) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();

			}
		}, Customer.class, false);

		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<Customer>(callback);
	}
	
	@Override
	protected Customer parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final Customer data = super.parseSuccessResponse(status, headers, body);
		
			UrlManagerBank.setNewLinks(data.links);
		
		return data;
	}

	@Override
	public TypedReferenceHandler<Customer> getHandler() {
		return handler;
	}
}