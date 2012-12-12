package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.FORCED_UPGRADE_REQUIRED;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_1;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_2;
import android.app.Activity;

import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
/**
 * PreAuthErrorResponseHandler handles server error messages received from the PreAuth call.
 * Its primary goal is to display a forced upgrade message or navigate the user to a locked out
 * page if the servers are down for any reason.
 * 
 * @author scottseward
 *
 */
public class PreAuthErrorResponseHandler extends AbstractPreAuthCallHandler implements ErrorResponseHandler{
	/**
	 * Create a PreAuthErrorResponseHandler to handle error responses generated
	 * 
	 * @param activity Pass the calling Activity activity to this handler.
	 */
	public PreAuthErrorResponseHandler(final Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}
	
	@Override
	public boolean handleFailure(final ErrorResponse<?> errorResponse) {
		if(errorResponse instanceof JsonMessageErrorResponse)
			return handleMessageErrorResponse((JsonMessageErrorResponse)errorResponse);
		
		return false;
	}
	
	/**
	 * Navigate the user to either the maintenance page, or show them a force upgrade dialog.
	 * 
	 * @param messageErrorResponse
	 * @return
	 */
	public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
		// FIXME named constants
		switch(messageErrorResponse.getMessageStatusCode()) {
			case FORCED_UPGRADE_REQUIRED: 
				TrackingHelper.trackPageView(AnalyticsPage.FORCED_UPGRADE);
				showForcedUpgradeAlertDialog();
				updateDateInPrefs();
				return true;
				
			case MAINTENANCE_MODE_1:
			case MAINTENANCE_MODE_2: 
				sendToMaintenancePage();
				return true;
			default:
				break;
		}
		
		return false;
	}
	

}
