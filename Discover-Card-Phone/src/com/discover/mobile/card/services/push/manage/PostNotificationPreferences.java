package com.discover.mobile.card.services.push.manage;

import android.content.Context;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Call to post the devices notification preferences.  All the prefs are base of the device's xid.
 * @author jthornton
 *
 */
public class PostNotificationPreferences extends CardJsonResponseMappingNetworkServiceCall<PostPreferencesDetail>{

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
		super(context, new PostCallParams(CardUrlManager.getPushSetNotificationPrefUrl()) {{ //$NON-NLS-1$
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
