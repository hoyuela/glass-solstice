package com.discover.mobile.common.push.manage;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.PushPreferenceReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class PostNotificationPreferences extends JsonResponseMappingNetworkServiceCall<PushNotificationPrefsDetail>{

	@SuppressWarnings("unused")
	private static final String TAG = PostNotificationPreferences.class.getSimpleName();
	
	private final TypedReferenceHandler<PushNotificationPrefsDetail> handler;
	
	public PostNotificationPreferences(final Context context, final AsyncCallback<PushNotificationPrefsDetail> callback,
			final PushNotificationPrefsDetail formData){
		super(context, new PostCallParams("cardsvcs/acs/contact/v1/preferences/enrollments") {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;
		}},
		PushNotificationPrefsDetail.class);
		
		handler = new PushPreferenceReferenceHandler<PushNotificationPrefsDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PushNotificationPrefsDetail> getHandler() {
		return handler;
	}
}
