package com.discover.mobile.common.push.history;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ReadNotificationReferenceHandler;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.url.UrlManager;

/**
 * Call to post a notification as read
 * @author jthornton
 *
 */
public class PostNotificationRead extends JsonResponseMappingNetworkServiceCall<PostReadDetail>{

	/**Reference handler to allow the call to be back on the UI*/
	private final TypedReferenceHandler<PostReadDetail> handler;
	
	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param callback - callback for the call to run in
	 * @param formData - data to be posted
	 */
	public PostNotificationRead(final Context context, final AsyncCallback<PostReadDetail> callback,
			final PostReadDetail formData){
		super(context, new PostCallParams(UrlManager.getPushReadNotificationUrl()) {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;

			body = formData;
		}},
		PostReadDetail.class);
		handler = new ReadNotificationReferenceHandler<PostReadDetail>(callback);
	}

	/**
	 * Get the reference handler for the call
	 * @return the reference handler for the call
	 */
	@Override
	protected TypedReferenceHandler<PostReadDetail> getHandler() {
		return handler;
	}
}

