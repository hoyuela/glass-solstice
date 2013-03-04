/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.error;

import android.app.AlertDialog;

import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

/**
 * @author ekaram
 *
 */
public interface ErrorHandler {

	/**
	 * Updates an ErrorHandlerUi to notify the user that an error has it
	 * occurred. It displays an error message on the error field, make the error
	 * label visible, Sets the input fields to be highlighted in red, and clears
	 * the activity edit texts in the getFieldsToClearAfterError() interface
	 * method.
	 * 
	 * The activity needs to implement ErrorHandlerUi for this functionality to
	 * work.
	 * 
	 * @param errorHandlerUi
	 *            - Reference to an instance ErrorHandlerUi to update with
	 *            errorText.
	 * @param errorText
	 *            - Contains the error string to be displayed on ErrorHandlerUi.
	 */
	public abstract void showErrorsOnScreen(ErrorHandlerUi errorHandlerUi, String errorText);

	/**
	 * Clears all text input fields on a ErrorHandlerUi instance.
	 * 
	 * @param errorHandlerUi
	 *            - Reference to ErrorHandlerUi that needs to be updated.
	 */
	public abstract void clearTextOnScreen(ErrorHandlerUi errorHandlerUi);

	/**
	 * Creates an error dialog with a single button using the title and error
	 * text provided. The dialog created will close the application on dismiss,
	 * if the user is not logged in; otherwise, returns to the previous screen.
	 * 
	 * @param activity
	 *            Reference to the activity which created the dialog
	 * @param errorCode
	 *            HTTP error code that triggered the creation of the dialog
	 * @param titleText
	 *            Title to be applied at the top of the modal dialog
	 * @param errorText
	 *            Text to be applied as the content of the modal dialog
	 * 
	 * @return Returns the modal dialog created
	 */
	public abstract ModalAlertWithOneButton createErrorModal(int errorCode, int titleText, int errorText);

	/**
	 * Creates an error dialog with a single button using the title and error
	 * text provided. The dialog created will close the application on dismiss,
	 * if the user is not logged in; otherwise, returns to the previous screen.
	 * 
	 * @param activity
	 *            Reference to the activity which created the dialog
	 * @param errorCode
	 *            HTTP error code that triggered the creation of the dialog
	 * @param titleText
	 *            Title to be applied at the top of the modal dialog
	 * @param errorText
	 *            Text to be applied as the content of the modal dialog
	 * 
	 * @return Returns the modal dialog created
	 */
	public abstract ModalAlertWithOneButton createErrorModal(String titleText, String errorText);

	/**
	 * Handler for an HTTP 500 Internal Server error response. Creates a one
	 * button modal with the default error title, error message, and help
	 * number. The help number is determined based on whether the user is logged
	 * in there bank or card account.
	 * 
	 * @return Returns a one button modal which can be displayed by the calling
	 *         activity
	 */
	public abstract ModalAlertWithOneButton handleHttpInternalServerErrorModal();

	/**
	 * Handler for a HTTP 403 fraud user and no user found. 
	 * @param mErrorHandlerUi 
	 * @return
	 */
	public abstract ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(ErrorHandlerUi mErrorHandlerUi, String message);

	/**
	 * Handler for an HTTP 503 Service Unavailable error response. Creates a one
	 * button modal with the default error title, and help number. The help
	 * number is determined based on whether the user is logged in there bank or
	 * card account.
	 * 
	 * Note: For Bank a 503 is meant for Planned and Unplanned System
	 * Maintenance.
	 * 
	 * @param errorText
	 *            - Error message to display in the modal created
	 * 
	 * @return Returns a one button modal which can be displayed by the calling
	 *         activity
	 */
	public abstract ModalAlertWithOneButton handleHttpServiceUnavailableModal(String errorText);

	public abstract void handleHttpForbiddenError();

	/**
	 * This function serves as a catch all error handler to NetworkServiceCall<>
	 * error responses.
	 * 
	 * @param httpErrorCode
	 *            Specifies the HTTP status code received in the error response.
	 */
	public abstract void handleGenericError(int httpErrorCode);

	public abstract void handleHttpUnauthorizedError();

	public abstract void handleLoginAuthFailure(ErrorHandlerUi errorHandlerUi, String errorMessage);

	/**
	 * Handler for an HTTP 403 Forbidden error response to a Login or Strong
	 * Auth request that indicates the user has been locked out. Creates a one
	 * button modal with default error title, and help number. The help number
	 * is determined based on whether the user is logged in there bank or card
	 * account. The error message is expected to be provided by the error
	 * response.
	 * 
	 * @param errorText
	 *            - Error message to display in the modal created
	 * 
	 * @return Returns a one button modal which can be displayed by the calling
	 *         activity
	 */
	public abstract ModalAlertWithOneButton handleLockedOut(ErrorHandlerUi errorHandlerUi, String errorText);

	

	/** 
	 * Navigates to login page after a session expired
	 */
	public abstract void handleSessionExpired();
	
	/**
	 * Displays a caller's custom alert as provided
	 * 
	 * @param alert
	 */
	public abstract void showCustomAlert(final AlertDialog alert) ;

}