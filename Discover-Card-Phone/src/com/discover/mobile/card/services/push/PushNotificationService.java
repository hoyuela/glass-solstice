package com.discover.mobile.card.services.push;

import roboguice.inject.ContextSingleton;
import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.common.DiscoverApplication;
import com.discover.mobile.common.DiscoverEnvironment;
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

    private DiscoverApplication appCache = null;

    /**
     * Starts the Xtify SDK using the correct app key and the correct Google
     * Project ID specific to the environment
     * 
     * @param context
     *            - application context
     */
    public void start(final Context context) {
        appCache = (DiscoverApplication) context.getApplicationContext();
		final String xtifyAppKey = DiscoverEnvironment.getPushKey();
		final String googleProjectId = DiscoverEnvironment.getPushID();
        XtifySDK.start(appCache, xtifyAppKey, googleProjectId);
    }
}