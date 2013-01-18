package com.discover.mobile.section.account;

import android.util.Log;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.R;
import com.discover.mobile.common.StandardErrorCodes;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.BaseErrorResponseHandler;

public class BaseAccountErrorResponseHandler extends BaseErrorResponseHandler{

	/**TAG used for labeling class for errors*/
	private static final String TAG = BaseAccountErrorResponseHandler.class.getSimpleName();

	public BaseAccountErrorResponseHandler(final ErrorHandlerUi ui){
		super(ui);
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
		final int statusCode = error.getHttpStatusCode();
		Log.e(TAG, Integer.toString(statusCode));
		
		
		//TODO: Handle these
		switch(statusCode){
			case StandardErrorCodes.UNAUTHORIZED:
				showModalErrorDialog(R.string.error_generic_title, 
						R.string.error_generic_content, 
						R.string.error_generic_button_text);
				break;
			case StandardErrorCodes.INVALID_FORMAT:
				showModalErrorDialog(R.string.error_generic_title, 
						R.string.error_generic_content, 
						R.string.error_generic_button_text);
				break;
			case StandardErrorCodes.FORBIDDEN:
				showModalErrorDialog(R.string.error_generic_title, 
						R.string.error_generic_content, 
						R.string.error_generic_button_text);
				break;
			case StandardErrorCodes.INTERNAL_SERVER_ERROR:
				showModalErrorDialog(R.string.error_generic_title, 
						R.string.error_generic_content, 
						R.string.error_generic_button_text);
				break;
			case StandardErrorCodes.SERVICE_UNAVAILABLE:
				showModalErrorDialog(R.string.error_generic_title, 
						R.string.error_generic_content, 
						R.string.error_generic_button_text);
				break;
		}
		
		return true;
	}
}
