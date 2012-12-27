package com.discover.mobile.common.push.manage;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.PostPushPreferencesReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

/**
 * Call to post the devices notification preferences.  All the prefs are base of the device's xid.
 * @author jthornton
 *
 */
public class PostNotificationPreferences extends JsonResponseMappingNetworkServiceCall<PostPreferencesDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<PostPreferencesDetail> handler;
	
	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param callback - callback for the call to run in
	 * @param formData - data to be posted
	 */
	public PostNotificationPreferences(final Context context, final AsyncCallback<PostPreferencesDetail> callback,
			final PostPreferencesDetail formData){
		super(context, new PostCallParams("/cardsvcs/acs/contact/v1/preferences/enrollments") {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;

			body = formData;
		}},
		PostPreferencesDetail.class);
		handler = new PostPushPreferencesReferenceHandler<PostPreferencesDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<PostPreferencesDetail> getHandler() {
		return handler;
	}
}
