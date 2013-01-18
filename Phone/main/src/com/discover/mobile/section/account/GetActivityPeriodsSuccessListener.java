package com.discover.mobile.section.account;

import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;

public class GetActivityPeriodsSuccessListener implements SuccessListener<RecentActivityPeriodsDetail>{
	
	/**Local instance of the fragment making this call*/
	private final ChooseDateRangeFragment fragment;
	
	/**
	 * Constructor for the class, letting the listener know the fragment using it
	 * @param fragment - fragment using this listener
	 * @param isOptedIn - true is the user is opting into push alerts
	 */
	public GetActivityPeriodsSuccessListener(final ChooseDateRangeFragment fragment){
		this.fragment = fragment;
	}

	/**
	 * Set when the listener should be executed
	 * @return when the listener should be executed
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle when the call is successful, in this case send it to the next screen
	 */
	@Override
	public void success(final RecentActivityPeriodsDetail detail) {
		fragment.displayDateRanges(detail);
		
	}
}