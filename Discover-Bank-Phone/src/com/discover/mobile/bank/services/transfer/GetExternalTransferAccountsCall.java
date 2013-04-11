package com.discover.mobile.bank.services.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

public class GetExternalTransferAccountsCall extends BankJsonResponseMappingNetworkServiceCall<AccountList>{
	/**Reference handler to allow the call to be back on the UI*/
	private final SimpleReferenceHandler<AccountList> handler;

	public GetExternalTransferAccountsCall(final Context context, final AsyncCallback<AccountList> callback) {
		super(context, new GetCallParams(BankUrlManager.getUrl(BankUrlManager.TRANSFER_URL_KEY) + "accounts") {
			{
				requiresSessionForRequest = true;
				errorResponseParser = BankErrorResponseParser.instance();
			}
		},
		AccountList.class);
		handler = new SimpleReferenceHandler<AccountList>(callback);
	}

	@Override
	protected TypedReferenceHandler<AccountList> getHandler() {
		// TODO Auto-generated method stub


		return handler;
	}

	@Override
	protected AccountList parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final AccountList data = super.parseSuccessResponse(status, headers, body);


		return data;
	}
}
