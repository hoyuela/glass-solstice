
package com.discover.mobile.bank.services;

import android.content.Context;

import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

/**
 * A buffer for card to the json response mapping network service call
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class BankJsonResponseMappingNetworkServiceCall<M> extends JsonResponseMappingNetworkServiceCall<M> {

	/**
	 * 
	 * @param context
	 * @param params
	 * @param modelClass
	 */
	protected BankJsonResponseMappingNetworkServiceCall(Context context,
			ServiceCallParams params, Class<M> modelClass) {
		super(context, params, modelClass);
		
	}

	@Override
	protected String getBaseUrl() {
		return BankUrlManager.getBaseUrl();
	}

	

}