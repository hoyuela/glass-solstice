package com.discover.mobile.common.logout;

import android.app.Activity;
import android.os.Bundle;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * Success listener for the log out call.
 * 
 * @author jthornton
 *
 */
public class LogOutSuccessFailListener implements SuccessListener<Object>, ErrorResponseHandler{
	
	/**Activity running*/
	private final Activity activity;
	
	/**
	 * Constructor for the class
	 * @param activity - the current activity running
	 */
	public LogOutSuccessFailListener(final Activity activity){
		this.activity = activity;
	}

	/**
	 * Get the priority of the call
	 * @ return the priority of the call
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the success of the call.  In this case send the user to the login screen with a 
	 * special intent to the logout message is shown.
	 * @param successObject - object retrieved from the success call
	 */
	@Override
	public void success(final NetworkServiceCall<?> sender, final Object successObject) {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
		bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
		FacadeFactory.getLoginFacade().navToLoginWithMessage(activity, bundle);
	}

	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender, final ErrorResponse<?> arg0) {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
		bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
		FacadeFactory.getLoginFacade().navToLoginWithMessage(activity, bundle);
		return false;
	}
}
