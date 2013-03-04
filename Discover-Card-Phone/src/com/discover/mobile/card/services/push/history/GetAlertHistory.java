package com.discover.mobile.card.services.push.history;

import android.content.Context;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.card.services.push.GetPushPreferenceReferenceHandler;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Get the alert history
 * @author jthornton
 *
 */
public class GetAlertHistory  extends CardJsonResponseMappingNetworkServiceCall<NotificationListDetail>{

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
		return CardUrlManager.getPushAlertHistoryUrl(begin, amount);
	}
}
