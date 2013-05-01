/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.error;

import android.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.discover.mobile.common.R;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

/**
 * FIXME - analyze this class and fix 
 * @author ekaram
 *
 */
public abstract class BaseErrorHandler implements ErrorHandler {

	static final String TAG = BaseErrorHandler.class.getSimpleName();

	/**
	 * 
	 */
	public BaseErrorHandler() {
		super();
	}

	@Override
	public void showErrorsOnScreen(final ErrorHandlerUi errorHandlerUi, final String errorText) {
		// Show error label and display error text
		if (errorHandlerUi != null) {
			errorHandlerUi.getErrorLabel().setText(errorText);
			errorHandlerUi.getErrorLabel().setVisibility(View.VISIBLE);
		}
	
		// Set the input fields to be highlighted in red and clears text
		if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
			for (final EditText text : errorHandlerUi.getInputFields()) {
				text.setBackgroundResource(R.drawable.edit_text_red);
				text.setText("");
			}
	
			// Set Focus to first field in screen
			errorHandlerUi.getInputFields().get(0).requestFocus();
		}
	}

	
	@Override
	public void clearTextOnScreen(final ErrorHandlerUi errorHandlerUi) {
		// Hide error label and display error text
		if (errorHandlerUi != null) {
			errorHandlerUi.getErrorLabel().setVisibility(View.GONE);
		}
	
		// Set the input fields to be highlighted in red and clears text
		if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
			// Set Focus to first field in screen
			errorHandlerUi.getInputFields().get(0).requestFocus();
	
			for (int i = (errorHandlerUi.getInputFields().size() - 1); i >= 0; i--) {
				final EditText text = errorHandlerUi.getInputFields().get(i);
				text.setText("");
				text.setBackgroundResource(R.drawable.edit_text_default);
			}
		}
	}

	/**
	 * Show a custom modal alert dialog for the activity
	 * 
	 * @param alert
	 *            - the modal alert to be shown
	 */
	@Override
	public void showCustomAlert(final AlertDialog alert) {
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	
	@Override
	public ModalAlertWithOneButton createErrorModal(final int errorCode, final int titleText, final int errorText) {
		return null;
	}

	
	@Override
	public ModalAlertWithOneButton createErrorModal(final String titleText, final String errorText) {
		return null;
	}

	
	@Override
	public ModalAlertWithOneButton handleHttpInternalServerErrorModal() {
		return null;
	
	}

	
	@Override
	public ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(final ErrorHandlerUi mErrorHandlerUi, 
																							final String message) {
		return null;
	}

	
	@Override
	public ModalAlertWithOneButton handleHttpServiceUnavailableModal(final String errorText) {
		return null;
	}

	
	@Override
	public void handleHttpForbiddenError() {
		// TODO Will complete this in the Handle Technical Difficulties User
		// Story
	}

	
	@Override
	public void handleGenericError(final int httpErrorCode) {
	}

	
	@Override
	public void handleHttpUnauthorizedError() {
	
	}

	
	@Override
	public ModalAlertWithOneButton handleLockedOut(final ErrorHandlerUi errorHandlerUi, final String errorText) {
		return null;
	}

	
	public void handleStrongAuthChallenge() {
	}

	
	@Override
	public void handleSessionExpired() {
	}

	
	@Override
	public abstract void handleLoginAuthFailure(ErrorHandlerUi errorHandlerUi, String errorMessage);

}