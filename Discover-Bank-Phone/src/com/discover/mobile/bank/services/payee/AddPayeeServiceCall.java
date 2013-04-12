package com.discover.mobile.bank.services.payee;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Used for invoking the Bank - Add a Payee Service API invoked via POST /api/payees. The JSON
 * response to this web-service API is de-serialized into a AddPayeeDetail object and passed to the
 * application layer for processing.
 * 
 * The following is an example of the Add Managed Payee JSON response:
 * {
 *		"name" : "VERIZON WIRELESS",
 *		"nickName" : "Phone Bill",
 *		"accountNumber": "8888888",
 *		"addressZip" : "60070",
 *		"isVerified" : true,
 *		"merchantNumber" : "2082"
 * }
 * 
 */
public class AddPayeeServiceCall extends BankJsonResponseMappingNetworkServiceCall<PayeeDetail>{
	/**
	 * Used for printing logs into Android Logcat
	 */
	private static final String TAG = AddPayeeServiceCall.class.getSimpleName();

	/** Reference handler to return the data to the UI */
	private final TypedReferenceHandler<PayeeDetail> handler;

	/** Reference to the PayeeDetail with the information that will be sent to the server for adding a payee**/
	private final AddPayeeDetail payeeDetail;
	private final boolean isUpdate;
	
	/**
	 * 
	 * @param context
	 *            Reference to the context invoking the API
	 * @param callback
	 *            Reference to the Handler for the response
	 */
	public AddPayeeServiceCall(final Context context,
			final AsyncCallback<PayeeDetail> callback,
			final AddPayeeDetail details, final boolean update) {

		/**Generate the ServiceCall Params and provide the Paramter Types to be used by the Super Class**/
		super(context, generateCallParams(details), PayeeDetail.class);
		
		/**Information about the Payee being added*/
		payeeDetail = details;
		
		/**Set flag usedto determine whether the service call is for an update or new payee request*/
		isUpdate = update;
		
		/**Create the handler for the response for this request*/
		this.handler = new SimpleReferenceHandler<PayeeDetail>(callback);
	}
	
	/**
	 * Method used to generate the ServiceCallParams used to configure the handling of a Service Call when
	 * a request is sent; in addition to when a response is received.
	 * @param details 
	 * 
	 * @return Returns a PostCallParams that is to be provided to the NetworkServiceCall<> base class in the constructor.
	 */
	private static PostCallParams generateCallParams(final AddPayeeDetail details) {
		final String url = BankUrlManager.getUrl(BankUrlManager.PAYEES_URL_KEY);
		final PostCallParams callParams = new PostCallParams(url);
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
		
		/**Information about the Payee being added*/
		callParams.body = details;

		// Specify what error parser to use when receiving an error
		// response is received
		callParams.errorResponseParser = BankErrorResponseParser.instance();
		
		
		
		return callParams;

	}


	/**
	 * @return the handler
	 */
	@Override
	public TypedReferenceHandler<PayeeDetail> getHandler() {
		return this.handler;
	}
	
	@Override
	protected PayeeDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final PayeeDetail data = super.parseSuccessResponse(status, headers, body);
		
		return data;
	}
	
	/**
	 * Returns a reference to a PayeeDetail object which was used for generating a JSON Post Request
	 * to add a Payee.
	 */
	public AddPayeeDetail getAddedPayee() {
		return this.payeeDetail;
	}
	
	
	/**
	 * 
	 * @return True if service call was made to update a payee, false otherise.
	 */
	public boolean isUpdate() {
		return isUpdate;
	}
	

}
