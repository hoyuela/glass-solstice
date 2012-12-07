package com.discover.mobile.common.push;

import roboguice.inject.ContextSingleton;
import android.content.Context;

import com.discover.mobile.common.R;
import com.xtify.sdk.api.XtifySDK;

/**
 * Service class for the push notifications.  Right now this is meant to be a singleton as there is
 * no reason to have more than one of these.  This class currently provides common notification 
 * service methods including the starting of the Xtify Service.
 * 
 * @author jthornton
 *
 */
@ContextSingleton
public class PushNotificationService {

	public void start(final Context context) {
		final String xtifyAppKey = context.getResources().getString(R.string.push_key);
		final String googleProjectId = context.getResources().getString(R.string.push_id);
		XtifySDK.start(context.getApplicationContext(), xtifyAppKey, googleProjectId);
	}
}