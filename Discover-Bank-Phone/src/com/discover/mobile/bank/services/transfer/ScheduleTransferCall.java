package com.discover.mobile.bank.services.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class ScheduleTransferCall extends BankJsonResponseMappingNetworkServiceCall<TransferDetail>{
	/**Reference handler to allow the call to be back on the UI*/
	private final SimpleReferenceHandler<TransferDetail> handler;

	private TransferDetail detail;
	
	public ScheduleTransferCall(final Context context, final AsyncCallback<TransferDetail> callback,
			final TransferDetail data) {
		super(context, new PostCallParams(BankUrlManager.getUrl(BankUrlManager.TRANSFER_URL_KEY)) {{
			requiresSessionForRequest = true;

			body = data.getRequestFormat();

			errorResponseParser = BankErrorResponseParser.instance();
		}},
		TransferDetail.class);
		handler = new SimpleReferenceHandler<TransferDetail>(callback);
		
		/**Keep reference to the data sent in the request*/
		detail = data;
	}

	
	
	@Override
	protected TypedReferenceHandler<TransferDetail> getHandler() {
		return handler;
	}

	@Override
	protected TransferDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final TransferDetail data = super.parseSuccessResponse(status, headers, body);

		/**Set to and from accounts with the data used to send the request*/		
		data.toAccount = detail.toAccount;	
		data.fromAccount = detail.fromAccount;
		
		return data;
	}
}
