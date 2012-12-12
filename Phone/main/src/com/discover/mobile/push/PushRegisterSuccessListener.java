package com.discover.mobile.push;

import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.registration.DeviceRegistrationDetail;

/**
 * Success listener for the call to the server that registers the device and the vendor id (Xtify xid) to the current
 * user.  If this call is successful then the application should forward the user to the push manage screen.
 * 
 * @author jthornton
 *
 */
public class PushRegisterSuccessListener implements SuccessListener<DeviceRegistrationDetail>{
	
	/**Local instance of the fragment making this call (used to swap the fragment out)*/
	private final PushRegistrationUI fragment;
	
	/**Boolean set if the user is opting into the push alerts*/
	private final boolean isOptedIn;
	
	/**
	 * Constructor for the class, letting the listener know the fragment using it
	 * @param fragment - fragment using this listener
	 * @param isOptedIn - true is the user is opting into push alerts
	 */
	public PushRegisterSuccessListener(final PushRegistrationUI fragment, final boolean isOptedIn){
		this.fragment = fragment;
		this.isOptedIn = isOptedIn;
	}

	/**
	 * Set when the listener should be executed
	 * @return when the listener should be executed
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle when the call is successful, in this case send it to the next screen
	 */
	@Override
	public void success(final DeviceRegistrationDetail detail) {
		if(isOptedIn)
			fragment.changeToAcceptScreen();
		else
			fragment.changeToDeclineScreen();
	}
}
