package com.discover.mobile.common.push.history;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.GetPushPreferenceReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class GetAlertHistory  extends JsonResponseMappingNetworkServiceCall<NotificationListDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<NotificationListDetail> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetAlertHistory(final Context context, 
						   final AsyncCallback<NotificationListDetail> callback,
						   final int begin,
						   final int end){
		super(context, new GetCallParams(getUrl(begin, end)){{
		
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
	 * @param context - activity context
	 * @return the url of the call
	 */
	private static String getUrl(final int begin, final int end){
		return "/cardsvcs/acs/msghist/v1/notification/history?size="+begin+"&fromMsg="+end;
	}
}
