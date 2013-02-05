package com.discover.mobile.section.account.recent;

import com.discover.mobile.common.account.recent.GetTransactionDetails;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.NetworkServiceCall;

public class GetTransactionsSuccessListener implements SuccessListener <GetTransactionDetails>{
	
	/**Fragment to return the successful nature of the call*/
	private final AccountRecentActivityFragment fragment;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public GetTransactionsSuccessListener(final AccountRecentActivityFragment fragment){
		this.fragment = fragment;
	}

	/**
	 * Get the callback priority of the success handler
	 * @return the callback priority of the success handler
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the successful response
	 * @param successObject - object retrieved from the server
	 * @return the successful response
	 */
	@Override
	public void success(final NetworkServiceCall<?> sender, final GetTransactionDetails successObject) {
		fragment.setTransactions(successObject);
		fragment.showTransactions();
	}

}
