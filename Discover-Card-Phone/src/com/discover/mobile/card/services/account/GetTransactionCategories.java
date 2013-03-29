package com.discover.mobile.card.services.account;

import android.content.Context;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class GetTransactionCategories extends CardJsonResponseMappingNetworkServiceCall<CategoriesDetail>{

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