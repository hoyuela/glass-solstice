package com.discover.mobile.bank.services.payment;

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

public class CreatePaymentCall extends BankJsonResponseMappingNetworkServiceCall<PaymentDetail> {

	/**Reference handler to allow the call to be back on the UI*/
	private final SimpleReferenceHandler<PaymentDetail> handler;
	
	public CreatePaymentCall(final Context context, final AsyncCallback<PaymentDetail> callback,
			final CreatePaymentDetail modelClass) {
		super(context, new PostCallParams(BankUrlManager.getUrl(BankUrlManager.PAYMENTS_URL_KEY)) {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;

			body = modelClass;
			
			errorResponseParser = BankErrorResponseParser.instance();
		}},
		PaymentDetail.class);
		handler = new SimpleReferenceHandler<PaymentDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PaymentDetail> getHandler() {
		// TODO Auto-generated method stub
		
		
		return handler;
	}
	
	@Override
	protected PaymentDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final PaymentDetail data = super.parseSuccessResponse(status, headers, body);
		
		
		return data;
	}

}
