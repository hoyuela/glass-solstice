package com.discover.mobile.bank.services.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

public class RefreshBankSessionCall extends BankNetworkServiceCall<Object> {

	/**Reference handler for returning to the UI*/
	final TypedReferenceHandler<Object> handler;
	
	public RefreshBankSessionCall(final Context context, final AsyncCallback<Object> callback) {
		super(context, new GetCallParams(BankUrlManager.getRefreshSessionUrl()) {
			{
				clearsSessionBeforeRequest = false;
				requiresSessionForRequest = true;
				sendDeviceIdentifiers = true;
				errorResponseParser = BankErrorResponseParser.instance();
			}
		});
		
		handler = new StrongReferenceHandler<Object>(callback);
		
	}

	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}

	@Override
	protected Object parseSuccessResponse(int status,
			Map<String, List<String>> headers, InputStream body)
			throws IOException {
		return null;
	}
	
}
