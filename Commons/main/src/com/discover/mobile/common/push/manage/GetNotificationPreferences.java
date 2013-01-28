package com.discover.mobile.common.push.manage;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.GetPushPreferenceReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.UrlManagerCard;
import com.xtify.sdk.api.XtifySDK;

/**
 * Call to get the devices notification preferences.  All the prefs are base of the device's xid.
 * @author jthornton
 *
 */
public class GetNotificationPreferences extends JsonResponseMappingNetworkServiceCall<PushNotificationPrefsDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<PushNotificationPrefsDetail> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback to run the call in
	 */
	public GetNotificationPreferences(final Context context, final AsyncCallback<PushNotificationPrefsDetail> callback){
		super(context, new GetCallParams(getUrl(context)) {{
		
			sendDeviceIdentifiers = true;
		}}, PushNotificationPrefsDetail.class);
		
		handler = new GetPushPreferenceReferenceHandler<PushNotificationPrefsDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<PushNotificationPrefsDetail> getHandler() {
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