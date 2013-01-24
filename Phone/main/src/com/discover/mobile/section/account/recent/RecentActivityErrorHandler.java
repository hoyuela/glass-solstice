package com.discover.mobile.section.account.recent;

import android.util.Log;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.common.StandardErrorCodes;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.error.CardBaseErrorResponseHandler;
import com.discover.mobile.section.account.summary.LatePaymentErrorHandler;

public class RecentActivityErrorHandler extends CardBaseErrorResponseHandler{
	
	/**TAG used for labeling class for errors*/
	private static final String TAG = LatePaymentErrorHandler.class.getSimpleName();
	
	/**Fragment to return the successful nature of the call*/
	private AccountRecentActivityFragment fragment;

	public RecentActivityErrorHandler(final AccountRecentActivityFragment fragment) {
		super((ErrorHandlerUi)fragment.getActivity());
		this.fragment = fragment;
	}
	
	/**
	 * Get the callback priority of the success handler
	 * @return the callback priority of the success handler
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
		
		switch(statusCode){
			case StandardErrorCodes.UNAUTHORIZED:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.INVALID_FORMAT:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.FORBIDDEN:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.INTERNAL_SERVER_ERROR:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.SERVICE_UNAVAILABLE:
				//fragment.showLatePaymentModal(false);
				break;
		}
		return true;
	}
	
	/**
	 * Exposed as protected method in case of need to override by
	 * child class.
	 * 
	 * IF the calling class wants HTTP error codes suppressed, then
	 * they should override this method with a "return true" 
	 * 
	 * @param messageErrorResponse
	 * @return
	 */
	@Override
	protected boolean handleHTTPErrorCode(final int httpErrorCode) {
		switch (httpErrorCode) {
			case StandardErrorCodes.UNAUTHORIZED:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.INVALID_FORMAT:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.FORBIDDEN:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.INTERNAL_SERVER_ERROR:
				//fragment.showLatePaymentModal(false);
				break;
			case StandardErrorCodes.SERVICE_UNAVAILABLE:
				//fragment.showLatePaymentModal(false);
				break;
		}
		return true;
	}
}
