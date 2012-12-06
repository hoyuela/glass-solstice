package com.discover.mobile.common.push.manage;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.PushPreferenceReferanceHandler;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class PostNotificationPreferences extends JsonResponseMappingNetworkServiceCall<PushNotificationPreferncesDetail>{

	@SuppressWarnings("unused")
	private static final String TAG = PostNotificationPreferences.class.getSimpleName();
	
	private final TypedReferenceHandler<PushNotificationPreferncesDetail> handler;
	
	public PostNotificationPreferences(final Context context, final AsyncCallback<PushNotificationPreferncesDetail> callback,
			final PushNotificationPreferncesDetail formData){
		super(context, new PostCallParams("cardsvcs/acs/contact/v1/preferences/enrollments") {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;
		}}, PushNotificationPreferncesDetail.class);
		
		handler = new PushPreferenceReferanceHandler<PushNotificationPreferncesDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PushNotificationPreferncesDetail> getHandler() {
		return handler;
	}
}
