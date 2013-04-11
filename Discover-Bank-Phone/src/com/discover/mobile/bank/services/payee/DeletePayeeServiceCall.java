package com.discover.mobile.bank.services.payee;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.XHttpMethodOverrideValues;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.google.common.collect.ImmutableMap;

/**
 * This is used for deleting a Managed or Unmanaged Payee using the Bank 
 * web-service API DELETE /api/payments/payees/{id}. To confirm the successful deletion
 * of a Payee a response of 204 No Content should be received
 * from the server.
 * 
 * Error Codes:
 * 204 (No Content)	 Request was successful
 * 
 * @author henryoyuela
 *
 */
public class DeletePayeeServiceCall extends BankNetworkServiceCall<PayeeDetail> {
	private final TypedReferenceHandler<PayeeDetail> handler;
	private final PayeeDetail payeeDetails;
			
	public DeletePayeeServiceCall(final Context context, 
			final AsyncCallback<PayeeDetail> callback, final PayeeDetail payee) {
		super(context, new PostCallParams(generateUrl(payee)) {
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
				
				//Custom headers for delete
				headers = ImmutableMap.<String,String>builder()
						.put(HttpHeaders.XHttpMethodOveride, XHttpMethodOverrideValues.DELETE.toString())
						.build();

			}
		});

		//Hold a reference to payment details for providing context to callbacks
		payeeDetails = payee;
		
		// TODO decide if this is the best type of handler
		this.handler = new SimpleReferenceHandler<PayeeDetail>(callback);
	}

	private static String generateUrl(final PayeeDetail payee) {
		final StringBuilder url = new StringBuilder();
		url.append(BankUrlManager.getUrl(BankUrlManager.PAYEES_URL_KEY));
		url.append(payee.id);
		url.append(BankUrlManager.DELETE_METHOD);
		return url.toString();
	}
	@Override
	protected TypedReferenceHandler<PayeeDetail> getHandler() {
		return handler;
	}

	@Override
	protected PayeeDetail parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body)
			throws IOException {
		return null;
	}

	/**
	 * 
	 * @return Returns a reference to PayeeDetail which information about the transaction being deleted
	 */
	public PayeeDetail getPayeeDetail() {
		return this.payeeDetails;
	}
}
