package com.discover.mobile.error;

import java.net.HttpURLConnection;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.common.auth.bank.BankErrorCodes;
import com.discover.mobile.common.auth.bank.BankErrorResponse;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.google.common.base.Strings;

/**
 * This class is added to an instance of GenericAsyncCallback<> when built specifically for a Bank related
 * NetworkServiceCall<>. This class is used as a dispatcher of NetworkServiceCall error response events.
 * It relays the error response to the appropriate error handler based on the response's HTTP status code 
 * and BankErrorResponse error code.
 * 
 * @author henryoyuela
 *
 */
public final class BankBaseErrorResponseHandler implements ErrorResponseHandler {
	/**
	 * Contains a reference to an ErrorHandlerFactory to 
	 */
	protected ErrorHandlerFactory mErrorHandlerFactory = null;
	protected ErrorHandlerUi mErrorHandlerUi = null;
	/**
	 * Default constructor should not be used
	 */
	@SuppressWarnings("unused")
	private BankBaseErrorResponseHandler() {
		
	}
	/**
	 * Constructor used to initialized an instance of the BankBaseErrorResponseHandler.
	 * 
	 * @param errorHandlerUi Reference to an instance of ErrorHandlerUi in order to make changes to the UI
	 * 						 when an error occurs. An activity that makes a network service call is expected
	 * 						 to implement this interface in order to handle the error response.
	 */
	public BankBaseErrorResponseHandler(final ErrorHandlerUi errorHandlerUi) {
		mErrorHandlerUi = errorHandlerUi;
		mErrorHandlerFactory = errorHandlerUi.getErrorHandlerFactory();
	
	}
	
	/**
	 * This function is the callback used by a NetworkServiceCall<> to notify the application
	 * that an error response was received. This calls uses this callback to dispatch the
	 * error response to the appropriate handler.
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
	 * @param messageErrorResponse
	 * @return
	 */
	protected boolean handleJsonErrorCode(final BankErrorResponse msgErrResponse) {	
		boolean handled = false;
		
		String errCode = msgErrResponse.getErrorCode();
		String strongQuestion = msgErrResponse.getDataValue("challengeQuestion");
		String strongQuestionId = msgErrResponse.getDataValue("challengeQuestionId");
		
		
		if( !Strings.isNullOrEmpty(msgErrResponse.getErrorCode()) ) {
			//Login Errors
			if( errCode.equals(BankErrorCodes.ERROR_INVALID_LOGIN) ) {
				mErrorHandlerFactory.handleLoginAuthFailure(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LAST_ATTEMPT_LOGIN) ) {
				mErrorHandlerFactory.handleLoginAuthFailure(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LOGIN_LOCKED)) {
				mErrorHandlerFactory.handleLockedOut(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			} 
			//Strong Auth Errors
			else if( errCode.equals(BankErrorCodes.ERROR_INVALID_STRONG_AUTH) || errCode.equals(BankErrorCodes.ERROR_LAST_ATTEMPT_STRONG_AUTH) ) {
				mErrorHandlerFactory.handleStrongAuthFailure(mErrorHandlerUi, msgErrResponse.getErrorMessage(), strongQuestion, strongQuestionId);
			} else if( errCode.equals(BankErrorCodes.ERROR_LOCKED_STRONG_AUTH)) {
				mErrorHandlerFactory.handleLockedOut(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			}
			//Maintenance Errors
			else if( errCode.equals(BankErrorCodes.ERROR_MAINTENANCE_PLANNED)) {
				mErrorHandlerFactory.handleHttpServiceUnavailableModal(msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_MAINTENANCE_UNPLANNED )) {
				mErrorHandlerFactory.handleHttpServiceUnavailableModal(msgErrResponse.getErrorMessage());
			}
		} else {
			mErrorHandlerFactory.handleGenericError(msgErrResponse.getHttpStatusCode());
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
			mErrorHandlerFactory.handleHttpUnauthorizedError();
			return true;
		case HttpURLConnection.HTTP_INTERNAL_ERROR:
			mErrorHandlerFactory.handleHttpInternalServerErrorModal();
			return true;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			mErrorHandlerFactory.handleHttpServiceUnavailableModal(null);
			return true;
		case HttpURLConnection.HTTP_FORBIDDEN:
			mErrorHandlerFactory.handleGenericError(httpErrorCode);
			return true;
		default:
			mErrorHandlerFactory.handleGenericError(httpErrorCode);
			return true;
		}
	}

	/**
	 * An AsyncCallback may have several ErrorResponseHandler. This function serves as a callback
	 * to the AsyncCallback it is associated with in order to determine the priority of this handler 
	 * among the other ErrorResponseHandler in its list. AsyncCallback will use the CallbackPriority
	 * value to determine when it should call handleFailure() for an instance of this class.
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}


}
