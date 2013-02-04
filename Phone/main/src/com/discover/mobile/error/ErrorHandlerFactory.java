package com.discover.mobile.error;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.R;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.bank.BankActivityManager;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.Navigator;

/**
 * Used to handle error responses to a NetworkServiceCall<>.
 * 
 * @author henryoyuela
 * 
 */
public class ErrorHandlerFactory {
	static final String TAG = ErrorHandlerFactory.class.getSimpleName();
	static final ErrorHandlerFactory instance = new ErrorHandlerFactory();

	/**
	 * Uses a singleton design pattern
	 */
	private ErrorHandlerFactory() {
	}

	/**
	 * 
	 * @return Returns the singleton instance of ErrorHandlerFactory
	 */
	public static ErrorHandlerFactory getInstance() {
		return instance;
	}

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
	public void showErrorsOnScreen(final ErrorHandlerUi errorHandlerUi,final String errorText) {
		//Show error label and display error text
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

	/**
	 * Clears all text input fields on a ErrorHandlerUi instance.
	 * 
	 * @param errorHandlerUi
	 *            - Reference to ErrorHandlerUi that needs to be updated.
	 */
	public void clearTextOnScreen(final ErrorHandlerUi errorHandlerUi) {
		//Hide error label and display error text
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
	public static void showCustomAlert(final AlertDialog alert) {
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	/**
	 * DismissListener which can be applied to an alert dialog to close the
	 * application when it is dismissed.
	 * 
	 * @author henryoyuela
	 * 
	 */
	private class CloseApplicationOnDismiss implements OnDismissListener {
		// Reference of the activity to be able to close out the application
		private final Activity activity;

		public CloseApplicationOnDismiss(final Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onDismiss(final DialogInterface dialog) {
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "Closing Application...");
			}

			// close application
			this.activity.finish();

		}
	}

	/**
	 * DismissListener which can be applied to an alert dialog to navigate back
	 * to the login page when it is dismissed.
	 * 
	 * @author henryoyuela
	 * 
	 */
	private class NavigateToLoginOnDismiss implements OnDismissListener {
		// Reference of the activity to be able to close out the application
		private final Activity activity;

		public NavigateToLoginOnDismiss(final Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onDismiss(final DialogInterface dialog) {
			// Send an intent to open login activity if current activity is not
			// login
			if (this.activity.getClass() != LoginActivity.class) {
				final Intent intent = new Intent(activity, LoginActivity.class);
				final Bundle bundle = new Bundle();
				bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE,
						false);
				intent.putExtras(bundle);
				activity.startActivity(intent);

				// Close current activity
				activity.finish();
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "Application is already in login view");
				}
			}
		}
	}

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
	public ModalAlertWithOneButton createErrorModal(final int errorCode,
			final int titleText, final int errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		int helpResId = 0;
		if (Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT) {
			helpResId = R.string.need_help_number_text;
		} else {
			helpResId = R.string.bank_need_help_number_text;
		}

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity,
				titleText, errorText, true, helpResId, R.string.ok);

		// If not logged in then exit the application
		if (!Globals.isLoggedIn()
				&& HttpURLConnection.HTTP_UNAVAILABLE == errorCode) {
			// Close application
			modal.setOnDismissListener(instance.new CloseApplicationOnDismiss(
					activeActivity));
		} else if (Globals.isLoggedIn()) {
			// Navigate back to login
			modal.setOnDismissListener(instance.new NavigateToLoginOnDismiss(
					activeActivity));
		}

