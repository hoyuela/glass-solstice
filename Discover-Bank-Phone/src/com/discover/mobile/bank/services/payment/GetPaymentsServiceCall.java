package com.discover.mobile.bank.services.payment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Used for invoking the Bank - Payment Service API found at ./api/payments/. The JSON
 * response to this web-service API is de-serialized into a payment detailed object and passed to the
 * application layer.
 * 
 * The GetPayeeServiceCall class uses this object to store the a list of payment information information.
 * 
 * API call: /api/payments
 * 
 * JSON Example:
 * 
 *	 {
 *	        "id": "20130308124154972478",
 *	        "description": "San4",
 *	        "status": "SCHEDULED",
 *	        "amount": {
 *	            "value": 7600,
 *	            "formatted": "$76.00"
 *	        },
 *	        "payee": {
 *	            "id": "00000000006",
 *	            "nickName": "San4"
 *	        },
 *	        "paymentMethod": {
 *	            "id": 10,
 *	            "accountNumber": {
 *	                "ending": "1569",
 *	                "formatted": "****1569",
 *	                "bankFormatted": "523-874156-9",
 *	                "unmaskedAccountNumber": "5238741569"
 *	            },
 *	            "type": "MONEY_MARKET",
 *	            "nickName": "DP MONEY MARKET",
 *	            "jointOwners": [
 *	                {
 *	                    "id": "0001655227",
 *	                    "name": {
 *	                        "givenName": "CLINTON CRAFORD",
 *	                        "formatted": "CLINTON CRAFORD null"
 *	                    },
 *	                    "phoneNumbers": [],
 *	                    "addresses": []
 *	                },
 *	                {
 *	                    "id": "0001656216",
 *	                    "name": {
 *	                        "givenName": "ROBERT DUFFY",
 *	                        "formatted": "ROBERT DUFFY null"
 *	                    },
 *	                    "phoneNumbers": [],
 *	                    "addresses": []
 *	                }
 *	            ]
 *	        },
 *	        "deliverBy": "2013-03-13T04:00:00.000+0000",
 *	        "confirmationNumber": "FLKRZ-2HKHX",
 *	        "jointPayment": true,
 *	        "jointOwnerName": "ROBERT DUFFY null",
 *	        "links": {
 *	            "self": {
 *	                "ref": "/api/payments/20130308124154972478",
 *	                "allowed": [
 *	                    "GET",
 *	                    "POST",
 *	                    "DELETE"
 *	                ]
 *	            }
 *       }
 *    }
 * 
 * @author jthornton, hoyuela
 *
 */
public class GetPaymentsServiceCall extends BankUnamedListJsonResponseMappingNetworkServiceCall<ListPaymentDetail, PaymentDetail> {

	private static final String TAG = "GetPayments";

	/**Reference handler for returning to the UI*/
	private final TypedReferenceHandler<ListPaymentDetail> handler;

	/**Boolean set to true if a payment was recently deleted*/
	private boolean wasDeleted = false;

	/**Bundle of data to pass after making the call*/
	private Bundle extras;

	/** Url used to make the service request */
	private final String queryURL;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetPaymentsServiceCall(final Context context, final AsyncCallback<ListPaymentDetail> callback, final String url) {

		super(context, new GetCallParams(url) {
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
		}, ListPaymentDetail.class, PaymentDetail.class);

		queryURL = url;

		handler = new SimpleReferenceHandler<ListPaymentDetail>(callback);
	}

	/**
	 * Parse the success response.  Take the unnamed table and then parses it correctly returning a list of the
	 * POJO model class to the UI.
	 * 
	 * @param status - response status
	 * @param header - map of headers
	 * @param body - response body
	 * @return list of details
	 */
	@Override
	protected ListPaymentDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, 
			final InputStream body) throws IOException {

		final ListPaymentDetail details = new ListPaymentDetail();
		details.payments = super.parseUnamedList(body);
		details.links = parseHeaderForLinks(headers);

		cacheData(details);

		return details;
	}

	/**
	 * Method used to cache data received via this service call.
	 * 
	 * @param details
	 *            Reference to the data that is to be cached in BankUser instance.
	 */
	private void cacheData(final ListPaymentDetail details) {
		String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
		
		/** Verify URL used for downloading payment details is not null */
		if (details != null && null != queryURL) {
			/** If the url used was for requesting scheduled payments, then cache as scheduled payments */
			if (queryURL.equalsIgnoreCase(url) && BankUser.instance().getScheduled() == null) {
				BankUser.instance().setScheduled(details);
			} else {
				url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.COMPLETED);

				/** If the url used was for requesting completed payments, then cache as completed payments */
				if (queryURL.equalsIgnoreCase(url) && BankUser.instance().getCompleted() == null) {
					BankUser.instance().setCompleted(details);
				} else {
					url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.CANCELLED);

					/** If the url used was for requesting cancelled payments, then cache as cancelled payments */
					if (queryURL.equalsIgnoreCase(url) && BankUser.instance().getCancelled() == null) {
						BankUser.instance().setCancelled(details);
					} else {
						if (Log.isLoggable(TAG, Log.WARN)) {
							Log.w(TAG, "Unable to cache payment results, unknown query!");
						}
					}
				}
			}
		} else {
			if (Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Unable to cache payment results, invalid query");
			}
		}
	}

	@Override
	public TypedReferenceHandler<ListPaymentDetail> getHandler() {
		return handler;
	}

	/**
	 * @return the wasDeleted
	 */
	public boolean isWasDeleted() {
		return wasDeleted;
	}

	/**
	 * @param wasDeleted the wasDeleted to set
	 */
	public void setWasDeleted(final boolean wasDeleted) {
		this.wasDeleted = wasDeleted;
	}

	/**
	 * @return the extras
	 */
	@Override
	public Bundle getExtras() {
		return extras;
	}

	/**
	 * @param extras the extras to set
	 */
	public void setExtras(final Bundle extras) {
		this.extras = extras;
	}
}
