package com.discover.mobile.bank.services.payment;

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
 * This is used for deleting a Scheduled Payment Transaction using the Bank 
 * web-service API DELETE /api/payments/{id}. To confirm the successful deletion
 * of a Scheduled Payment Transaction a response of 204 No Content should be received
 * from the server.
 * 
 * Error Codes:
 * 204 (No Content)	 Request was successful
 * 400 (Bad Request) An input field is malformed
 * 403 (Forbidden)	 You are not allowed to perform this operation
 * 404 (Not Found)	 The specific payment does not exist
 * 
 * @author henryoyuela
 *
 */
public class DeletePaymentServiceCall extends BankNetworkServiceCall<PaymentDetail> {
	private final TypedReferenceHandler<PaymentDetail> handler;
	private final PaymentDetail pmtDetails;
			
	public DeletePaymentServiceCall(final Context context, 
			final AsyncCallback<PaymentDetail> callback, final PaymentDetail pmt) {
		super(context, new PostCallParams(generateUrl(pmt)) {
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
		pmtDetails = pmt;
		
		// TODO decide if this is the best type of handler
		this.handler = new SimpleReferenceHandler<PaymentDetail>(callback);
	}

	private static String generateUrl(final PaymentDetail pmt) {
		final StringBuilder url = new StringBuilder();
		url.append(BankUrlManager.getUrl(BankUrlManager.PAYMENTS_URL_KEY));
		url.append(pmt.id);
		url.append(BankUrlManager.DELETE_METHOD);
		return url.toString();
	}
	@Override
	protected TypedReferenceHandler<PaymentDetail> getHandler() {
		return handler;
	}

	@Override
	protected PaymentDetail parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body)
			throws IOException {
		return null;
	}

	/**
	 * 
	 * @return Returns a reference to PaymentDetail which information about the transaction being deleted
	 */
	public PaymentDetail getPaymentDetail() {
		return this.pmtDetails;
	}
}
