package com.discover.mobile.push;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.BaseErrorResponseHandler;

/**
 * Error handler for the call to the server that registers the device and the
 * vendor id (Xtify xid) to the current user. If this call errors out then the
 * application should forward the user to the push manage screen.
 * 
 * @author jthornton
 * 
 */
public class PushRegisterErrorHandler extends BaseErrorResponseHandler {

	/**
	 * Local instance of the fragment making this call (used to swap the
	 * fragment out)
	 */
	private PushRegistrationUI fragment;

	/** Boolean set if the user is opting into the push alerts */
	private final boolean isOptedIn;

	/**
	 * Constructor for the class, letting the handler know the fragment using it
	 * 
	 * @param fragment
	 *            - fragment using this handler
	 * @param isOptedIn
	 *            - true if the user is opting into push alerts
	 */
	public PushRegisterErrorHandler(final ErrorHandlerUi errorHandlerUi, final PushRegistrationUI fragment, final boolean isOptedIn) {
		super(errorHandlerUi);
		this.fragment = fragment;
		this.isOptedIn = isOptedIn;
		
	}

	/**
	 * Handle when the call fails, in this case send it to the next screen
	 * 
	 * FIXME don't we want to report failure or take action here? 
	 * 
	 * @return true if the error is handled
	 */
	@Override
	protected boolean handleJsonErrorCode(JsonMessageErrorResponse messageErrorResponse) {
		if (isOptedIn)
			fragment.changeToAcceptScreen();
		else
			fragment.changeToDeclineScreen();
		return true;
	}

	
}
