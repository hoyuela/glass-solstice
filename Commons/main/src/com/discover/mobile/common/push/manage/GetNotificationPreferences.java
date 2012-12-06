package com.discover.mobile.common.push.manage;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.PushPreferenceReferanceHandler;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.xtify.sdk.api.XtifySDK;

public class GetNotificationPreferences extends JsonResponseMappingNetworkServiceCall<PushNotificationPreferncesDetail>{

	private final TypedReferenceHandler<PushNotificationPreferncesDetail> handler;
	
	public GetNotificationPreferences(final Context context, final AsyncCallback<PushNotificationPreferncesDetail> callback){
		super(context, new GetCallParams(getUrl(context)) {{
		
			sendDeviceIdentifiers = true;
		}}, PushNotificationPreferncesDetail.class);
		
		handler = new PushPreferenceReferanceHandler<PushNotificationPreferncesDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PushNotificationPreferncesDetail> getHandler() {
		return handler;
	}
	
	private static String getUrl(final Context context){
		return "cardsvcs/acs/contact/v1/preferences/enrollments?vid=" + XtifySDK.getXidKey(context) ; //$NON-NLS-1$
	}
}