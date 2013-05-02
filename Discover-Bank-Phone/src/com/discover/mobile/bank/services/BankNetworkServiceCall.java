
package com.discover.mobile.bank.services;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;

/**
 * A buffer for bank to the json response mapping network service call
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class BankNetworkServiceCall<M> 
	extends NetworkServiceCall<M> 
	implements BackgroundServiceCall {

	/**
	 * Flag used to determine whether service call is to run silently in background.
	 */
	private boolean isBackgroundCall = false;
	/**
	 * Flag used to mark the service call as being handled by the application. 
	 * Caller is responsible for marking this flag once the result has been processed to avoid re-handling
	 */
	private boolean handled = false;
	
	/**
	 * 
	 * @param context
	 * @param params
	 */
	protected BankNetworkServiceCall(final Context context, final ServiceCallParams params) {
		super(context, params);
		
	}

	@Override
	protected String getBaseUrl() {
		return BankUrlManager.getBaseUrl();
	}

	@Override
	public void setIsBackgroundCall(final boolean value) {
		isBackgroundCall = value;	
	}

	@Override
	public boolean isBackgroundCall() {
		return isBackgroundCall;
	}
	
	/**
	 * Method used to mark as the service call being handled. This allows the application to know whether the service call has
	 * been processed.
	 */
	public void markHandled() {
		handled = true;
	}
	
	/**
	 * Method used to check if the service call response has been processed.
	 * 
	 * @return True if handled, false otherwise
	 */
	public boolean isHandled() {
		return handled;
	}

}