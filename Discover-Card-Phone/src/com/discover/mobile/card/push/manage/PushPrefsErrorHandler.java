package com.discover.mobile.card.push.manage;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardBaseErrorResponseHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * Error Handler for the posting of the preferences after the user had decided to change their current settings. 
 * @author jthornton
 *
 */
public class PushPrefsErrorHandler extends CardBaseErrorResponseHandler{

	/**Tag labeling the class for errors*/
	private static final String TAG = PushPrefsErrorHandler.class.getSimpleName();

	/**
	 * Constructor for the class
	 */
	public PushPrefsErrorHandler(final ErrorHandlerUi errorHandlerUi){
		super(errorHandlerUi);
	}
	
	/**
	 * Set the priority of the handler
	 * @return the priority of the handler
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Handle the error response
	 * @error - error response from the server
	 * @return true if the error was handled
	 */
	@Override
	public boolean handleJsonErrorCode(final JsonMessageErrorResponse error) {
		Utils.log(TAG, Integer.toString(error.getHttpStatusCode()));
		showModalErrorDialog(R.string.error_generic_title, 
							 R.string.error_generic_content, 
							 R.string.error_generic_button_text);
		return true;
	}

}
