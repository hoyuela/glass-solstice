package com.discover.mobile.push.register;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * Error handler for the call to the server that registers the device and the vendor id (Xtify xid) to the current
 * user.  If this call errors out then the application should forward the user to the push manage screen.
 * 
 * @author jthornton
 *
 */
public class PushRegisterErrorHandler implements ErrorResponseHandler{
	
	/**Local instance of the fragment making this call (used to swap the fragment out)*/
	private PushRegistrationUI fragment;
	
	/**Boolean set if the user is opting into the push alerts*/
	private final boolean isOptedIn;
	
	/**
	 * Constructor for the class, letting the handler know the fragment using it
	 * @param fragment - fragment using this handler
	 * @param isOptedIn - true if the user is opting into push alerts
	 */
	public PushRegisterErrorHandler(final PushRegistrationUI fragment, final boolean isOptedIn){
		this.fragment = fragment;
		this.isOptedIn = isOptedIn;
	}

	/**
	 * Set when the handler should be executed
	 * @return when the handler should be executed
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle when the call fails, in this case send it to the next screen
	 * @return true if the error is handled
	 */
	@Override
	public boolean handleFailure(final ErrorResponse<?> arg0) {
		if(isOptedIn)
			fragment.changeToAcceptScreen();
		else
			fragment.changeToDeclineScreen();
		return true;
	}
}
