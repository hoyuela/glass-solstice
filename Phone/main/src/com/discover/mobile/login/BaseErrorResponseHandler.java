package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.NO_DATA_FOUND;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.StandardErrorCodes.UNSCHEDULED_MAINTENANCE;

import java.net.HttpURLConnection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.R;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
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
public class BaseErrorResponseHandler implements ErrorResponseHandler {

	/**
	 * The Parent RoboSlidingFragmentActvitiy that made the service call
	 */
	protected ErrorHandlerUi errorHandlerUi = null;
	
	/**
	 * Private constructor to prevent construction without a fragment or
	 * activity
	 */
	public BaseErrorResponseHandler(ErrorHandlerUi errorHandlerUi) {
		this.errorHandlerUi = errorHandlerUi;
	}

	/**
	 * Private constructor to prevent construction without a fragment or
	 * activity
	 */
	@SuppressWarnings("unused")
	private BaseErrorResponseHandler() {
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
	public final boolean handleFailure(final ErrorResponse<?> errorResponse) {

		String errorHandlingFailureMessage;

		if (errorResponse instanceof JsonMessageErrorResponse) {

			JsonMessageErrorResponse messageErrorResponse = (JsonMessageErrorResponse) errorResponse;
			
			// FIRST we will try for common status code generic handling
			switch (messageErrorResponse.getMessageStatusCode()) {
			case UNSCHEDULED_MAINTENANCE:
				getErrorFieldUi().sendToErrorPage(R.string.temporary_outage);
				return true;

			case SCHEDULED_MAINTENANCE:
				getErrorFieldUi().sendToErrorPage(R.string.planned_outage_one);
				return true;

			case PLANNED_OUTAGE:
				getErrorFieldUi().sendToErrorPage(R.string.planned_outage_one);
				return true;

			case NO_DATA_FOUND:
				getErrorFieldUi().sendToErrorPage(R.string.no_data_found);
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
		ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
			for (EditText text : errorHandlerUi.getInputFields()) {
				text.setBackgroundResource(R.drawable.edit_text_red);
			}
		}
	}

	/**
	 * Clears the activity edit texts in the getFieldsToClearAfterError()
	 * interface method
	 */
	protected void clearInputs() {

		ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
		if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
			for (EditText text : errorHandlerUi.getInputFields()) {
				text.setText("");
			}
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
			getErrorFieldUi().sendToErrorPage(R.string.internal_server_error_500);
			return true;

		case HttpURLConnection.HTTP_UNAVAILABLE:
			getErrorFieldUi().sendToErrorPage(R.string.internal_server_error_503);
			return true;

		case HttpURLConnection.HTTP_FORBIDDEN:
			getErrorFieldUi().sendToErrorPage(R.string.forbidden_403);
			return true;
		}
		return false;
	}

	

	/**
	 * A common method used to display a modal dialog with an error message
	 * 
	 * @param stringResource
	 */
	protected void showModalErrorDialog(int title, int content, int buttonText) {
		getErrorFieldUi().showOneButtonAlert(title, content, buttonText);
	}

	/**
	 * A common method used to display a modal dialog with an error message
	 * 
	 * @param stringResource
	 */
	protected void showDynamicModalErrorDialog(int title, String content, int buttonText) {
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
	protected void setErrorText(int errorText) {
		ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
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
	protected void setDynamicErrorText(String message) {
		ErrorHandlerUi errorHandlerUi = getErrorFieldUi();
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
    public void showOneButtonAlert(int title, int content, int buttonText){    	
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
    public void showDynamicOneButtonAlert(int title, String content, int buttonText){    	
		showCustomAlert(new ModalAlertWithOneButton(getErrorFieldUi().getContext(),title,content,buttonText));
    }
    
    
    /**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	protected void sendToErrorPage(int titleText, int errorText) {
		final Intent maintenancePageIntent = new Intent(getErrorFieldUi().getContext(), LockOutUserActivity.class);
		maintenancePageIntent.putExtra(IntentExtraKey.ERROR_TEXT_KEY, errorText);
		getErrorFieldUi().getContext().startActivity(maintenancePageIntent);
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
	}

	/**
	 * A common method used to forward user to error page with a given static
	 * string text message
	 * 
	 * @param errorText
	 */
	protected void sendToErrorPage(int errorText) {
		final Intent maintenancePageIntent = new Intent(getErrorFieldUi().getContext(), LockOutUserActivity.class);
		maintenancePageIntent.putExtra(IntentExtraKey.ERROR_TEXT_KEY, errorText);
		getErrorFieldUi().getContext().startActivity(maintenancePageIntent);
	}

}
