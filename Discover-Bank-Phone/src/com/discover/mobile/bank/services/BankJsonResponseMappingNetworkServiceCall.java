
package com.discover.mobile.bank.services;

import android.content.Context;
import android.os.Bundle;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
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
	 * Flag used to mark the service call as being handled by the application. 
	 * Caller is responsible for marking this flag once the result has been processed to avoid re-handling
	 */
	boolean handled = false;

	/**
	 * 
	 * @param context
	 * @param params
	 * @param modelClass
	 */
	protected BankJsonResponseMappingNetworkServiceCall(final Context context,
			final ServiceCallParams params, final Class<M> modelClass) {
		super(context, params, modelClass);

		if(params.requiresSessionForRequest){
			Globals.setCurrentAccount(AccountType.BANK_ACCOUNT);
		}
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
		Globals.setCurrentAccount(AccountType.BANK_ACCOUNT);
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

	public Bundle getResponse() {
		return null;
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