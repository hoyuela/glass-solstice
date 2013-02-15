package com.discover.mobile.bank.services.customer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * Used for invoking the Bank - Customer Service API found at ./api/customers/current. The JSON
 * response to this web-service API is de-serialized into a Customer object and passed to the
 * application layer.
 * 
 * The following is an example of the Customer JSON response:
 * 
 * {
 *    "id": "1",
 *    "name": {
 *        "givenName": "Andrew",
 *        "middleName": "M",
 *        "familyName": "Duckett",
 *        "formatted": "Andrew M Duckett"
 *    },
 *    "email": "andrewduckett@discover.com",
 *    "addresses": [
 *        {
 *            "type": "work",
 *            "streetAddress": "2600 Lake Cook Road",
 *            "locality": "Riverwoods",
 *            "region": "Illinois",
 *            "postalCode": "60015",
 *            "formatted": "2600 Lake Cook Road\nRiverwoods Illinois 60015"
 *        }
 *    ],
 *    "phoneNumbers": [
 *        {
 *            "type": "work",
 *            "number": "2244055446"
 *        }
 *    ],
 *    "eligibility": {
 *        "transfers": {
 *            "eligible": true,
 *            "enrolled": true
 *        },
 *        "payments": {
 *            "eligible": true,
 *            "enrolled": false,
 *            "links": {
 *                "terms": {
 *                    "ref": "https://www.discoverbank.com/api/payments/terms",
 *                    "allowed": [
 *                        "GET",
 *                        "POST"
 *                    ]
 *                }
 *            }
 *        },
 *        "deposits": {
 *            "eligible": false,
 *            "enrolled": false
 *        }
 *    },
 *    "links": {
 *        "accounts": {
 *            "ref": "/api/accounts",
 *            "allowed": [
 *                "GET"
 *            ]
 *        },
 *        "payees": {
 *            "ref": "/api/payees",
 *            "allowed": [
 *                "GET",
 *                "POST"
 *            ]
 *        },
 *        "payments": {
 *            "ref": "/api/payments",
 *            "allowed": [
 *                "GET",
 *                "POST"
 *             ]
 *        },
 *        "self": {
 *            "ref": "/api/customers/1",
 *            "allowed": [
 *                "GET"
 *            ]
 *        },
 *        "transfers": {
 *            "ref": "/api/transfers",
 *            "allowed": [
 *                "GET",
 *                "POST"
 *            ]
 *        }
 *    }
 *}
 * 
 * @author henryoyuela
 *
 */
public class CustomerServiceCall extends
		BankJsonResponseMappingNetworkServiceCall<Customer> {

	private final TypedReferenceHandler<Customer> handler;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public CustomerServiceCall(final Context context,
			final AsyncCallback<Customer> callback) {

		super(context, new GetCallParams(BankUrlManager.getCustomerServiceUrl()) {
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
		}, Customer.class);

		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<Customer>(callback);
	}
	
	@Override
	protected Customer parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final Customer data = super.parseSuccessResponse(status, headers, body);
		
		//Sets links that are used by other Bank NetworkServiceCall classes
		BankUrlManager.setNewLinks(data.links);
		
		//Stores Customer data into BankUser singleton instance to be referenced
		//later by the application layer and other classes
		BankUser.instance().setCustomerInfo(data);
		return data;
	}

	@Override
	public TypedReferenceHandler<Customer> getHandler() {
		return handler;
	}
}