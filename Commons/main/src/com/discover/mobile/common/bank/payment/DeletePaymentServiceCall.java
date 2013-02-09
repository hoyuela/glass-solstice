package com.discover.mobile.common.bank.payment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams.DeleteCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;
import com.discover.mobile.common.urlmanager.BankUrlManager;

public class DeletePaymentServiceCall extends NetworkServiceCall<PaymentDetail> {
	private final TypedReferenceHandler<PaymentDetail> handler;
	
	public DeletePaymentServiceCall(final Context context, 
			final AsyncCallback<PaymentDetail> callback, final PaymentDetail pmt) {
		super(context, new DeleteCallParams(BankUrlManager.getUrl(BankUrlManager.PAYMENTS_URL_KEY) +"/" +pmt.id) {
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
		}, false);

		// TODO decide if this is the best type of handler
		this.handler = new SimpleReferenceHandler<PaymentDetail>(callback);
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

}
