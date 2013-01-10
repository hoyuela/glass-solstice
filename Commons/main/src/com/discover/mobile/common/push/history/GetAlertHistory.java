package com.discover.mobile.common.push.history;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.GetPushPreferenceReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.url.UrlManager;

/**
 * Get the alert history
 * @author jthornton
 *
 */
public class GetAlertHistory  extends JsonResponseMappingNetworkServiceCall<NotificationListDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<NotificationListDetail> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 * @param begin - spot to start getting notifications
	 * @param amount - amount to get
	 */
	public GetAlertHistory(final Context context, 
						   final AsyncCallback<NotificationListDetail> callback,
						   final int begin,
						   final int amount){
		super(context, new GetCallParams(getUrl(begin, amount)){{
		
			sendDeviceIdentifiers = true;
		}}, NotificationListDetail.class);
		
		handler = new GetPushPreferenceReferenceHandler<NotificationListDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<NotificationListDetail> getHandler() {
		return handler;
	}
	
	/**
	 * Get the url of the call
	 * @param begin - spot to start getting notifications
	 * @param amount - amount to get
	 */
	private static String getUrl(final int begin, final int amount){
		return UrlManager.getPushAlertHistoryUrl(begin, amount);
	}
}
