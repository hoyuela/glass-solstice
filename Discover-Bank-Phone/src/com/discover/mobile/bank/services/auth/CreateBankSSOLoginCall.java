package com.discover.mobile.bank.services.auth;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorSSOResponseParser;

public class CreateBankSSOLoginCall extends
		BankJsonResponseMappingNetworkServiceCall<BankLoginData> {

	private final String TAG = CreateBankSSOLoginCall.class.getSimpleName();
	private final TypedReferenceHandler<BankLoginData> handler;

	protected CreateBankSSOLoginCall(Context context,
			final AsyncCallback<BankLoginData> callback,
			final BankSSOLoginDetails login) {
		super(context, new PostCallParams(BankUrlManager.getSSOTokenUrl()) {
			{
				clearsSessionBeforeRequest = true;

				requiresSessionForRequest = false;

				sendDeviceIdentifiers = true;
						
				body = login;
				
				// Specify what error parser to use when receiving an error response
				errorResponseParser = BankErrorSSOResponseParser.instance();
			}
		}, BankLoginData.class);
		// TODO Auto-generated constructor stub

		handler = new StrongReferenceHandler<BankLoginData>(callback);
	}

	@Override
	protected TypedReferenceHandler<BankLoginData> getHandler() {
		return handler;
	}

}
