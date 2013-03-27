package com.discover.mobile.bank.error;

import java.net.HttpURLConnection;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.deposit.BankDepositForbidden;
import com.discover.mobile.bank.deposit.BankDepositTermsFragment;
import com.discover.mobile.bank.deposit.BankDepositWorkFlowStep;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankNetworkServiceCallManager;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.AcceptTermsService;
import com.discover.mobile.bank.services.deposit.GetAccountLimits;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.error.NavigateToLoginOnDismiss;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.error.bank.BankErrorResponse;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.widgets.ValidatedInputField;
import com.google.common.base.Strings;

/**
 * Used to handle error responses to a NetworkServiceCall<>.
 * 
 * @author henryoyuela
 * 
 */

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
		//Make sure the error handler UI interface has input fields
		if( errorHandlerUi.getInputFields() != null ) {
			// Set Focus to first field in screen
			errorHandlerUi.getInputFields().get(0).requestFocus();
	
			// Show error label and display error text
			if (errorHandlerUi != null && !Strings.isNullOrEmpty(errorText)) {
				final TextView errorLabel = errorHandlerUi.getErrorLabel();
				final int red = DiscoverActivityManager.getActiveActivity().getResources().getColor(R.color.red);
	
				errorLabel.setText(errorText);
				errorLabel.setVisibility(View.VISIBLE);
				errorLabel.setTextColor(red);
			}
	
			// Set the input fields to be highlighted in red and clears text
			if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
				final List<EditText> inputFields = errorHandlerUi.getInputFields();
				final int numberOfFields = inputFields.size();
				Object genericField = null;
	
				//Loop through the input fields, determine what kind of field they are, set their error state
				//and clear the text in them.
				for(int i = 0; i < numberOfFields; ++i){
					genericField = inputFields.get(i);
	
					//If the current field is a ValidatedInputField we should use its method for setting errors.
					if(genericField instanceof ValidatedInputField){
						((ValidatedInputField)genericField).setErrors();
					}else{
						((EditText)genericField).setBackgroundResource(R.drawable.edit_text_red);
					}
					//Clear the text in the field.
					((EditText)genericField).getEditableText().clear();
				}
	
			}
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
	@Override
	public void showCustomAlert(final AlertDialog alert) {
		DiscoverModalManager.setActiveModal(alert);
		DiscoverModalManager.setAlertShowing(true);
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
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		final int helpResId = com.discover.mobile.bank.R.string.bank_need_help_number_text;

		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, 
				titleText, errorText, true, helpResId,R.string.ok);
		
		/**Set modal Title and phone number if provided from server*/
		updateModalInfo(modal);

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
	 * com.discover.mobile.error.ErrorHandler#createErrorModal(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ModalAlertWithOneButton createErrorModal(final String titleText, final String errorText) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		// Keep track of times an error page is shown for login
		TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

		// Decide on what help number to show
		final int helpResId = com.discover.mobile.bank.R.string.bank_need_help_number_text;


		// Create a one button modal with text as per parameters provided
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, 
				titleText, errorText, true, helpResId, R.string.ok);
		
		/**Set modal Title and phone number if provided from server*/
		updateModalInfo(modal);
		
		modal.getBottom().getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				modal.dismiss();

			}
		});

		// Show one button error dialog
		return modal;
	}
	
	/**
	 * Method used to update the title and phone number in modal if provided in the last error
	 * response which is provided via the BankNetworkServiceCallManager;
	 * 
	 * @param modal Reference to a modal that has not been shown.
	 */
	public void updateModalInfo(final ModalAlertWithOneButton modal) {
		/**Update footer in modal to use phone number provided in error response*/
		final ErrorResponse<?> errorResponse = BankNetworkServiceCallManager.getInstance().getLastError();
		if( errorResponse != null &&  errorResponse instanceof BankErrorResponse ) {
			final BankErrorResponse bankErrorResponse = (BankErrorResponse) errorResponse;
			final String phoneNumber = bankErrorResponse.getPhoneNumber();
			final String title = bankErrorResponse.getTitle();
			
			final ModalDefaultTopView modalTopView = (ModalDefaultTopView)modal.getTop();
			
			/**Set modal title with title sent from server*/
			if( !Strings.isNullOrEmpty(title) )
				modalTopView.setTitle(title);
			
			/**Set modal phonenumber with number sent from server*/
			if( !Strings.isNullOrEmpty(phoneNumber) && modalTopView.getHelpFooter() != null) 
				modalTopView.getHelpFooter().setToDialNumberOnClick(phoneNumber);
		}

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
	@Override
	public ModalAlertWithOneButton handleHttpServiceUnavailableModal(final String errorText) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

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
	
	/**
	 * Handler for 403 Forbidden for Check Deposit work-flow. Error message is expected to be provided from server.
	 * 
	 * @param msgErrResponse Reference to object that represents the response from the Server with error message to display.
	 * 
	 * @return True if handled, false otherwise
	 */
	public boolean handleHttpForbidden(final BankErrorResponse msgErrResponse) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();
		boolean handled = false;
		
		/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
		if( activity != null && activity instanceof BankNavigationRootActivity ) {			
			final NetworkServiceCall<?> lastCall = BankNetworkServiceCallManager.getInstance().getLastServiceCall();
			
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity)activity;
			
			/**Remove the Check Deposit Terms and Conditions View from fragment back stack*/
			if( navActivity.getCurrentContentFragment() instanceof BankDepositTermsFragment) {			
				navActivity.getSupportFragmentManager().popBackStack();
			}
			
			/**Verify the user is currently in the check deposit work-flow*/
			if ( lastCall instanceof GetAccountLimits ||
				 (lastCall instanceof AcceptTermsService &&
			     ((AcceptTermsService)lastCall).getEligibility().isDepositsEligibility())) {
				
				final Bundle bundle = new Bundle();
				bundle.putString(BankDepositForbidden.KEY_ERROR_MESSAGE, msgErrResponse.getErrorMessage());
				BankConductor.navigateToCheckDepositWorkFlow(bundle, BankDepositWorkFlowStep.ForbiddenError);
				handled = true;
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Unable to process 403 invalid service call");
				}
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to process 403 invalid activity type");
			}
		}
		
		return handled;
		
	}
	
	
	/**
	 * Handler for 422 UnprocessableEntiry from execution of a Bank service call. Error message is expected to be provided from server.
	 * 
	 * @param msgErrResponse Reference to object that represents the response from the Server with error message to display.
	 * 
	 * @return True if handled, false otherwise
	 */
	public boolean handleUnprocessableEntity(final BankErrorResponse msgErrResponse) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();
		boolean handled = false;
		
		/**Verify an error message is provided*/
		if( !Strings.isNullOrEmpty(msgErrResponse.getErrorMessage()) ) {
			/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
			if( activity != null && activity instanceof BankNavigationRootActivity ) {			
				final BankNavigationRootActivity navActivity = (BankNavigationRootActivity)activity;
				
				if( navActivity.getCurrentContentFragment() != null &&
					navActivity.getCurrentContentFragment() instanceof BankErrorHandlerDelegate ) {
				
					final BankErrorHandlerDelegate errorHandler = (BankErrorHandlerDelegate)navActivity.getCurrentContentFragment();
					
					handled = errorHandler.handleError(msgErrResponse);
				} else {
					if( Log.isLoggable(TAG, Log.ERROR)) {
						Log.e(TAG, "Unable to process 422 invalid fragment type");
					}
				}
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Unable to process 422 invalid activity type");
				}
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "No Error message provided");
			}
		}
		
		return handled;
		
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
		
		//If it is a screen with inline errors then clear the text fields on an error
		if( DiscoverActivityManager.getActiveActivity() instanceof ErrorHandlerUi ) {
			final ErrorHandlerUi currentUi = (ErrorHandlerUi) DiscoverActivityManager.getActiveActivity();
			this.showErrorsOnScreen(currentUi, null);
		}
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
	 * com.discover.mobile.error.ErrorHandler#handleLoginAuthFailure(com.discover
	 * .mobile.error.ErrorHandlerUi, java.lang.String)
	 */
	@Override
	public void handleLoginAuthFailure(final ErrorHandlerUi errorHandlerUi, final String errorMessage) {
		/** Navigate to login page if not already on this page*/
		BankConductor.navigateToLoginPage(DiscoverActivityManager.getActiveActivity(), IntentExtraKey.SHOW_ERROR_MESSAGE, errorMessage);
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
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

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

		//Hide bottom view for locked out account
		modal.hideBottomView();
		
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
	@Override
	public void handleSessionExpired() {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		BankConductor.navigateToLoginPage(activeActivity, IntentExtraKey.SESSION_EXPIRED, null);
	}

}
