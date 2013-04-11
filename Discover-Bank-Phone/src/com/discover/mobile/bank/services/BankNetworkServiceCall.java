
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
	boolean isBackgroundCall = false;
	
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

}