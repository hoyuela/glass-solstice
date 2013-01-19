package com.discover.mobile.common.auth.bank;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.UrlManagerBank;

/**
 * The Bank Login call for retrieving a valid token and any URL's that are
 * returned.
 * 
 * @author ajleeds
 * 
 */
public class CreateBankLoginCall extends
		JsonResponseMappingNetworkServiceCall<BankLoginData> {
	
	private final TypedReferenceHandler<BankLoginData> handler;

	public CreateBankLoginCall(final Context context,
			final AsyncCallback<BankLoginData> callback,
			final BankLoginDetails login) {
 
		super(context, new PostCallParams(UrlManagerBank.getGetTokenUrl()) {
			{

				clearsSessionBeforeRequest = true;

				requiresSessionForRequest = false;

				sendDeviceIdentifiers = true;
						
				body = login;
				
				// Specify what error parser to use when receiving an error response
				errorResponseParser = BankErrorResponseParser.instance();

			}
		}, BankLoginData.class, false);

		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<BankLoginData>(callback);
	}

	@Override
	protected TypedReferenceHandler<BankLoginData> getHandler() {
		return handler;
	}
}
