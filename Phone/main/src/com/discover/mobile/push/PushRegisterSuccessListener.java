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
	private PushNowAvailableFragment fragment;
	
	/**
	 * Constructor for the class, letting the listener know the fragment using it
	 * @param fragment - fragment using this listener
	 */
	public PushRegisterSuccessListener(final PushNowAvailableFragment fragment){
		this.fragment = fragment;
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
	public void success(DeviceRegistrationDetail detail) {
		fragment.changeToPushManageScreen();
	}
}
