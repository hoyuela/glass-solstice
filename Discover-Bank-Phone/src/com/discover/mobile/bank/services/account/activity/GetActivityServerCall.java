package com.discover.mobile.bank.services.account.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;
import com.discover.mobile.common.net.json.UnamedListJsonResponseMappingNetworkServiceCall;

/**
 * Used for invoking the Bank - Account Service API found at ./api/accounts/{id}/activity. The JSON
 * response to this web-service API is de-serialized into a ActivityDetails List object and passed to the
 * application layer.
 * 
 * API Call: /api/accounts/{id}/activity
 * 
 * The following is an example of the Customer JSON response:
 * 
 * [
 *      {
 *         "id" : "123182309128",
 *         "description" : "CUSTOMER DEPOSIT",
 *         "amount" : 35000,
 *         "dates": {
 *                          "date" : "20120416T00:00:00Z",
 *                          "dateClassifier" : "POSTED_DATE",
 *                          "formattedDate" : " 04/16/2012"
 *                     },
 *         "balance" : 47000,
 *         "transactionType" : "DEPOSIT"
 *      },
 *      {
 *         "id" : "123182309129",
 *         "description" : "CUSTOMER DEPOSIT",
 *         "amount" : 12000,
 *         "dates": {
 *                          "date" : "20120415T00:00:00Z",
 *                          "dateClassifier" : "POSTED_DATE",
 *                          "formattedDate" : " 04/15/2012"
 *                     },
 *         "balance" : 12000,
 *        "transactionType" : "DEPOSIT"
 *      }
 * ]
 * 
 * @author jthornton
 *
 */
public class GetActivityServerCall extends UnamedListJsonResponseMappingNetworkServiceCall<ListActivityDetail, ActivityDetail> {

	/**Reference handler to return the data to the UI*/
	private final TypedReferenceHandler<ListActivityDetail> handler;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 * @param url to get the activity from
	 */
	public GetActivityServerCall(final Context context,
			final AsyncCallback<ListActivityDetail> callback, final String url) {

		super(context, new GetCallParams(url) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				this.clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				this.requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				this.sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				this.errorResponseParser = BankErrorResponseParser.instance();

			}
		}, ListActivityDetail.class, ActivityDetail.class, false);

		this.handler = new SimpleReferenceHandler<ListActivityDetail>(callback);
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
	protected ListActivityDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {

		final ListActivityDetail details = new ListActivityDetail();
		details.activities = super.parseUnamedList(body);
		return details;
	}

	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<ListActivityDetail> getHandler() {
		return this.handler;
	}
}
