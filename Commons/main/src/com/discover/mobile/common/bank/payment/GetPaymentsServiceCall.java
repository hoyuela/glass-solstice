package com.discover.mobile.common.bank.payment;

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
 * This is used for storing payment detailed information provided in a JSON response to a
 * Bank web-service API invocation
 * 
 * The GetPayeeServiceCall class uses this object to store the Payee information.
 * 
 * API call: /api/payments
 * 
 * JSON Example:
 * 
 *{
 *   "id": "20121006121122",
 *   "description": "Payment to Comcast",
 *    "amount": 8649,
 *    "status": "SCHEDULED",
 *    "dates": {
 *        "deliverBy": {
 *            "date": "2012-10-06T00:00:00Z",
 *           "formattedDate": "10/06/2012"
 *       }
 *   },
 *   "payee": {
 *		"id": "000001",
 *		"name": "Comcast",
 *		"nickName": "Mom's Comcast",
 *		"accountNumber": "******1114",
 *		"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *		"isVerified": true,
 *		"phone": "800.841.3000",
 *		"links": [
 *			"self" : {
 *				"ref": "https://beta.discoverbank.com/api/payees/000001",
 *				"allowed": ["GET"]
 *			},
 *			"update" : {
 *				"ref" : "https://beta.discoverbank.com/api/payees/000001/put",
 *				"allowed" : ["POST"]
 *			},
 *			"delete" : {
 *				"ref" : "https://beta.discoverbank.com/api/payees/000001/delete",
 *				"allowed" : ["POST"]
 *			}
 *		]
 *	},
 *    "paymentAccount": {
 *        "ending": "1111",
 *        "id": 1,
 *        "name": "Discover Cashback Checking",
 *        "nickname": "My Rewards Checking",
 *        "type": "CHECKING",
 *        "balance": 123456,
 *        "interestRate": {
 *            "numerator": 6,
 *            "denominator": 100,
 *            "formatted": "0.06%"
 *        },
 *        "interestEarnedLastStatement": 123,
 *        "interestYearToDate": 4321,
 *        "openDate": "2007-04-06T16: 14: 24.134455Z",
 *        "status": "OPEN",
 *        "links": {
 *            "self": {
 *                "ref": "https://www.discoverbank.com/api/accounts/1",
 *                "allowed": ["GET"]
 *            }
 *        }
 *    },
 *    "confirmationNumber": "F123-7H2Z",
 *    "memo": "service upgrade",
 *	
 *	"links": [
 *			"self" : {
 *				"ref": "https://beta.discoverbank.com/api/payment/20121006121122",
 *				"allowed": ["GET"]
 *			},
 *			"update" : {
 *				"ref" : "https://beta.discoverbank.com/api/payment/20121006121122/put",
 *				"allowed" : ["POST"]
 *			},
 *			"delete" : {
 *				"ref" : "https://beta.discoverbank.com/api/payment/20121006121122/delete",
 *				"allowed" : ["POST"]
 *			}
 *		]
 *}
 * 
 * @author jthornton
 *
 */
public class GetPaymentsServiceCall extends UnamedListJsonResponseMappingNetworkServiceCall<ListPaymentDetail> {

	private final TypedReferenceHandler<ListPaymentDetail> handler;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetPaymentsServiceCall(final Context context,
			final AsyncCallback<ListPaymentDetail> callback) {

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
		}, ListPaymentDetail.class, false);

		// TODO decide if this is the best type of handler
		this.handler = new SimpleReferenceHandler<ListPaymentDetail>(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ListPaymentDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {

		final ListPaymentDetail details = new ListPaymentDetail();
		details.payments = (List<PaymentDetail>)super.parseSuccessResponse(status, headers, body);
		return details;
	}

	@Override
	public TypedReferenceHandler<ListPaymentDetail> getHandler() {
		return this.handler;
	}
}
