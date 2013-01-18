package com.discover.mobile.section.account.summary;

import com.discover.mobile.common.account.summary.LatePaymentWarningDetail;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;

/**
 * Success listener for the late payment modal.  This applies specifically to the retrieval
 * of the user specific details.  The text is retrieved in another call.
 * 
 * @author jthornton
 *
 */
public class LatePaymentWarningSuccessListener  implements SuccessListener<LatePaymentWarningDetail>{
	
	/**Fragment to return the successful nature of the call*/
	private AccountSummaryFragment fragment;
	
	/**
	 * Constructor for the class
	 * @param fragment - fragment using this listener
	 */
	public LatePaymentWarningSuccessListener(final AccountSummaryFragment fragment){
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
	public void success(final LatePaymentWarningDetail detail) {
		fragment.getLatePaymentTextInformation();
		fragment.prepLatePaymentModalInfo(detail);
	}
}