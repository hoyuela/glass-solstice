package com.discover.mobile.common.account;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.CardUrlManager;

public class GetTransactionCategories extends JsonResponseMappingNetworkServiceCall<CategoriesDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<CategoriesDetail> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetTransactionCategories(final Context context, final AsyncCallback<CategoriesDetail> callback){
		super(context, new GetCallParams(CardUrlManager.getSearchTransCategoryUrl()) {{
		
			sendDeviceIdentifiers = true;
		}}, CategoriesDetail.class);

		handler = new SimpleReferenceHandler<CategoriesDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<CategoriesDetail> getHandler() {
		return handler;
	}
}