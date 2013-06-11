package com.discover.mobile.bank.services.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class GetExternalTransferAccountsCall extends 
											BankUnamedListJsonResponseMappingNetworkServiceCall<AccountList, Account>{
	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<AccountList> handler;

	public GetExternalTransferAccountsCall(final Context context, final AsyncCallback<AccountList> callback) {
		super(context, new GetCallParams(BankUrlManager.getUrl(BankUrlManager.EXTERNAL_TRANSFER_ACCOUNTS_URL_KEY)) {
			{
				requiresSessionForRequest = true;
				errorResponseParser = BankErrorResponseParser.instance();
				this.setCancellable(true);
			}
		},
		AccountList.class, Account.class);
		handler = new SimpleReferenceHandler<AccountList>(callback);
	}

	@Override
	protected TypedReferenceHandler<AccountList> getHandler() {
		return this.handler;
	}

	@Override
	protected AccountList parseSuccessResponse(final int status, final Map<String,List<String>> headers, 
																						final InputStream body)
			throws IOException {
		
		final AccountList accountList = new AccountList();
		accountList.accounts = super.parseUnamedList(body);
		
		return accountList;
	}
}
