package com.discover.mobile.common.push.manage;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.PostPushPreferencesReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class PostNotificationPreferences extends JsonResponseMappingNetworkServiceCall<PostPreferencesDetail>{

	@SuppressWarnings("unused")
	private static final String TAG = PostNotificationPreferences.class.getSimpleName();
	
	private final TypedReferenceHandler<PostPreferencesDetail> handler;
	
	public PostNotificationPreferences(final Context context, final AsyncCallback<PostPreferencesDetail> callback,
			final PostPreferencesDetail formData){
		super(context, new PostCallParams("cardsvcs/acs/contact/v1/preferences/enrollments") {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;
		}},
		PostPreferencesDetail.class);
		
		handler = new PostPushPreferencesReferenceHandler<PostPreferencesDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PostPreferencesDetail> getHandler() {
		return handler;
	}
}
