package com.discover.mobile.error;

import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_NUMBER_CHANGED;
import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_NUMBER_REREGISTERED;
import static com.discover.mobile.common.StandardErrorCodes.ACCOUNT_SETUP_PENDING;
import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.NO_DATA_FOUND;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.StandardErrorCodes.UNSCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;

import java.net.HttpURLConnection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.R;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

/**
 * A base class for error response handling.
 * 
 * All error response handler with custom actions should extend this class.
 * 
 * Extending is not required; if a class needs no custom JSON response
 * code handling then this class would be adequate
 * 
 * @author ekaram
 * 
 */
public class CardBaseErrorResponseHandler implements ErrorResponseHandler {

	/**
	 * The Parent RoboSlidingFragmentActvitiy that made the service call
	 */
	protected ErrorHandlerUi errorHandlerUi = null;
	
	protected ErrorHandlerFactory mErrorHandlerFactory = null;
	/**
	 * Private constructor to prevent construction without a fragment or
	 * activity
	 */
	public CardBaseErrorResponseHandler(final ErrorHandlerUi errorHandlerUi) {
		this.errorHandlerUi = errorHandlerUi;
		mErrorHandlerFactory = errorHandlerUi.getErrorHandlerFactory();
	}

	/**
	 * Private constructor to prevent construction without a fragment or
	 * activity
	 */
	@SuppressWarnings("unused")
	private CardBaseErrorResponseHandler() {
		throw new RuntimeException("invalid constructor");
	}

	/**
	 * Can override this if you need another priority: MIDDLE
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 * Expectation is that all common error codes are handled here.
	 * 
	 * The extended ErrorHandler class will handle any network specific call
	 * error codes coming back
	 */
	@Override
	public final boolean handleFailure(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {

		String errorHandlingFailureMessage;
		final Resources resources = errorHandlerUi.getContext().getResources();
		
		if (errorResponse instanceof JsonMessageErrorResponse) {

			final JsonMessageErrorResponse messageErrorResponse = (JsonMessageErrorResponse) errorResponse;
			
			// FIRST we will try for common status code generic handling
			switch (messageErrorResponse.getMessageStatusCode()) {
				case UNSCHEDULED_MAINTENANCE:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.temporary_outage));
					return true;
	
				case SCHEDULED_MAINTENANCE:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.planned_outage_one));
					return true;
	
