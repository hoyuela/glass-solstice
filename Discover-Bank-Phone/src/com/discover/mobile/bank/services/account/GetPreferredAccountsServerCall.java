package com.discover.mobile.bank.services.account;

import android.content.Context;

import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;


public class GetPreferredAccountsServerCall extends BankUnamedListJsonResponseMappingNetworkServiceCall<PreferredAccounts,
																										Account> {

	/**Reference handler to return the data to the UI*/
	private final TypedReferenceHandler<PreferredAccounts> handler;
	
	public GetPreferredAccountsServerCall(Context context, final AsyncCallback<PreferredAccounts> callback) {
		
		super(context, new GetCallParams(generateUrl()) {
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
		}, PreferredAccounts.class, Account.class);

		this.handler = new SimpleReferenceHandler<PreferredAccounts>(callback);
		this.setIsBackgroundCall(true);
	}

	@Override
	protected TypedReferenceHandler<PreferredAccounts> getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}
	
	private static String generateUrl() {
		return BankUrlManager.getUrl(BankUrlManager.ACCOUNT_URL_KEY) + "preferred";	
	}

}
