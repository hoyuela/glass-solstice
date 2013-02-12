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
public class GetActivityPeriods extends JsonResponseMappingNetworkServiceCall<RecentActivityPeriodsDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<RecentActivityPeriodsDetail> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetActivityPeriods(final Context context, final AsyncCallback<RecentActivityPeriodsDetail> callback){
		super(context, new GetCallParams(CardUrlManager.getStatementIdentifiers()) {{
		
			sendDeviceIdentifiers = true;
		}}, RecentActivityPeriodsDetail.class);

		handler = new SimpleReferenceHandler<RecentActivityPeriodsDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<RecentActivityPeriodsDetail> getHandler() {
		return handler;
	}
}