package com.discover.mobile.bank.login;

import static com.discover.mobile.common.StandardErrorCodes.FORCED_UPGRADE_REQUIRED;

import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * PreAuthErrorResponseHandler handles server error messages received from the
 * PreAuth call. Its primary goal is to display a forced upgrade message or
 * navigate the user to a locked out page if the servers are down for any
 * reason.
 * 
 * @author scottseward
 * 
 */
public class PreAuthErrorResponseHandler extends CardBaseErrorResponseHandler implements ErrorResponseHandler {

	/**
	 * Create a PreAuthErrorResponseHandler to handle error responses generated
	 * 
	 * @param loginActivity
	 *            Pass the calling Activity activity to this handler.
	 */
	public PreAuthErrorResponseHandler(final LoginActivity loginActivity) {
		super(loginActivity);

	}

	
	/**
	 * Notifies Login Activity that Pre-Authentication failed with an HTTP error response.
	 */
	@Override
	protected boolean handleHTTPErrorCode(final int httpErrorCode)  {		
		final LoginActivity loginActivity = (LoginActivity)getErrorFieldUi();
		
		if( null != loginActivity ) {
			//Notify login activity that Pre-Auth call has completed
			loginActivity.preAuthComplete(false);
		}
		
		return true;
	}
	
	/**
	 * Show them a force upgrade dialog if applicable
	 * 
	 * @param messageErrorResponse
	 * @return returns true if the given error can be handled here, false
	 *         otherwise.
	 */
	@Override
	protected boolean handleJsonErrorCode(final JsonMessageErrorResponse messageErrorResponse) {
		if (messageErrorResponse.getMessageStatusCode() == FORCED_UPGRADE_REQUIRED) {
			TrackingHelper.trackPageView(AnalyticsPage.FORCED_UPGRADE);
			PreAuthCallHelper.showForcedUpgradeAlertDialog(getUi());
			PreAuthCallHelper.updateDateInPrefs(getUi().getContext());
			return true;
		}

		final LoginActivity loginActivity = (LoginActivity)getErrorFieldUi();
		
		if( null != loginActivity ) {
			//Notify login activity that Pre-Auth call has completed
			loginActivity.preAuthComplete(true);
		}
		
		return false;
	}

}
