package com.discover.mobile.bank.services.payment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * The POST call made to Bank APIs to accept a terms and service agreement.
 * It does not need to map a response because the response is expected to be empty.
 * 
 * @author scottseward
 *
 */
public class AcceptPayBillsTerms extends BankJsonResponseMappingNetworkServiceCall<PaymentDetail> {

	/**Reference handler to allow the call to be back on the UI*/
	private final SimpleReferenceHandler<PaymentDetail> handler;
	
	public AcceptPayBillsTerms(final Context context, final AsyncCallback<PaymentDetail> callback) {
		super(context, new PostCallParams(BankUrlManager.getAcceptPayBillsTerms()) {{
			requiresSessionForRequest = true;
			
			errorResponseParser = BankErrorResponseParser.instance();
			
		}}, PaymentDetail.class);
		
		handler = new SimpleReferenceHandler<PaymentDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PaymentDetail> getHandler() {
		return handler;
	}
	
	@Override
	protected PaymentDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, 
			final InputStream body)throws IOException {
		final PaymentDetail data = super.parseSuccessResponse(status, headers, body);
		
		
		return data;
	}

}
