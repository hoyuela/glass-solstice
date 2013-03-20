package com.discover.mobile.bank.services.auth;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

public class RefreshBankSessionCall extends BankJsonResponseMappingNetworkServiceCall<Object> {

	/**Reference handler for returning to the UI*/
	final TypedReferenceHandler<Object> handler;
	
	public RefreshBankSessionCall(final Context context, final AsyncCallback<Object> callback) {
		super(context, new GetCallParams(BankUrlManager.getRefreshSessionUrl()) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = true;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
			}
		}, null);
		
		handler = new StrongReferenceHandler<Object>(callback);
		
	}

	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}
	
}
