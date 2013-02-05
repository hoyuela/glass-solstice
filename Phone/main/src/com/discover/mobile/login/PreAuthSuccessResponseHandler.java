package com.discover.mobile.login;

import android.util.Log;

import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.NetworkServiceCall;
/**
 * Handles the success response from a PreAuth call.
 * Will present optional upgrade message to the user if the server sees that the application
 * is not up to date.
 * 
 * @author scottseward
 *
 */
public class PreAuthSuccessResponseHandler extends PreAuthCallHelper implements SuccessListener<PreAuthResult>{
	LoginActivity loginActivity;

	private final static String TAG = "PreAuthSuccess";
	
	public PreAuthSuccessResponseHandler(final LoginActivity loginActivity) {
		this.loginActivity = loginActivity;
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
	public void success(final NetworkServiceCall<?> sender, final PreAuthResult value) {
		//Verify login is a valid reference
		if( null != loginActivity) {		
			if( Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "Pre-auth status code: " + value.statusCode);
			}
			if(PreAuthCallHelper.shouldPresentOptionalUpdate(loginActivity,value.upgradeDescription)) {
				TrackingHelper.trackPageView(AnalyticsPage.OPTIONAL_UPGRADE);
				PreAuthCallHelper.showOptionalUpgradeAlertDialog(loginActivity, value.upgradeDescription);
			} 	
			
			//Notify login activity that Pre-Auth call has completed
			loginActivity.preAuthComplete(true);
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "LoginActivity reference is invalid");
			}
		}
	}
	

}
