package com.discover.mobile.bank.error;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.discover.mobile.bank.BankActivityManager;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthDetails;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

/**
 * Used to handle error responses to a NetworkServiceCall<>.
 * 
 * @author henryoyuela
 * 
 */
// public class BankErrorHandler extends BaseErrorHandler implements
// ErrorHandler {
public class BankErrorHandler implements ErrorHandler {

	static final String TAG = BankErrorHandler.class.getSimpleName();
	static final ErrorHandler instance = new BankErrorHandler();

	/**
	 * Uses a singleton design pattern
	 */
	private BankErrorHandler() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#clearTextOnScreen(com.discover
	 * .mobile.error.ErrorHandlerUi)
	 */
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
	@Override
	public ModalAlertWithOneButton createErrorModal(final int errorCode, final int titleText, final int errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		int helpResId = 0;
		if (Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT) {
			helpResId = R.string.need_help_number_text;
		} else {
			helpResId = com.discover.mobile.bank.R.string.bank_need_help_number_text;
		}

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, titleText, errorText, true, helpResId,
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
	@Override
	public ModalAlertWithOneButton createErrorModal(final String titleText, final String errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		int helpResId = 0;
		if (Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT) {
			helpResId = R.string.need_help_number_text;
		} else {
			helpResId = com.discover.mobile.bank.R.string.bank_need_help_number_text;
		}

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, titleText, errorText, true, helpResId,
				R.string.ok);

		modal.getBottom().getButton().setOnClickListener(new OnClickListener() {
			@Override
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
	@Override
	public ModalAlertWithOneButton handleHttpInternalServerErrorModal() {

		final ModalAlertWithOneButton modal = createErrorModal(HttpURLConnection.HTTP_INTERNAL_ERROR, R.string.error_500_title,
				com.discover.mobile.bank.R.string.bank_error_500_message);
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
	@Override
	public ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(final ErrorHandlerUi mErrorHandlerUi,
			final String message) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

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
	@Override
	public ModalAlertWithOneButton handleHttpServiceUnavailableModal(final String errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		// Fetch modal title from resources
		final String title = activeActivity.getResources().getString(R.string.error_503_title);

		ModalAlertWithOneButton modal = null;

		// If errorText is null then use the default error message when a 503 is
		// received
		if (null == errorText) {
			modal = createErrorModal(HttpURLConnection.HTTP_UNAVAILABLE, R.string.error_503_title,
					com.discover.mobile.bank.R.string.bank_error_503_message);

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
	@Override
	public void handleHttpForbiddenError() {
		// TODO: Will complete this in the Handle Technical Difficulties User
		// Story
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#handleGenericError(int)
	 */
	@Override
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
	@Override
	public void handleHttpUnauthorizedError() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleStrongAuthFailure(com.discover
	 * .mobile.error.ErrorHandlerUi, java.lang.String,
	 * com.discover.mobile.common.auth.bank.strong.BankStrongAuthDetails)
	 */
	@Override
	public void handleStrongAuthFailure(final ErrorHandlerUi errorHandlerUi, final String errorMessage,
			final BankStrongAuthDetails details) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		BankNavigator.navigateToStrongAuth(activeActivity, details, errorMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.discover.mobile.error.ErrorHandler#handleLoginAuthFailure(com.discover
	 * .mobile.error.ErrorHandlerUi, java.lang.String)
	 */
	@Override
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
	@Override
	public ModalAlertWithOneButton handleLockedOut(final ErrorHandlerUi errorHandlerUi, final String errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		// Fetch modal title from resources
		final String title = activeActivity.getResources().getString(R.string.error_403_title);

		ModalAlertWithOneButton modal = null;

		// If errorText is null then use the default error message when a 403 is
		// called during login attempt
		if (null == errorText) {
			modal = createErrorModal(HttpURLConnection.HTTP_UNAVAILABLE, R.string.error_403_title,
					com.discover.mobile.bank.R.string.bank_error_403_message);
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
	 * @see com.discover.mobile.error.ErrorHandler#handleStrongAuthChallenge()
	 */
	@Override
	public void handleStrongAuthChallenge() {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		// Verify user is not in login page, if they are then the challenge will
		// be handled elsewhere
		if (activeActivity.getClass() != LoginActivity.class) {
			BankServiceCallFactory.createStrongAuthRequest().submit();
		} else {
			if (Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "In login activity, so ignoring strong auth challenge");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.error.ErrorHandler#handleSessionExpired()
	 */
	@Override
	public void handleSessionExpired() {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		BankNavigator.navigateToLoginPage(activeActivity, IntentExtraKey.SESSION_EXPIRED);
	}

}
