package com.discover.mobile.bank.services.payment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
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
 * Class used to edit a Payment using the Bank API Web-Service PUT /api/payments/{id}.
 * 
 * @author henryoyuela
 *
 */
public class UpdatePaymentCall extends BankJsonResponseMappingNetworkServiceCall<PaymentDetail> {
	/**Reference handler to allow the call to be back on the UI*/
	private final SimpleReferenceHandler<PaymentDetail> handler;
	
	public UpdatePaymentCall(final Context context, final AsyncCallback<PaymentDetail> callback,
			final CreatePaymentDetail payment, final String paymentId) {
		super(context, new PostCallParams(getUrl(paymentId)){{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;

			/**Set Payee ID to null as it is not editable*/
			payment.payee = null;
			
			body = payment;
			
			errorResponseParser = BankErrorResponseParser.instance();
			
			//Custom headers for delete
			headers = ImmutableMap.<String,String>builder()
					.put(HttpHeaders.XHttpMethodOveride, XHttpMethodOverrideValues.PUT.toString())
					.build();
		}},
		PaymentDetail.class);
		handler = new SimpleReferenceHandler<PaymentDetail>(callback);
	}
	
	private static String getUrl(final String paymentId) {
		return BankUrlManager.getUrl(BankUrlManager.PAYMENTS_URL_KEY) +paymentId +BankUrlManager.PUT_METHOD;
	}

	@Override
	protected TypedReferenceHandler<PaymentDetail> getHandler() {	
		return handler;
	}
	
	@Override
	protected PaymentDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final PaymentDetail data = super.parseSuccessResponse(status, headers, body);	
		return data;
	}

}
