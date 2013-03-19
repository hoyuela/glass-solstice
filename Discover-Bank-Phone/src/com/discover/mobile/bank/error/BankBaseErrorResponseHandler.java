package com.discover.mobile.bank.error;

import java.net.HttpURLConnection;

import android.app.Activity;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.paybills.SchedulePaymentFragment;
import com.discover.mobile.bank.services.auth.BankSchema;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.payment.CreatePaymentCall;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.error.bank.BankErrorCodes;
import com.discover.mobile.common.net.error.bank.BankErrorResponse;
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
	protected ErrorHandler mErrorHandler= null;
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
		mErrorHandler = errorHandlerUi.getErrorHandler();
	
	}
	
	/**
	 * This function is the callback used by a NetworkServiceCall<> to notify the application
	 * that an error response was received. This calls uses this callback to dispatch the
	 * error response to the appropriate handler.
	 */
	@Override
	public final boolean handleFailure(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
		boolean handled = false;
		
		if (errorResponse instanceof BankErrorResponse) {
			//Use JSON Error Response Handler if error response has a JSON body in it
			handled = handleJsonErrorCode((BankErrorResponse)errorResponse);
		} else {
			//Use HTTP Error Response Handler if error response has no JSON body in it
			handled = handleHTTPErrorCode(errorResponse.getHttpStatusCode(), errorResponse.getConnection());
		}
		
		//If it is a screen with inline errors then clear the text fields on an error
		if( DiscoverActivityManager.getActiveActivity() instanceof ErrorHandlerUi ) {
			final ErrorHandlerUi currentUi = (ErrorHandlerUi) DiscoverActivityManager.getActiveActivity();
			mErrorHandler.showErrorsOnScreen(currentUi, null);
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
	
		final String errCode = msgErrResponse.getErrorCode();

		if( !Strings.isNullOrEmpty(msgErrResponse.getErrorCode()) ) {
			//Login Errors
			if( errCode.equals(BankErrorCodes.ERROR_INVALID_LOGIN) ) {
				mErrorHandler.handleLoginAuthFailure(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LAST_ATTEMPT_LOGIN) ) {
				mErrorHandler.handleLoginAuthFailure(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_LOGIN_LOCKED)) {
				mErrorHandler.handleLockedOut(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			}else if (errCode.equals(BankErrorCodes.ERROR_FRAUD_USER) || errCode.equals(BankErrorCodes.ERROR_NO_ACCOUNTS_FOUND)){
				mErrorHandler.handleHttpFraudNotFoundUserErrorModal(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			}
			//Strong Auth Errors
			else if( errCode.equals(BankErrorCodes.ERROR_INVALID_STRONG_AUTH) || errCode.equals(BankErrorCodes.ERROR_LAST_ATTEMPT_STRONG_AUTH) ) {
				final BankStrongAuthDetails details = new BankStrongAuthDetails(msgErrResponse);
				
				final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

				BankConductor.navigateToStrongAuth(activeActivity, details, msgErrResponse.getErrorMessage());
				
			} else if( errCode.equals(BankErrorCodes.ERROR_LOCKED_STRONG_AUTH)) {
				mErrorHandler.handleLockedOut(mErrorHandlerUi, msgErrResponse.getErrorMessage());
			}
			//Maintenance Errors
			else if( errCode.equals(BankErrorCodes.ERROR_MAINTENANCE_PLANNED)) {
				mErrorHandler.handleHttpServiceUnavailableModal(msgErrResponse.getErrorMessage());
			} else if( errCode.equals(BankErrorCodes.ERROR_MAINTENANCE_UNPLANNED )) {
				mErrorHandler.handleHttpServiceUnavailableModal(msgErrResponse.getErrorMessage());
			//Error Handling for 422 Unprocessable Entity
			} else if( msgErrResponse.getHttpStatusCode() == BankHttpStatusCodes.HTTP_UNPROCESSABLE_ENTITY.getValue() &&
					   msgErrResponse.getHttpStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST ) {
				//Check if 422 and 400 could be meant for inline error handling, if it fails to handle them then send to generic handler
				final BankErrorHandler errorHandler = (BankErrorHandler)BankErrorHandler.getInstance();
				if( !errorHandler.handleUnprocessableEntity(msgErrResponse) ) {
					mErrorHandler.handleGenericError(msgErrResponse.getHttpStatusCode());
				}
			//Error handling for 403 Forbidden
			} else if( msgErrResponse.getHttpStatusCode() == HttpURLConnection.HTTP_FORBIDDEN ) {
				//Check if 403 is meant for check deposit functionality otherwise send to generic handler
				final BankErrorHandler errorHandler = (BankErrorHandler)BankErrorHandler.getInstance();
				if( !errorHandler.handleHttpForbidden(msgErrResponse) ) {
					mErrorHandler.handleGenericError(msgErrResponse.getHttpStatusCode());
				}
			} else {
				mErrorHandler.handleGenericError(msgErrResponse.getHttpStatusCode());
			}
		} else {
			mErrorHandler.handleGenericError(msgErrResponse.getHttpStatusCode());
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
	protected boolean handleHTTPErrorCode(final int httpErrorCode, final HttpURLConnection conn) {

		return handleHTTPErrorCode(httpErrorCode, conn, null);
	}
	
	/**
	 * Exposed as protected method in case of need to override by child class.
	 * 
	 * IF the calling class wants HTTP error codes suppressed, then they should
	 * override this method with a "return true"
	 * 
	 * @param messageErrorResponse
	 * @param sender
	 *            The sender so decisions can be made on a call-by-call basis.
	 *            Sender may come as null due to legacy code, null checks are
	 *            absolutely required!
	 * @return
	 */
	protected boolean handleHTTPErrorCode(final int httpErrorCode,
			final HttpURLConnection conn, final NetworkServiceCall<?> sender) {

		switch (httpErrorCode) {
		case HttpURLConnection.HTTP_UNAUTHORIZED:
			final String wwwAuthenticateValue = conn
					.getHeaderField(HttpHeaders.Authentication);

			if (!Strings.isNullOrEmpty(wwwAuthenticateValue)) {
				// Check if token expired
				if (wwwAuthenticateValue.contains(BankSchema.BANKAUTH)) {
					// Navigate back to home page
					mErrorHandler.handleSessionExpired();
				}
				// Check if strong auth challenge
				else if (wwwAuthenticateValue.contains(BankSchema.BANKSA)) {
					BankServiceCallFactory.createStrongAuthRequest().submit();
				}
				// Check if not authorized to view page
				else {
					// Display a modal and return to previous page
					mErrorHandler.handleGenericError(httpErrorCode);
				}
			} else {
				mErrorHandler.handleGenericError(httpErrorCode);
			}
			return true;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			mErrorHandler.handleHttpServiceUnavailableModal(null);
			return true;
		case HttpURLConnection.HTTP_FORBIDDEN:
			mErrorHandler.handleGenericError(httpErrorCode);
			return true;
		case HttpURLConnection.HTTP_CONFLICT:
			if(sender != null && sender instanceof CreatePaymentCall) {
				final Activity activeActivity = DiscoverActivityManager.getActiveActivity();
				final BaseFragment f = ((BankNavigationRootActivity)activeActivity).getCurrentContentFragment();
				if(f instanceof SchedulePaymentFragment) {
					((SchedulePaymentFragment)f).setDuplicatePaymentError(true);
				}
			}
			return true;
		default:
			mErrorHandler.handleGenericError(httpErrorCode);
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
