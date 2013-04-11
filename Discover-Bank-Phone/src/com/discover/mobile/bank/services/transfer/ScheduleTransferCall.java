package com.discover.mobile.bank.services.transfer;

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

public class ScheduleTransferCall extends BankJsonResponseMappingNetworkServiceCall<TransferDetail>{
	/**Reference handler to allow the call to be back on the UI*/
	private final SimpleReferenceHandler<TransferDetail> handler;

	public ScheduleTransferCall(final Context context, final AsyncCallback<TransferDetail> callback,
			final TransferDetail modelClass) {
		super(context, new PostCallParams(BankUrlManager.getUrl(BankUrlManager.TRANSFER_URL_KEY)) {{
			requiresSessionForRequest = true;

			body = modelClass;

			errorResponseParser = BankErrorResponseParser.instance();
		}},
		TransferDetail.class);
		handler = new SimpleReferenceHandler<TransferDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<TransferDetail> getHandler() {
		// TODO Auto-generated method stub


		return handler;
	}

	@Override
	protected TransferDetail parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final TransferDetail data = super.parseSuccessResponse(status, headers, body);


		return data;
	}
}
