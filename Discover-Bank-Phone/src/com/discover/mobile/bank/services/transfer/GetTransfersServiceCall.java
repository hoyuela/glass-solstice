package com.discover.mobile.bank.services.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.bank.ui.table.LoadMoreList;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class GetTransfersServiceCall extends 
									BankUnamedListJsonResponseMappingNetworkServiceCall<ListTransferDetail, TransferDetail>{
	/**Reference handler for returning to the UI*/
	private final TypedReferenceHandler<ListTransferDetail> handler;
	
	private TransferType transferType = null;
	
	public GetTransfersServiceCall(final Context context,
									final AsyncCallback<ListTransferDetail> callback, 
									final String url) {

		super(context, getGetCallParams(url), ListTransferDetail.class, TransferDetail.class);

		handler = new SimpleReferenceHandler<ListTransferDetail>(callback);
	}
	
	private static GetCallParams getGetCallParams(final String url) {
		return new GetCallParams(url) {{
				final int timeout = 180;
				//Sets the service call to be cancellable
				this.setCancellable(true);
				this.readTimeoutSeconds = timeout;
				this.connectTimeoutSeconds = timeout;
				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
			}};
	}

	@Override
	protected ListTransferDetail parseSuccessResponse(final int status, 
													  final Map<String,List<String>> headers, 								
													  final InputStream body) throws IOException {
		
		final ListTransferDetail accountList = new ListTransferDetail();
		accountList.transfers = super.parseUnamedList(body);
		accountList.links = super.parseHeaderForLinks(headers);
		
		handleResults(accountList);
		
		return accountList;
	}
	
	private void handleResults(final ListTransferDetail results) {
		final TransferType transferType = getTransferType();
		final boolean isLoadingMore = isLoadingMore();
		final boolean receivedNewList = !isLoadingMore() && transferType != null;
		
		if(receivedNewList) {
			BankUser.instance().getCachedActivityMap().put(transferType, results);
		} else if (isLoadingMore){
			appendResultsToCallingList(results);
		}
	}
	
	private void appendResultsToCallingList(final ListTransferDetail results) {
		final Bundle bundle = getExtras();
		final boolean hasExtras = bundle != null;
		
		if(hasExtras) {
			final LoadMoreList callingList = (LoadMoreList)bundle.getSerializable(BankExtraKeys.LOAD_MORE_LIST);
			final Enum<?> cacheKey = (Enum<?>)bundle.getSerializable(BankExtraKeys.CACHE_KEY);
			
			final boolean hasNewData = callingList != null && cacheKey != null;
			
			if(hasNewData) {
				bundle.putSerializable(LoadMoreList.APPEND_LIST_KEY, results);
				callingList.addData(bundle);
			} else {
				Log.e(GetTransfersServiceCall.class.getSimpleName(),
						"Could not append results to list. Extras bundle has no new data or key.");
			}
		}
	}
	
	public TransferType getTransferType() {
		return transferType;
	}
	
	public void setTransferType(final TransferType transferType) {
		this.transferType = transferType;
	}
	
	public boolean isLoadingMore() {
		final Bundle extras = getExtras();
		return extras != null && extras.getBoolean(BankExtraKeys.IS_LOADING_MORE);
	}

	@Override
	protected TypedReferenceHandler<ListTransferDetail> getHandler() {
		return handler;
	}
}
