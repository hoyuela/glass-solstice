package com.discover.mobile.login;

import android.util.Log;

import com.discover.mobile.RoboSlidingFragmentActivity;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
/**
 * Handles the success response from a PreAuth call.
 * Will present optional upgrade message to the user if the server sees that the application
 * is not up to date.
 * 
 * @author scottseward
 *
 */
public class PreAuthSuccessResponseHandler extends PreAuthCallHelper implements SuccessListener<PreAuthResult>{
	
	RoboSlidingFragmentActivity activity;

	private final static String TAG = PreAuthErrorResponseHandler.class.getSimpleName();
	
	public PreAuthSuccessResponseHandler(final RoboSlidingFragmentActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Shows the optional upgrade message if it has never been shown before
	 * or has been 30 days or more since it was last shown.
	 * 
	 */
	@Override
	public void success(final PreAuthResult value) {
		Log.d(TAG, "Pre-auth status code: " + value.statusCode);
		if(PreAuthCallHelper.shouldPresentOptionalUpdate(activity,value.upgradeDescription)) {
			TrackingHelper.trackPageView(AnalyticsPage.OPTIONAL_UPGRADE);
			PreAuthCallHelper.showOptionalUpgradeAlertDialog(activity,value.upgradeDescription);
		} 		
	}
	

}
