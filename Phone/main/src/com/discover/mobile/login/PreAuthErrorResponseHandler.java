package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.FORCED_UPGRADE_REQUIRED;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.StandardErrorCodes.UNSCHEDULED_MAINTENANCE;
import android.app.Activity;
import android.content.Intent;

import com.discover.mobile.common.ScreenType;
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
				
			case UNSCHEDULED_MAINTENANCE:
				sendToErrorPage(ScreenType.UNSCHEDULED_MAINTENANCE);
				return true;
			
			case SCHEDULED_MAINTENANCE:
				sendToErrorPage(ScreenType.SCHEDULED_MAINTENANCE);
				return true;
				
			default:
				break;
		}
		
		return false;
	}
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
	}

}
