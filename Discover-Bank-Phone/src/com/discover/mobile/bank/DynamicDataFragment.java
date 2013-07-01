package com.discover.mobile.bank;

import android.os.Bundle;

/**
 * Interface used for fragments that need to have data loaded into itself after it is
 * present on the screen.
 * 
 * @author jthornton
 *
 */
public interface DynamicDataFragment{

	/**
	 * Handle the received data from the service call
	 * @param bundle - bundle received from the service call
	 */
	void handleReceivedData(final Bundle bundle);

	/**
	 * Set if the fragment is loading more
	 * @param isLoadingMore - if the fragment is loading more
	 */
	void setIsLoadingMore(final boolean isLoadingMore);

	/**
	 * Get if the fragment is loading more
	 * @return isLoadingMore - if the fragment is loading more
	 */
	boolean getIsLoadingMore();
	
	/**
	 * Refresh the load more listener
	 */
	void refreshListener();
}
