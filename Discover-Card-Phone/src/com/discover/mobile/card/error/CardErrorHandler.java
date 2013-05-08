package com.discover.mobile.card.error;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.discover.mobile.card.R;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.error.BaseErrorHandler;
import com.discover.mobile.common.error.CloseApplicationOnDismiss;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.error.NavigateToLoginOnDismiss;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

/**
 * Used to handle error responses to a NetworkServiceCall<>.
 * 
 * @author henryoyuela
 * 
 */

public class CardErrorHandler extends BaseErrorHandler {

	static final String TAG = CardErrorHandler.class.getSimpleName();
	static final ErrorHandler instance = new CardErrorHandler();

	/**
	 * Uses a singleton design pattern
	 */
	private CardErrorHandler() {
	}

	/**
	 * 
	 * @return Returns the singleton instance of ErrorHandlerFactory
	 */
	public static ErrorHandler getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#showErrorsOnScreen(com.discover
	 * .mobile.error.ErrorHandlerUi, java.lang.String)
	 */
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
			
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#clearTextOnScreen(com.discover.mobile.error.ErrorHandlerUi)
	 */
	public void clearTextOnScreen(final ErrorHandlerUi errorHandlerUi) {
		
		// Hide error label and display error text
		if (errorHandlerUi != null && errorHandlerUi.getErrorLabel() != null) {
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
	public void showCustomAlert(final AlertDialog alert) {
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#createErrorModal(int, int,
	 * int)
	 */
	
	public ModalAlertWithOneButton createErrorModal(final int errorCode, final int titleText, final int errorText) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);


		

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, titleText, errorText, true, R.string.need_help_number_text,
				R.string.ok);

		// If not logged in then exit the application
		if (!Globals.isLoggedIn() && HttpURLConnection.HTTP_UNAVAILABLE == errorCode) {
			// Close application
			modal.setOnDismissListener(new CloseApplicationOnDismiss(activeActivity));
		} else if (Globals.isLoggedIn()) {
			// Navigate back to login
			modal.setOnDismissListener(new NavigateToLoginOnDismiss(activeActivity));
		}

		// Show one button error dialog
		return modal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#createErrorModal(java.lang.String,
	 * java.lang.String)
	 */
	
	public ModalAlertWithOneButton createErrorModal(final String titleText, final String errorText) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		int helpResId =  R.string.need_help_number_text;
	

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, titleText, errorText, true, helpResId,
				R.string.ok);

		modal.getBottom().getButton().setOnClickListener(new OnClickListener() {
			
			public void onClick(final View v) {
				modal.dismiss();

			}
		});

		// Show one button error dialog
		return modal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleHttpInternalServerErrorModal
	 * ()
	 */
	
	public ModalAlertWithOneButton handleHttpInternalServerErrorModal() {

		final ModalAlertWithOneButton modal = createErrorModal(HttpURLConnection.HTTP_INTERNAL_ERROR, R.string.error_500_title,
				R.string.error_500);
		showCustomAlert(modal);
		return modal;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleHttpFraudNotFoundUserErrorModal
	 * (com.discover.mobile.error.ErrorHandlerUi, java.lang.String)
	 */
	
	public ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(final ErrorHandlerUi mErrorHandlerUi,
			final String message) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		final ModalAlertWithOneButton modal = createErrorModal(
				activeActivity.getResources().getString(R.string.error_403_title_request), message);
		showCustomAlert(modal);
		return modal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleHttpServiceUnavailableModal
	 * (java.lang.String)
	 */
	
	public ModalAlertWithOneButton handleHttpServiceUnavailableModal(final String errorText) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		// Fetch modal title from resources
		final String title = activeActivity.getResources().getString(R.string.error_503_title);

		ModalAlertWithOneButton modal = null;

		// If errorText is null then use the default error message when a 503 is
		// received
		if (null == errorText) {
			modal = createErrorModal(HttpURLConnection.HTTP_UNAVAILABLE, R.string.error_503_title,
					R.string.error_503);

		} else {
			modal = createErrorModal(title, errorText);
		}

		// If not logged in then exit the application
		if (!Globals.isLoggedIn()) {
			// Close application
			modal.setOnDismissListener(new CloseApplicationOnDismiss(activeActivity));
		} else if (Globals.isLoggedIn()) {
			// Navigate back to login
			modal.setOnDismissListener(new NavigateToLoginOnDismiss(activeActivity));
		}

		showCustomAlert(modal);

		return modal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#handleHttpForbiddenError()
	 */
	
	public void handleHttpForbiddenError() {
		// TODO: Will complete this in the Handle Technical Difficulties User
		// Story
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#handleGenericError(int)
	 */
	
	public void handleGenericError(final int httpErrorCode) {
		final ModalAlertWithOneButton modal = createErrorModal(httpErrorCode, R.string.error_request_not_completed_title,
				R.string.error_request_not_completed_msg);

		showCustomAlert(modal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#handleHttpUnauthorizedError()
	 */
	
	public void handleHttpUnauthorizedError() {

	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleLoginAuthFailure(com.discover
	 * .mobile.error.ErrorHandlerUi, java.lang.String)
	 */
	
	public void handleLoginAuthFailure(final ErrorHandlerUi errorHandlerUi, final String errorMessage) {
		showErrorsOnScreen(errorHandlerUi, errorMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleLockedOut(com.discover.mobile
	 * .error.ErrorHandlerUi, java.lang.String)
	 */
	
	public ModalAlertWithOneButton handleLockedOut(final ErrorHandlerUi errorHandlerUi, final String errorText) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		// Fetch modal title from resources
		final String title = activeActivity.getResources().getString(R.string.error_403_title);

		ModalAlertWithOneButton modal = null;

		// If errorText is null then use the default error message when a 403 is
		// called during login attempt
		if (null == errorText) {
			modal = createErrorModal(HttpURLConnection.HTTP_UNAVAILABLE, R.string.error_403_title,
					R.string.error_403);
		} else {
			modal = createErrorModal(title, errorText);
		}

		// Navigate back to login
		modal.setOnDismissListener(new NavigateToLoginOnDismiss(activeActivity));

		showCustomAlert(modal);

		// Clear text and set focus to first field
		clearTextOnScreen(errorHandlerUi);

		return modal;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#handleSessionExpired()
	 */
	
	public void handleSessionExpired() {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
		FacadeFactory.getLoginFacade().navToLoginWithMessage(activeActivity, bundle);
	}

}
