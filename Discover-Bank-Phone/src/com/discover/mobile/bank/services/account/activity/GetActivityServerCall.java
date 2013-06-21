package com.discover.mobile.bank.services.account.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.account.TransferDeletionType;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

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
public class GetActivityServerCall 
					extends BankUnamedListJsonResponseMappingNetworkServiceCall<ListActivityDetail, ActivityDetail> {

	/**Reference handler to return the data to the UI*/
	private final TypedReferenceHandler<ListActivityDetail> handler;

	private final ActivityDetailType type;
	private static final int TWO_MINUTES = 120;
	/**Retains a reference to whether we deleted an activity before calling this service*/
	private final TransferDeletionType deletionType;
	private boolean didDeleteTransfer;
	private boolean didDeletePayment;
	
	
	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 * @param url to get the activity from
	 */
	public GetActivityServerCall(final Context context,
									final AsyncCallback<ListActivityDetail> callback, final String url, 
									final ActivityDetailType type, 
									final TransferDeletionType deletionType) {

		super(context, new GetCallParams(url) {
			{
				/**Set timeout to receive response to two minutes*/
				this.readTimeoutSeconds = TWO_MINUTES;
				
				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;
				
				//Sets the service call to be cancellable
				this.setCancellable(true);
				
				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
			}
		}, ListActivityDetail.class, ActivityDetail.class);

		this.type = type;
		this.deletionType = deletionType;
		
		handler = new SimpleReferenceHandler<ListActivityDetail>(callback);
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
	protected ListActivityDetail parseSuccessResponse(final int status, 
			final Map<String,List<String>> headers, final InputStream body) throws IOException {

		final ListActivityDetail details = new ListActivityDetail();
		details.activities = super.parseUnamedList(body);
		details.links = parseHeaderForLinks(headers);
		details.type = this.type;

		/**
		 * Cache newly downloaded Activity data. The activity downloaded should be for the current account set in the
		 * BankUser. The user cannot access activity from any other account other than the account being viewed. Only
		 * the first download should be cached, any other activity downloaded should not be cached.
		 */
		if (BankUser.instance().getCurrentAccount() != null) {
			if (ActivityDetailType.Posted == type && BankUser.instance().getCurrentAccount().posted == null) {
				BankUser.instance().getCurrentAccount().posted = details;
			} else if (BankUser.instance().getCurrentAccount().scheduled == null) {
				BankUser.instance().getCurrentAccount().scheduled = details;
			}
		}

		return details;
	}

	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<ListActivityDetail> getHandler() {
		return handler;
	}
	
	public TransferDeletionType getDeletionType() {
		return deletionType;
	}
	
	public boolean getDidDeleteActivity() {
		return deletionType != null || this.didDeletePayment;
	}
	
	public boolean getDidDeletePayment() {
		return this.didDeletePayment;
	}
	
	public boolean getDidDeleteTransfer() {
		return this.didDeleteTransfer;
	}

	/**
	 * @param didDeleteTransfer the didDeleteTransfer to set
	 */
	public final void setDidDeleteTransfer(final boolean didDeleteTransfer) {
		this.didDeleteTransfer = didDeleteTransfer;
	}

	/**
	 * @param didDeletePayment the didDeletePayment to set
	 */
	public final void setDidDeletePayment(final boolean didDeletePayment) {
		this.didDeletePayment = didDeletePayment;
	}
}
