package com.discover.mobile.error;

import java.net.HttpURLConnection;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.common.auth.bank.BankErrorCodes;
import com.discover.mobile.common.auth.bank.BankErrorResponse;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.google.common.base.Strings;


public final class BankBaseErrorResponseHandler implements ErrorResponseHandler {
	protected ErrorHandlerFactory mErrorHandlerFactory = null;

	public BankBaseErrorResponseHandler(ErrorHandlerUi errorHandlerUi) {
		mErrorHandlerFactory = errorHandlerUi.getErrorHandlerFactory();
	
	}
	
	/**
	 * Expectation is that all common error codes are handled here.
	 * 
	 * The extended ErrorHandler class will handle any network specific call
	 * error codes coming back
	 */
	@Override
	public final boolean handleFailure(final ErrorResponse<?> errorResponse) {
		boolean handled = false;
		if (errorResponse instanceof BankErrorResponse) {
			//Use JSON Error Response Handler if error response has a JSON body in it
			handled = handleJsonErrorCode((BankErrorResponse)errorResponse);
		} else {
			//Use HTTP Error Response Handler if error response has no JSON body in it
			handled = handleHTTPErrorCode(errorResponse.getHttpStatusCode());
		}
		return handled;
	}

	/**
	 * For the child class to implement for specific handling of json error
	 * codes
	 * 
	 * OVERRIDE ME TO HANDLE CUSTOM JSON RESPONSE CODES ---
	 * 
	 * 
	 * @param messageErrorResponse
	 * @return
	 */
	protected boolean handleJsonErrorCode(final BankErrorResponse msgErrResponse) {	
		boolean handled = false;
		
		String errCode = msgErrResponse.getErrorCode();
		
		if( !Strings.isNullOrEmpty(msgErrResponse.getErrorCode()) ) {
			//Login Errors
			if( errCode.equals(BankErrorCodes.ERROR_INVALID_LOGIN) ) {
				mErrorHandlerFactory.handleLoginOrStrongAuthFailure(msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LAST_ATTEMPT_LOGIN) ) {
				mErrorHandlerFactory.handleLoginOrStrongAuthFailure(msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LOGIN_LOCKED)) {
				//Show a modal for locked out
				mErrorHandlerFactory.handleLockedOut();
			} 
			//Strong Auth Errors
			else if( errCode.equals(BankErrorCodes.ERROR_INVALID_STRONG_AUTH) ) {
				mErrorHandlerFactory.handleLoginOrStrongAuthFailure(msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LAST_ATTEMPT_STRONG_AUTH)) {
				mErrorHandlerFactory.handleLoginOrStrongAuthFailure(msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LOCKED_STRONG_AUTH)) {
				//Show a modal for locked out
				mErrorHandlerFactory.handleLockedOut();
			}
			//Maintenance Errors
			else if( errCode.equals(BankErrorCodes.ERROR_MAINTENANCE_PLANNED)) {
				mErrorHandlerFactory.handleHttpUnavailableErrorModal(true);
			} else if( errCode.equals(BankErrorCodes.ERROR_MAINTENANCE_UNPLANNED )) {
				mErrorHandlerFactory.handleHttpUnavailableErrorModal(false);
			}
		} else {
			mErrorHandlerFactory.handleGenericErrorModal(msgErrResponse.getHttpStatusCode());
			handled = true;
		}
			
		return handled;
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
	protected boolean handleHTTPErrorCode(final int httpErrorCode) {

		switch (httpErrorCode) {
		case HttpURLConnection.HTTP_UNAUTHORIZED:
			mErrorHandlerFactory.handleHttpUnauthorized();
			return true;
		case HttpURLConnection.HTTP_INTERNAL_ERROR:
			mErrorHandlerFactory.handleHttpInternalServerErrorModal();
			return true;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			mErrorHandlerFactory.handleHttpUnavailableErrorModal(false);
			return true;
		case HttpURLConnection.HTTP_FORBIDDEN:
			mErrorHandlerFactory.handleHttpForbiddenErrorModal();
			return true;
		default:
			mErrorHandlerFactory.handleGenericErrorModal(httpErrorCode);
			return true;
		}
	}

	/**
	 * Can override this if you need another priority: MIDDLE
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}


}