		// Show one button error dialog
		return modal;
	}

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
	public ModalAlertWithOneButton createErrorModal(final String titleText,
			final String errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		int helpResId = 0;
		if (Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT) {
			helpResId = R.string.need_help_number_text;
		} else {
			helpResId = R.string.bank_need_help_number_text;
		}

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity,
				titleText, errorText, true, helpResId, R.string.ok);
		
		modal.getBottom().getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				modal.dismiss();
				
			}		
		});

		// Show one button error dialog
		return modal;
	}

	/**
	 * Handler for an HTTP 500 Internal Server error response. Creates a one
	 * button modal with the default error title, error message, and help
	 * number. The help number is determined based on whether the user is logged
	 * in there bank or card account.
	 * 
	 * @return Returns a one button modal which can be displayed by the calling
	 *         activity
	 */
	public ModalAlertWithOneButton handleHttpInternalServerErrorModal() {

		final ModalAlertWithOneButton modal = createErrorModal(
				HttpURLConnection.HTTP_INTERNAL_ERROR,
				R.string.error_500_title, R.string.bank_error_500_message);
		showCustomAlert(modal);
		return modal;

	}
	
	/**
	 * Handler for a HTTP 403 fraud user and no user found. 
	 * @param mErrorHandlerUi 
	 * @return
	 */
	public ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(final ErrorHandlerUi mErrorHandlerUi, final String message) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		final ModalAlertWithOneButton modal = createErrorModal(activeActivity.getResources().getString(R.string.error_403_title_request), message);
		showCustomAlert(modal);
		return modal;
	}

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
	public ModalAlertWithOneButton handleHttpServiceUnavailableModal(
			final String errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		// Fetch modal title from resources
		final String title = activeActivity.getResources().getString(
				R.string.error_503_title);

		ModalAlertWithOneButton modal = null;

		// If errorText is null then use the default error message when a 503 is
		// received
		if (null == errorText) {
			modal = createErrorModal(HttpURLConnection.HTTP_UNAVAILABLE,
					R.string.error_503_title, R.string.bank_error_503_message);

		} else {
			modal = createErrorModal(title, errorText);
		}

		// If not logged in then exit the application
		if (!Globals.isLoggedIn()) {
			// Close application
			modal.setOnDismissListener(instance.new CloseApplicationOnDismiss(
					activeActivity));
		} else if (Globals.isLoggedIn()) {
			// Navigate back to login
			modal.setOnDismissListener(instance.new NavigateToLoginOnDismiss(
					activeActivity));
		}

		showCustomAlert(modal);

		return modal;
	}

	public void handleHttpForbiddenError() {
		// TODO: Will complete this in the Handle Technical Difficulties User
		// Story
	}

	/**
	 * This function serves as a catch all error handler to NetworkServiceCall<>
	 * error responses.
	 * 
	 * @param httpErrorCode
	 *            Specifies the HTTP status code received in the error response.
	 */
	public void handleGenericError(final int httpErrorCode) {
		final ModalAlertWithOneButton modal = createErrorModal(
				httpErrorCode,
				R.string.error_request_not_completed_title, R.string.error_request_not_completed_msg);
		
		showCustomAlert(modal);
	}

	public void handleHttpUnauthorizedError() {
		
	}
	
	/**
	 * This function handles the response for a 401 with strong auth. The new
	 * question and id are sent as an intent to the strong auth activity.
	 * 
	 * @param errorHandlerUi
	 * @param errorMessage
	 * @param question
	 * @param id
	 */
	public void handleStrongAuthFailure(final ErrorHandlerUi errorHandlerUi,
			final String errorMessage, final String question, final String id) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();

		Navigator.navigateToStrongAuth(activeActivity, question, id, errorMessage);
	}

	public void handleLoginAuthFailure(final ErrorHandlerUi errorHandlerUi,
			final String errorMessage) {
		showErrorsOnScreen(errorHandlerUi, errorMessage);
	}

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
	public ModalAlertWithOneButton handleLockedOut(
			final ErrorHandlerUi errorHandlerUi, final String errorText) {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		// Fetch modal title from resources
		final String title = activeActivity.getResources().getString(
				R.string.error_403_title);

		ModalAlertWithOneButton modal = null;

		// If errorText is null then use the default error message when a 403 is
		// called during login attempt
		if (null == errorText) {
			modal = createErrorModal(HttpURLConnection.HTTP_UNAVAILABLE,
					R.string.error_403_title, R.string.bank_error_403_message);
		} else {
			modal = createErrorModal(title, errorText);
		}

		// Navigate back to login
		modal.setOnDismissListener(instance.new NavigateToLoginOnDismiss(
				activeActivity));

		showCustomAlert(modal);

		// Clear text and set focus to first field
		clearTextOnScreen(errorHandlerUi);

		return modal;
	}

	/**
     * Launch the strong auth Activity with the question that was retrieved from the get strong auth question call.
     */
	public void handleStrongAuthChallenge() {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		//Verify user is not in login page, if they are then the challenge will be handled elsewhere
		if( activeActivity.getClass() != LoginActivity.class ) {
			BankServiceCallFactory.createStrongAuthRequest().submit();
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "In login activity, so ignoring strong auth challenge");
			}
		}
	}
	
	/** 
	 * Navigates to login page after a session expired
	 */
	public void handleSessionExpired() {
		final Activity activeActivity = BankActivityManager.getActiveActivity();
		
		Navigator.navigateToLoginPage(activeActivity, IntentExtraKey.SESSION_EXPIRED);
	}
	
	
}
