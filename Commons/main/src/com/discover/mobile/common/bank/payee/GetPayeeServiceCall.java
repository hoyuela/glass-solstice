package com.discover.mobile.common.bank.payee;

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
import com.discover.mobile.common.urlmanager.BankUrlManager;

/**
 * Used for invoking the Bank - Customer Service API found at ./api/payees/. The JSON
 * response to this web-service API is de-serialized into a Payees List object and passed to the
 * application layer.
 * 
 * The following is an example of the Customer JSON response:
 * 
 *[{
 *	"id": "000001",
 *	"name": "Comcast",
 *	"nickName": "Mom's Comcast",
 *	"accountNumber": "******1114",
 *	"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *	"isVerified": true,
 *	"phone": "800.841.3000",
 *	"links": [
 *		"self" : {
 *			"ref": "https://beta.discoverbank.com/api/payees/000001",
 *			"allowed": ["GET"]
 *		},
 *		"update" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000001/put",
 *			"allowed" : ["POST"]
 *		},
 *		"delete" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000001/delete",
 *			"allowed" : ["POST"]
 *		}
 *	]
 *},
 *{
 *	"id": "000002",
 *	"name": "Comcast",
 *	"nickName": "My Comcast",
 *	"accountNumber": "******1115",
 *	"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *	"isVerified": true,
 *	"phone": "800.841.3000",
 *	"links": [
 *		"self" : {
 *			"ref": "https://beta.discoverbank.com/api/payees/000002",
 *			"allowed": ["GET"]
 *		},
 *		"update" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000002/put",
 *			"allowed" : ["POST"]
 *		},
 *		"delete" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000002/delete",
 *			"allowed" : ["POST"]
 *		}
 *	]
 *}]
 * 
 * @author jthornton
 *
 */
public class GetPayeeServiceCall  extends UnamedListJsonResponseMappingNetworkServiceCall<ListPayeeDetail> {

	/**Reference handler to return the data to the UI*/
	private final TypedReferenceHandler<ListPayeeDetail> handler;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetPayeeServiceCall(final Context context,
			final AsyncCallback<ListPayeeDetail> callback) {

		super(context, new GetCallParams(BankUrlManager.getUrl(BankUrlManager.PAYEES_URL_KEY)) {
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
		}, ListPayeeDetail.class, false);

		this.handler = new SimpleReferenceHandler<ListPayeeDetail>(callback);
	}

	/**
	 * Parse the success response.
	 * 
	 * @param status - response status
	 * @param header - map of headers
	 * @param body - response body
	 * @return list of payee details
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ListPayeeDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {

		final ListPayeeDetail details = new ListPayeeDetail();
		details.payees = (List<PayeeDetail>)super.parseSuccessResponse(status, headers, body);
		return details;

	}

	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<ListPayeeDetail> getHandler() {
		return this.handler;
	}
}
