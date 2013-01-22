package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;

import java.net.HttpURLConnection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
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
import com.discover.mobile.common.customui.NonEmptyEditText;
import com.discover.mobile.common.customui.UsernameOrAccountNumberEditText;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.register.PushRegistrationStatusSuccessListener;
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
	
	private final String MAIN_ERROR_LABEL_TEXT_KEY = "a";
	private final String SHOULD_UPDATE_PASS_APPEARANCE = "b";
	private final String SHOULD_UPDATE_ACCT_NBR_APPEARANCE = "c";
	private final String MAIN_ERROR_LABEL_VISIBILITY_KEY = "d";
	private final String PASS_FIELD_TEXT_KEY = "e";
	private final String CARD_FIELD_TEXT_KEY = "f";
	
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
		loadAllViews();
		setupInputFields();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);
		
		setOnClickActions();
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
			
			String cardText = savedInstanceState.getString(CARD_FIELD_TEXT_KEY);
			String passText = savedInstanceState.getString(PASS_FIELD_TEXT_KEY);
			
			if(!Strings.isNullOrEmpty(passText))
				passField.setText(passText);
			
			if(!Strings.isNullOrEmpty(cardText))
				cardNumField.setText(cardText);
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
		
		mainScrollView = (ScrollView)findViewById(R.id.main_scroll_view);
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

		passField.addTextChangedListener(getSubmitButtonTextWatcherEnabler());
		cardNumField.addTextChangedListener(getSubmitButtonTextWatcherEnabler());
		
		attachErrorLabels();
	}
	
	/**
	 * Assign click listeners to buttons and phone number.
	 */
	private void setOnClickActions() {
		final String helpNumberString = helpNumber.getText().toString();
		final Context currentContext = this;
		helpNumber.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonMethods.dialNumber(helpNumberString, currentContext);
			}
		});
		
		submitButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				doForgotUserIdCall();
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
	 * Go back to the forgotten credentials activity instead of just finishing this activity.
	 */
	@Override
	public void goBack() {
		final Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	/**
	 * Submit the form info to the server and handle success or error.
	 */
	private void doForgotUserIdCall() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {
			@Override
			public void success(final RegistrationConfirmationDetails value) {
				progress.dismiss();
				confirmationDetails = value;
				getAccountDetails();
			}
			
			@Override
			public void failure(final Throwable error) {
				progress.dismiss();
				Log.e(TAG, "Error: " + error.getMessage());
				showOkAlertDialog("Error", error.getMessage());
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				mainScrollView.smoothScrollTo(0, 0);
				
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						displayOnMainErrorLabel(getString(R.string.login_error));
						return true;
						
					case HttpURLConnection.HTTP_UNAVAILABLE:
						displayOnMainErrorLabel(getString(R.string.unkown_error_text));
						return true;

				}
				
				return false;
			}
			
			private void displayOnMainErrorLabel(final String text){
				mainErrLabel.setText(text);
				CommonMethods.setViewVisible(mainErrLabel);
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				mainScrollView.smoothScrollTo(0, 0);

				progress.dismiss();

				idErrLabel.setText(messageErrorResponse.getMessage());
				
				switch (messageErrorResponse.getMessageStatusCode()){
					case BAD_ACCOUNT_STATUS:
					case AUTH_BAD_ACCOUNT_STATUS:
						sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
						return true;
					case STRONG_AUTH_NOT_ENROLLED:
						sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
						return true;
					case LOCKED_OUT_ACCOUNT:
						sendToErrorPage(ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
						return true;
					case MAX_LOGIN_ATTEMPTS:
						sendToErrorPage(ScreenType.LOCKED_OUT_USER);
						return true;
					case LAST_ATTEMPT_WARNING:
						displayOnMainErrorLabel(getString(R.string.login_attempt_warning));
						return true;


				}
				
				return false;
			}
		};

		new ForgotUserIdCall(this, callback, CommonMethods.getSpacelessString(cardNumField.getText().toString()), passField.getText().toString()).submit();
	}
	
	/**
	 * Send a user to a lockout activity and finish this activity.
	 * @param screenType
	 */
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
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
	 * Return a TextWatcher that will enable the submit button when all form info is valid.
	 * @return
	 */
	private TextWatcher getSubmitButtonTextWatcherEnabler() {
		return new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if(cardNumField.isValid() && passField.isValid())
					submitButton.setEnabled(true);
				else
					submitButton.setEnabled(false);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {/*empty*/}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {/*empty*/}
			
		};
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
				.withSuccessListener(new SuccessListener<AccountDetails>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final AccountDetails value) {
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
					.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(new LoginActivity()))
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
		public void success(final PushRegistrationStatusDetail value) {
			super.success(value);
			navigateToConfirmationScreenWithResponseData(confirmationDetails);

		}
	}
	
	/**
	 * Start the next activity after this one is complete.
	 * @param responseData
	 */
	private void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData){
		final Intent confirmationScreen = new Intent(this, NavigationRootActivity.class);
		confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
		confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
		confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);

		//TODO: Decide which screen type to display forgot both or register
		confirmationScreen.putExtra(IntentExtraKey.SCREEN_TYPE, IntentExtraKey.SCREEN_REGISTRATION);
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_CONFIRMATION);
		

		this.startActivity(confirmationScreen);
	}
	
}
