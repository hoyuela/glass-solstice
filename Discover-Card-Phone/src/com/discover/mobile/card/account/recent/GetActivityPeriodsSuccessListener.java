package com.discover.mobile.card.account.recent;

import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * Success listener for getting the periods that can be selected to view transactions for.
 * @author jthornton
 *
 */
public class GetActivityPeriodsSuccessListener implements SuccessListener<RecentActivityPeriodsDetail>{
	
	/**Local instance of the fragment making this call*/
	private final AccountRecentActivityFragment fragment;
	
	/**
	 * Constructor for the class, letting the listener know the fragment using it
	 * @param fragment - fragment using this listener
	 * @param isOptedIn - true is the user is opting into push alerts
	 */
	public GetActivityPeriodsSuccessListener(final AccountRecentActivityFragment fragment){
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
	public void success(final NetworkServiceCall<?> sender, final RecentActivityPeriodsDetail detail) {
		fragment.setDateRange(detail.dates.get(0));
		fragment.setPeriods(detail);
		fragment.getTransactions();
	}
}