				case PLANNED_OUTAGE:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.planned_outage_one));
					return true;
	
				case NO_DATA_FOUND:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.no_data_found));
					return true;
				
				case EXCEEDED_LOGIN_ATTEMPTS:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.locked_account));
					return true;
					
				case LAST_ATTEMPT_WARNING:
					setErrorText(R.string.login_attempt_warning);
					return true;
					
				case STRONG_AUTH_NOT_ENROLLED:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.account_security_not_enrolled));
					return true;
				
				case AUTH_BAD_ACCOUNT_STATUS:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.zluba_error));
					return true;
				
				case ACCOUNT_NUMBER_REREGISTERED:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.account_number_reregistered));
					return true;
					
				case LOCKED_OUT_ACCOUNT:
					TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LOCKED);
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.locked_account));
					return true;
					
				case ACCOUNT_SETUP_PENDING:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.account_setup_pending));
					return true;
				
				case ACCOUNT_NUMBER_CHANGED:
					mErrorHandlerFactory.handleLockedOut(errorHandlerUi, resources.getString(R.string.account_number_changed));
					return true;
					
			}
			// SECOND we try the JSON specific error code handling
			if (handleJsonErrorCode(messageErrorResponse)) {
				return true;
			}
			
			// THIRD - If we get to this block of code, the JSON error code has
			// not been handled properly. We alert the developer to let them
			// know they have a condition to handle in the child class
			errorHandlingFailureMessage = "ERROR - UNHANDLED JSON ERROR CODE: " + messageErrorResponse.getMessageStatusCode()
					+ " : " + messageErrorResponse.getMessage();

		} else {
			if (handleHTTPErrorCode(errorResponse.getHttpStatusCode())) {
				return true;
			}
			// If we get to this block of code, the HTTP error code has not been
			// handled properly. We alert the developer to let them know they
			// have a condition to handle in the base class
			errorHandlingFailureMessage = "ERROR - UNHANDLED HTTP ERROR CODE: " + errorResponse.getHttpStatusCode();
		}
		//TODO: This is for development use only; don't want to push this line into production
		showDynamicModalErrorDialog(R.string.generic_error_title, errorHandlingFailureMessage, R.string.ok);
		return false;
	}

	/**
	 * Set the input fields to be highlighted in red.
	 */
	protected void setInputFieldsDrawableToRed() {
		final ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
			for (final EditText text : errorHandlerUi.getInputFields()) {
				text.setBackgroundResource(R.drawable.edit_text_red);
			}
		}
	}

	/**
	 * Clears the activity edit texts in the getFieldsToClearAfterError()
	 * interface method
	 */
	protected void clearInputs() {

		final ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
			for (final EditText text : errorHandlerUi.getInputFields()) {
				text.setText("");
			}
		}

	}
	
	/**
	 * Sets the last error code for the ErrorHandlerUi instance referenced
	 * 
	 * @param errorCode Status Code from the response received
	 */
	protected void setLastError(final int errorCode) {
		final ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null ) {
			errorHandlerUi.setLastError(errorCode);
		}
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
	protected boolean handleJsonErrorCode(final JsonMessageErrorResponse messageErrorResponse) {
		
		return false;
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
			setErrorText(R.string.login_error);
			setInputFieldsDrawableToRed();
			clearInputs();
			return true;

		case HttpURLConnection.HTTP_INTERNAL_ERROR:
			sendToErrorPage(R.string.internal_server_error_500);
			return true;

		case HttpURLConnection.HTTP_UNAVAILABLE:
			sendToErrorPage(httpErrorCode, R.string.error_503_title, R.string.internal_server_error_503);
			return true;

		case HttpURLConnection.HTTP_FORBIDDEN:
			sendToErrorPage(R.string.forbidden_403);
			return true;
		}
		return false;
	}

	

	/**
	 * A common method used to display a modal dialog with an error message
	 * 
	 * @param stringResource
	 */
	protected void showModalErrorDialog(final int title,final int content,final int buttonText) {
		getErrorFieldUi().showOneButtonAlert(title, content, buttonText);
	}

	/**
	 * A common method used to display a modal dialog with an error message
	 * 
	 * @param stringResource
	 */
	protected void showDynamicModalErrorDialog(final int title,final String content,final int buttonText) {
		getErrorFieldUi().showDynamicOneButtonAlert(title, content, buttonText);
	}

	/**
	 * A common method to display an error message on the error field and make
	 * the error label visible.
	 * 
	 * The activity needs to implement ErrorFieldActivity for this funtionality
	 * to work.
	 * 
	 * @param text
	 * @param errorText
	 */
	protected void setErrorText(final int errorText) {
		final ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null) {
			errorHandlerUi.getErrorLabel().setText(((Context) errorHandlerUi).getResources().getString(errorText));
			errorHandlerUi.getErrorLabel().setVisibility(View.VISIBLE);
		}
	}

	/**
	 * A common method to display DYNAMIC error messages on the error field
	 * 
	 * DO NOT USE THIS FOR STATIC, PLEASE USE THE INT RESOURCE METHOD
	 * 
	 * The activity needs to implement ErrorFieldActivity for this functionality
	 * to work.
	 * 
	 * @param text
	 * @param errorText
	 */
	protected void setDynamicErrorText(final String message) {
		final ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null) {
			errorHandlerUi.getErrorLabel().setText(message);
			errorHandlerUi.getErrorLabel().setVisibility(View.VISIBLE);
		}
	}

	/**
	 * If an error field activity, we can do more .. like display error text
	 * highlight fields red, etc.
	 * 
	 * Returns the activity, fragment if they implement the ErrorHandlerUI
	 * Otherwise returns null;
	 * 
	 * @return
	 */
	protected ErrorHandlerUi getErrorFieldUi() {
		return errorHandlerUi;
	}


	/**
     * Show a custom modal alert dialog for the activity
     * @param alert - the modal alert to be shown
     */
    public void showCustomAlert(final AlertDialog alert){
    	alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    
    /**
     * Show the default one-button alert with a custom title, content an button text
     * 
     * Uses the orange button
     * 
     * @param title - the resource id for title for the alert
     * @param content - the resource id for content to display on the box
     * @param buttonText - the resource id for button text to display on the button
     */
    public void showOneButtonAlert(final int title,final int content,final int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(getErrorFieldUi().getContext(),title,content,buttonText));
    }
    
    /**
     * Show the default one-button alert with a custom title, content an button text
     * 
     * Uses the orange button
     * 
     * @param title - the resource id for title for the alert
     * @param content - the resource id for content to display on the box
     * @param buttonText - the resource id for button text to display on the button
     */
    public void showDynamicOneButtonAlert(final int title,final String content,final int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(getErrorFieldUi().getContext(),title,content,buttonText));
    }
    
    
    /**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	protected void sendToErrorPage(final int titleText,final int errorText) {
		final Context context = getErrorFieldUi().getContext();
		final ErrorHandlerFactory factory = ErrorHandlerFactory.getInstance();
		final AlertDialog dialog = factory.createErrorModal(context.getString(titleText), context.getString(errorText));
		ErrorHandlerFactory.showCustomAlert(dialog);
	}
	
    /**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	protected void sendToErrorPage(final int errorCode, final int titleText,final int errorText) {
		final ErrorHandlerFactory factory = ErrorHandlerFactory.getInstance();
		final AlertDialog dialog = factory.createErrorModal(errorCode, titleText, errorText);
		ErrorHandlerFactory.showCustomAlert(dialog);
	}

	/**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	protected void sendToErrorPage(final int errorText) {
		final Context context = getErrorFieldUi().getContext();
		final ErrorHandlerFactory factory = ErrorHandlerFactory.getInstance();
		final AlertDialog dialog = 
				factory.createErrorModal(context.getString(R.string.default_outage_header), 
						context.getString(errorText));
		ErrorHandlerFactory.showCustomAlert(dialog);
	}

}
