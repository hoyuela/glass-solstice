package com.discover.mobile.section.account.summary;

import com.discover.mobile.common.account.summary.LatePaymentWarningTextDetail;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;

public class LatePaymentWarningTextSuccessListener implements SuccessListener<LatePaymentWarningTextDetail>{
	
	/**Fragment to return the successful nature of the call*/
	private AccountSummaryFragment fragment;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public LatePaymentWarningTextSuccessListener(final AccountSummaryFragment fragment){
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
	 * @param detail - object retrieved from the server
	 * @return the successful response
	 */
	@Override
	public void success(final LatePaymentWarningTextDetail detail) {
		fragment.storeInfoStrings(detail);
		fragment.showLatePaymentModal();
	}
}