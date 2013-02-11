package com.discover.mobile.card.login.register;

import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.FAILED_SECURITY;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_EXTERNAL_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_ONLINE_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.ONLINE_STATUS_PROHIBITED;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.FINAL_LOGIN_ATTEMPT;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.SAMS_CLUB_MEMBER;

import java.net.HttpURLConnection;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardBaseErrorResponseHandler;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.card.push.register.PushRegistrationStatusSuccessListener;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.auth.forgot.ForgotUserIdCall;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.callback.LockScreenCompletionListener;
import com.discover.mobile.common.delegates.DelegateFactory;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.common.ui.widgets.NonEmptyEditText;
import com.discover.mobile.common.ui.widgets.UsernameOrAccountNumberEditText;
import com.google.common.base.Strings;
import com.xtify.sdk.api.XtifySDK;

/**
 * This class handles the forgot user ID flow.
 * If a user successfully completes this page they will be logged into the application and presented with
 * a dialog that shows them their user ID, email, and last 4 digits of their account number.
 * @author scottseward
 *
 */
public class ForgotUserIdActivity extends NotLoggedInRoboActivity {

	private static final String TAG = ForgotUserIdActivity.class.getSimpleName();

	private static final String MAIN_ERROR_LABEL_TEXT_KEY = "a";
	private static final String SHOULD_UPDATE_PASS_APPEARANCE = "b";
	private static final String SHOULD_UPDATE_ACCT_NBR_APPEARANCE = "c";
	private static final String MAIN_ERROR_LABEL_VISIBILITY_KEY = "d";
	private static final String PASS_FIELD_TEXT_KEY = "e";
	private static final String CARD_FIELD_TEXT_KEY = "f";

	private static final String MODAL_IS_SHOWING_KEY = "n";
	private static final String MODAL_BODY_KEY = "o";
	private static final String MODAL_TITLE_KEY = "p";
	private static final String MODAL_CLOSES_ACTIVITY_KEY = "q";

	private int modalTitleText;
	private int modalBodyText;
	private boolean modalClosesActivity = false;

	private RegistrationConfirmationDetails confirmationDetails;

	//BUTTONS
	private Button submitButton;

	//ERROR LABELS
	private TextView mainErrLabel;
	private TextView idErrLabel;
	private TextView passErrLabel;

	//TEXT LABELS
	private TextView cancelLabel;
	private TextView helpNumber;

	//INPUT FIELDS
	private UsernameOrAccountNumberEditText cardNumField;
	private NonEmptyEditText passField;

