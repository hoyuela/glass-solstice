package com.discover.mobile.card.services.account.recent;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.CardUrlManager;

/**
 * Call to get the activity periods to be displayed in the choose activity period fragment
 * @author jthornton
 *
 */
public class GetTransactions extends JsonResponseMappingNetworkServiceCall<GetTransactionDetails>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<GetTransactionDetails> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 * @PARAM category - transactions to retrieve
	 */
	public GetTransactions(final Context context, 
						   final AsyncCallback<GetTransactionDetails> callback, 
						   final RecentActivityPeriodDetail category){
		
		super(context, new GetCallParams(CardUrlManager.getGetRecentAccountTransactions(category.date)) {{
		
			sendDeviceIdentifiers = true;
		}}, GetTransactionDetails.class);

		handler = new SimpleReferenceHandler<GetTransactionDetails>(callback);
	}
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 * @param url - URL for the call
	 */
	public GetTransactions(final Context context, 
						   final AsyncCallback<GetTransactionDetails> callback, 
						   final String url){
		
		super(context, new GetCallParams(url) {{
		
			sendDeviceIdentifiers = true;
		}}, GetTransactionDetails.class);

		handler = new SimpleReferenceHandler<GetTransactionDetails>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<GetTransactionDetails> getHandler() {
		return handler;
	}
}