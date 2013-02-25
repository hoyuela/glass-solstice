package com.discover.mobile.bank.services.payment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * The POST call made to Bank APIs to accept a terms and service agreement.
 * It does not need to map a response because the response is expected to be empty.
 * 
 * @author scottseward
 *
 */
public class AcceptPayBillsTerms extends BankNetworkServiceCall<Object> implements Serializable {
	private static final long serialVersionUID = 5311990873372013208L;

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<Object> handler;
	
	public AcceptPayBillsTerms(final Context context, final AsyncCallback<Object> callback) {
		
		super(context, new PostCallParams(BankUrlManager.getAcceptPayBillsTerms()) {{
			requiresSessionForRequest = true;
			
			errorResponseParser = BankErrorResponseParser.instance();
			
		}});
		
		handler = new StrongReferenceHandler<Object>(callback);
	}

	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}
	
	@Override
	protected Object parseSuccessResponse(final int status, final Map<String,List<String>> headers, 
			final InputStream body)throws IOException {
		
		return this;
	}

}
