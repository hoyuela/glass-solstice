package com.discover.mobile.common.account.recent;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.GetPushPreferenceReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.UrlManagerCard;
import com.xtify.sdk.api.XtifySDK;

public class GetActivityPeriods extends JsonResponseMappingNetworkServiceCall<RecentActivityPeriodsDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<RecentActivityPeriodsDetail> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetActivityPeriods(final Context context, final AsyncCallback<RecentActivityPeriodsDetail> callback){
		super(context, new GetCallParams(UrlManagerCard.getStatementIdentifiers()) {{
		
			sendDeviceIdentifiers = true;
		}}, RecentActivityPeriodsDetail.class);
		
		//TODO: Change this handler
		handler = new GetPushPreferenceReferenceHandler<RecentActivityPeriodsDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<RecentActivityPeriodsDetail> getHandler() {
		return handler;
	}
	
	/**
	 * Get the url of the call
	 * @param context - activity context
	 * @return the url of the call
	 */
	private static String getUrl(final Context context){
		return UrlManagerCard.getPushGetNotificationPrefUrl(XtifySDK.getXidKey(context)) ; //$NON-NLS-1$
	}
}