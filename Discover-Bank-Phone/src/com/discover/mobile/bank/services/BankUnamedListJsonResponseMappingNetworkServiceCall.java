package com.discover.mobile.bank.services;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.json.UnamedListJsonResponseMappingNetworkServiceCall;

/**
 * A {@link NetworkServiceCall} that handles mapping of unnamed list JSON
 * requests and responses.
 * 
 * @param <M>
 *            The <u>m</u>odel type for the JSON result
 * @param <V>
 *            The <u>I</u>nner type for the JSON result
 */
public abstract class BankUnamedListJsonResponseMappingNetworkServiceCall<M, I>
		extends UnamedListJsonResponseMappingNetworkServiceCall<M, I> {

	/**
	 * 
	 * @param context
	 * @param params
	 * @param modelClass
	 * @param innerClass
	 */
	protected BankUnamedListJsonResponseMappingNetworkServiceCall(
			Context context, ServiceCallParams params, Class<M> modelClass,
			Class<I> innerClass) {
		super(context, params, modelClass, innerClass);

	}

	@Override
	protected String getBaseUrl() {
		return BankUrlManager.getBaseUrl();
	}

}
