
package com.discover.mobile.bank.services;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;

/**
 * A buffer for bank to the json response mapping network service call
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class BankNetworkServiceCall<M> extends NetworkServiceCall<M> {

	
	/**
	 * 
	 * @param context
	 * @param params
	 */
	protected BankNetworkServiceCall(Context context, ServiceCallParams params) {
		super(context, params);
		
	}

	@Override
	protected String getBaseUrl() {
		return BankUrlManager.getBaseUrl();
	}

	

}