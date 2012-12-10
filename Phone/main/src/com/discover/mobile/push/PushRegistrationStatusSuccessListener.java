package com.discover.mobile.push;

import android.content.Context;
import android.content.Intent;

import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail.VidStatus;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * This is the success listener for when the app tries to get information from Discover's server
 * about the vendor id (xid - XTIFY) and the current user.  If the call is successful then the
 * app needs to go on different navigation paths.
 * 
 * @author jthornton
 *
 */
public class PushRegistrationStatusSuccessListener implements SuccessListener<PushRegistrationStatusDetail>{
	
	/**Activity context*/
	private Context context;
	
	/**
	 * Constructor that takes in a context so that it can manipulate the flow of the app.
	 * @param context - activity context
	 */
	public PushRegistrationStatusSuccessListener(final Context context){
		this.context = context;
	}

	/**
	 * Set the priority level of the success handler
	 * @return CallbackPriority - the priority of the callback
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.LAST;
	}

	/**
	 * Send the app on the correct path when the call is successful
	 * @param value - the returning push registration detail from the server
	 */
	@Override
	public void success(final PushRegistrationStatusDetail value) {
		Intent intent;
		
		if(value.vidStatus == VidStatus.MISSING){
			CurrentSessionDetails.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
			intent = new Intent(context, NavigationRootActivity.class);	
			
		}else if(value.vidStatus == VidStatus.NOT_ASSOCIATED){
			CurrentSessionDetails.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(true);
			intent = new Intent(context, NavigationRootActivity.class);	

		}else if(value.vidStatus == VidStatus.ASSOCIATED){
			CurrentSessionDetails.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
			intent = new Intent(context, NavigationRootActivity.class);
		
		}else{
			intent = new Intent(context, NavigationRootActivity.class);

		}
		context.startActivity(intent);
		context = null;
	}

}