	//SCROLL VIEW
	private ScrollView mainScrollView;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_forgot_id);

		final HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
		progress.initChangePasswordHeader(0);
		progress.hideStepTwo();

		loadAllViews();
		setupInputFields();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);

		setOnClickActions();
		attachErrorLabels();

		restoreState(savedInstanceState);
	}

	/**
	 * Restore the state of input fields and error states if needed.
	 * @param savedInstanceState
	 */
	public void restoreState(final Bundle savedInstanceState) {
		if(savedInstanceState != null){
			mainErrLabel.setText(savedInstanceState.getString(MAIN_ERROR_LABEL_TEXT_KEY));
			mainErrLabel.setVisibility(savedInstanceState.getInt(MAIN_ERROR_LABEL_VISIBILITY_KEY));
			if(savedInstanceState.getBoolean(SHOULD_UPDATE_PASS_APPEARANCE))
				passField.updateAppearanceForInput();

			if(savedInstanceState.getBoolean(SHOULD_UPDATE_ACCT_NBR_APPEARANCE))
				cardNumField.updateAppearanceForInput();

			final String cardText = savedInstanceState.getString(CARD_FIELD_TEXT_KEY);
			final String passText = savedInstanceState.getString(PASS_FIELD_TEXT_KEY);

			if(!Strings.isNullOrEmpty(passText))
				passField.setText(passText);

			if(!Strings.isNullOrEmpty(cardText))
				cardNumField.setText(cardText);

			modalIsPresent = savedInstanceState.getBoolean(MODAL_IS_SHOWING_KEY);
			if(modalIsPresent){
				displayModal(savedInstanceState.getInt(MODAL_TITLE_KEY), 
						savedInstanceState.getInt(MODAL_BODY_KEY), 			
						savedInstanceState.getBoolean(MODAL_CLOSES_ACTIVITY_KEY));
			}
		}
	}

	/**
	 * Save the state of the error label on the screen so that upon rotation change, we can 
	 * restore them.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putString(MAIN_ERROR_LABEL_TEXT_KEY, mainErrLabel.getText().toString());
		outState.putInt(MAIN_ERROR_LABEL_VISIBILITY_KEY, mainErrLabel.getVisibility());

		if(passErrLabel.getVisibility() != View.GONE)
			outState.putBoolean(SHOULD_UPDATE_PASS_APPEARANCE, true);

		if(idErrLabel.getVisibility() != View.GONE)
			outState.putBoolean(SHOULD_UPDATE_ACCT_NBR_APPEARANCE, true);	

		outState.putString(PASS_FIELD_TEXT_KEY, passField.getText().toString());
		outState.putString(CARD_FIELD_TEXT_KEY, cardNumField.getText().toString());

		outState.putBoolean(MODAL_IS_SHOWING_KEY, modalIsPresent);
		outState.putInt(MODAL_TITLE_KEY, modalTitleText);
		outState.putInt(MODAL_BODY_KEY, modalBodyText);
		outState.putBoolean(MODAL_CLOSES_ACTIVITY_KEY, modalClosesActivity);

		super.onSaveInstanceState(outState);
	}


	/**
	 * Get the views that we need from the layout and assign them to local references.
	 */
	private void loadAllViews() {
		submitButton = (Button)findViewById(R.id.forgot_id_submit_button);

		mainErrLabel = (TextView)findViewById(R.id.forgot_id_submission_error_label);
		idErrLabel = (TextView)findViewById(R.id.forgot_id_id_error_label);
		passErrLabel = (TextView)findViewById(R.id.forgot_id_pass_error_label);

		cancelLabel = (TextView)findViewById(R.id.account_info_cancel_label);
		helpNumber = (TextView)findViewById(R.id.help_number_label);

		cardNumField = (UsernameOrAccountNumberEditText)findViewById(R.id.forgot_id_id_field);
		passField = (NonEmptyEditText)findViewById(R.id.forgot_id_password_field);

		mainScrollView = (ScrollView)findViewById(R.id.main_scroll);
	}

	/**
	 * Attach error labels to input fields.
	 */
	private void attachErrorLabels(){
		passField.attachErrorLabel(passErrLabel);
		cardNumField.attachErrorLabel(idErrLabel);
	}

	/**
	 * Set the card field to accept an account number.
	 * 
	 * Set the input fields to be able to control the enabled state of the submit button.
	 * If both pass and card are valid, the continue button gets enabled.
	 * 
	 */
	private void setupInputFields() {
		cardNumField.setFieldAccountNumber();
	}

	/**
	 * Assign click listeners to buttons and phone number.
	 */
	private void setOnClickActions() {
		final String helpNumberString = helpNumber.getText().toString();
		final Context currentContext = this;
		helpNumber.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				CommonMethods.dialNumber(helpNumberString, currentContext);
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				checkInputsAndSubmit();
			}
		});

		cancelLabel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				goBack();
			}
		});
	}

	/**
	 * When the hardware back button is pressed, call goBack().
	 */
	@Override
	public void onBackPressed() {
		goBack();
	}


	/**
	 * 
	 */
	private void checkInputsAndSubmit() {
		cardNumField.updateAppearanceForInput();
		passField.updateAppearanceForInput();
		CommonMethods.setViewGone(mainErrLabel);

		if(cardNumField.isValid() && passField.isValid())
			doForgotUserIdCall();
		else{
			mainScrollView.smoothScrollTo(0, 0);
			displayOnMainErrorLabel(getString(R.string.login_error));
		}

	}
	/**
	 * Submit the form info to the server and handle success or error.
	 */
	private void doForgotUserIdCall() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		//Lock orientation while request is being processed
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {
			@Override
			public void success(final NetworkServiceCall<?> sender, final RegistrationConfirmationDetails value) {
				progress.dismiss();
				confirmationDetails = value;
				getAccountDetails();
			}

			@Override
			public void complete(final NetworkServiceCall<?> sender, final Object result) {
				//Unlock orientation after request has been proceesed
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}

			@Override
			public void failure(final NetworkServiceCall<?> sender, final Throwable error) {
				progress.dismiss();
				Log.e(TAG, "Error: " + error.getMessage());
				showOkAlertDialog("Error", error.getMessage());

				final BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(sender, error);
			}

			@Override
			public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
				progress.dismiss();
				resetScrollPosition();

				switch (errorResponse.getHttpStatusCode()) {
				case HttpURLConnection.HTTP_UNAUTHORIZED:
					displayOnMainErrorLabel(getString(R.string.login_error));
					return true;

				default:
					Log.e(TAG, "UNHANDLED ERROR: " + errorResponse.toString());
					displayOnMainErrorLabel(getString(R.string.unkown_error_text));
					return true;

				}

			}


			@Override
			public boolean handleMessageErrorResponse(final NetworkServiceCall<?> sender, final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				resetScrollPosition();

				idErrLabel.setText(messageErrorResponse.getMessage());

				switch (messageErrorResponse.getMessageStatusCode()){

				case STRONG_AUTH_NOT_ENROLLED:
					displayModal(R.string.account_security_title_text, R.string.account_security_not_enrolled, true);
					return true;

				case LOCKED_OUT_ACCOUNT:
				case MAX_LOGIN_ATTEMPTS:
					displayModal(R.string.lockout_title, R.string.locked_account, true);
					return true;

				case LAST_ATTEMPT_WARNING:
					displayOnMainErrorLabel(getString(R.string.login_attempt_warning));
					return true;

				case SAMS_CLUB_MEMBER: 
					displayModal(R.string.we_are_sorry, R.string.account_info_sams_club_card_error_text, false);
					return true;

				case REG_AUTHENTICATION_PROBLEM: 
					displayOnMainErrorLabel(getString(R.string.account_info_bad_input_error_text));					
					return true;

				case FINAL_LOGIN_ATTEMPT:
					displayOnMainErrorLabel(getString(R.string.login_attempt_warning));
					return true;

				case INVALID_EXTERNAL_STATUS:
				case ONLINE_STATUS_PROHIBITED:
				case INVALID_ONLINE_STATUS:
				case BAD_ACCOUNT_STATUS:
				case AUTH_BAD_ACCOUNT_STATUS:
					displayModal(R.string.could_not_complete_request, R.string.zluba_error, true);
					return true;

				case PLANNED_OUTAGE:
					displayModal(R.string.could_not_complete_request, R.string.planned_outage_one, false);
					return true;

				case FAILED_SECURITY:	
					displayOnMainErrorLabel(getString(R.string.account_info_bad_input_error_text));
					return true;

				default:
					return false;

				}				
			}
		};

		new ForgotUserIdCall(this, callback, CommonMethods.getSpacelessString(cardNumField.getText().toString()), passField.getText().toString()).submit();
	}

	private void displayOnMainErrorLabel(final String text){
		mainErrLabel.setText(text);
		CommonMethods.setViewVisible(mainErrLabel);
	}

	private void resetScrollPosition(){
		mainScrollView.smoothScrollTo(0, 0);
	}

	private void showOkAlertDialog(final String title, final String message) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				finish();
			}
		})
		.show();
	}

	/**
	 * This method submits the users information to the Card server for verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 * 
	 */
	private void getAccountDetails() {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback
				.<AccountDetails> builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withErrorResponseHandler(new CardBaseErrorResponseHandler(this))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.withCompletionListener(new LockScreenCompletionListener(this))
				.withSuccessListener(new SuccessListener<AccountDetails>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final NetworkServiceCall<?> sender, final AccountDetails value) {
						// Set logged in to be able to save user name in
						// persistent storage
						Globals.setLoggedIn(true);

						// Update current account based on user logged

						CurrentSessionDetails.getCurrentSessionDetails()
						.setAccountDetails(value);

						getXtifyRegistrationStatus();

					}
				})
				.build();

		new AuthenticateCall(this, callback).submit();
	}

	/**
	 * Do a GET request to the server to check to see if this vendor id is
	 * registered to this user.
	 * 
	 * @author jthornton
	 */
	protected void getXtifyRegistrationStatus(){
		if(XtifySDK.getXidKey(this) != null){
			final AsyncCallback<PushRegistrationStatusDetail> callback = 
					GenericAsyncCallback.<PushRegistrationStatusDetail>builder(this)
					.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
							getResources().getString(R.string.push_progress_registration_loading), 
							true)
							.withSuccessListener(new PushConfirmationSuccessListener())
							.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(DelegateFactory.getLoginDelegate().getLoginActivity()))
							.withExceptionFailureHandler(new BaseExceptionFailureHandler())
							.withCompletionListener(new LockScreenCompletionListener(this))
							.finishCurrentActivityOnSuccess(this)
							.build();

			new GetPushRegistrationStatus(this, callback).submit();
		}else{
			navigateToConfirmationScreenWithResponseData(confirmationDetails);
			finish();
		}

	}
	/**
	 * The original success listener needed to be extended to support navigating to another screen
	 * on success. This specific class handles navigating to the home screen after a user completes
	 * registration and the push notification status is retrieved.
	 * @author scottseward
	 *
	 */
	private class PushConfirmationSuccessListener extends PushRegistrationStatusSuccessListener implements SuccessListener<PushRegistrationStatusDetail>{

		/**
		 * Constructor that takes in a context so that it can manipulate the flow of the app.
		 */
		public PushConfirmationSuccessListener(){}

		/**
		 * Set the priority level of the success handler
		 * @return CallbackPriority - the priority of the callback
		 */
		@Override
		public CallbackPriority getCallbackPriority() {
			return CallbackPriority.LAST;
		}

		/**
		 * Send the app on the correct path when the call is successful
		 * @param value - the returning push registration detail from the server
		 */
		@Override
		public void success(final NetworkServiceCall<?> sender, final PushRegistrationStatusDetail value) {
			super.success(sender, value);
			navigateToConfirmationScreenWithResponseData(confirmationDetails);

		}
	}

	/**
	 * Start the next activity after this one is complete.
	 * @param responseData
	 */
	private void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData){
		final Intent confirmationScreen = new Intent(this, CardNavigationRootActivity.class);
		confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
		confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
		confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);

		confirmationScreen.putExtra(IntentExtraKey.SCREEN_TYPE, IntentExtraKey.SCREEN_FOROGT_USER);
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_CONFIRMATION);

		this.startActivity(confirmationScreen);
	}

	private void displayModal(final int titleText, final int bodyText, final boolean finishActivityOnClose){
		modalBodyText = bodyText;
		modalTitleText = titleText;
		modalClosesActivity = finishActivityOnClose;

		showErrorModal(titleText, bodyText, finishActivityOnClose);
	}

	@Override
	public void goBack() {
		finish();
		final Intent forgotCredentialsActivity = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentialsActivity);
	}

	@Override
	public TextView getErrorLabel() {
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		return null;
	}

}
