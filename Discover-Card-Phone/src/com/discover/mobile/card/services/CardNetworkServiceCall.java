
package com.discover.mobile.card.services;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;

/**
 * A buffer for card to the network service call
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class CardNetworkServiceCall<M> extends NetworkServiceCall<M> {

	

	protected CardNetworkServiceCall(Context context, ServiceCallParams params) {
		super(context, params);
	}

	@Override
	protected String getBaseUrl() {
		return CardUrlManager.getBaseUrl();
	}

	

}