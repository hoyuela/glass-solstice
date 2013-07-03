package com.discover.mobile.bank.services.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.bank.ui.table.LoadMoreList;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * The service call that will return a user's transaction history.
 * It maps the response to a ListTransferDetail object and returns it to the service call success handler.
 * 
 * @author scottseward
 *
 */
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
				final int timeout = 60;
				//Sets the service call to be cancellable
				this.setCancellable(true);
				this.readTimeoutSeconds = timeout;
				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
			}};
	}

	/**
	 * Saves the response body and headers to a new ListTransferDetail object and returns it.
	 */
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
	
	/**
	 * Will either cache the results in the BankUser class HashMap or will append the results
	 * to the current transfer list the current Review Transfers Fragment.
	 * 
	 * @param resultsList a ListTransferDetail object that was mapped from the service response.
	 */
	private void handleResults(final ListTransferDetail resultsList) {
		final TransferType transferType = getTransferType();
		final boolean isLoadingMore = isLoadingMore();
		final boolean receivedNewList = !isLoadingMore() && transferType != null;
		
		if(receivedNewList) {
			BankUser.instance().cacheListWithKey(transferType, resultsList);
		} else if (isLoadingMore){
			appendResultsToCallingList(resultsList);
		}
	}
	
	/**
	 * Appends the results to the current Fragment's list if the current Fragment is
	 * a LoadMoreList Fragment. Otherwise if the current Fragment is not a LoadMoreList
	 * Fragment, then the data is appended to the LoadMoreList that initiated the service call.
	 * @param results the ListTransferDetails that should be appended to the current
	 * set of data.
	 */
	private void appendResultsToCallingList(final ListTransferDetail results) {
		final Bundle bundle = getExtras();
		final boolean hasExtras = bundle != null;
		
		if(hasExtras) {
			LoadMoreList callingList = getLoadMoreListFromCurrentFragment();
					
			final Enum<?> cacheKey = (Enum<?>)bundle.getSerializable(BankExtraKeys.CACHE_KEY);
			
			final boolean hasNewData = callingList != null && cacheKey != null;
			if(callingList == null) {
				callingList = (LoadMoreList)bundle.getSerializable(BankExtraKeys.LOAD_MORE_LIST);
			}
			
			if(hasNewData && callingList != null) {
				bundle.putSerializable(LoadMoreList.APPEND_LIST_KEY, results);
				callingList.addData(bundle);
			} else {
				Log.e(GetTransfersServiceCall.class.getSimpleName(),
						"Could not append results to list. Extras bundle may have no new data, key, or Fragment.");
			}
		}
	}
	
	/**
	 * 
	 * @return a reference to the top most Fragment on the back stack if it implements the 
	 * LoadMoreList interface, null if not.
	 */
	private LoadMoreList getLoadMoreListFromCurrentFragment() {
		LoadMoreList currentListFragment = null;
		
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		if(currentActivity instanceof BankNavigationRootActivity) {
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity)currentActivity;
			final Fragment topFragment = navActivity.getCurrentContentFragment();
			if(topFragment != null && topFragment instanceof LoadMoreList) {
				currentListFragment = (LoadMoreList)topFragment;
			}
		}
		
		return currentListFragment;
	}
	
	/**
	 * 
	 * @return the TransferType that is associated with this service call.
	 */
	public TransferType getTransferType() {
		return transferType;
	}
	
	/**
	 * 
	 * @return the TransferType that is associated with this service call.
	 */
	public void setTransferType(final TransferType transferType) {
		this.transferType = transferType;
	}
	
	/**
	 * 
	 * @return if this call is intended to be a load more call and not retrieving an entirely new list.
	 */
	public boolean isLoadingMore() {
		final Bundle extras = getExtras();
		return extras != null && extras.getBoolean(BankExtraKeys.IS_LOADING_MORE);
	}

	@Override
	protected TypedReferenceHandler<ListTransferDetail> getHandler() {
		return handler;
	}
}
