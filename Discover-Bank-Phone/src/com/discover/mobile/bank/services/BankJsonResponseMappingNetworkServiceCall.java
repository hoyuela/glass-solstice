
package com.discover.mobile.bank.services;

import android.content.Context;

import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

/**
 * A buffer for card to the json response mapping network service call
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class BankJsonResponseMappingNetworkServiceCall<M> 
	extends JsonResponseMappingNetworkServiceCall<M>
	implements BackgroundServiceCall{

	/**
	 * Flag used to determine whether service call is to run silently in background.
	 */
	boolean isBackgroundCall = false;
	
	/**
	 * 
	 * @param context
	 * @param params
	 * @param modelClass
	 */
	protected BankJsonResponseMappingNetworkServiceCall(final Context context,
			final ServiceCallParams params, final Class<M> modelClass) {
		super(context, params, modelClass);

	}

	/**
	 * 
	 * @param context
	 * @param params
	 * @param modelClass
	 */
	protected BankJsonResponseMappingNetworkServiceCall(final Context context,
			final ServiceCallParams params, final Class<M> modelClass, final String url) {
		super(context, params, modelClass, url);

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