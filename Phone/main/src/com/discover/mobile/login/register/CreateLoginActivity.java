package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_ALREADY_TAKEN;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_SSN_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.CreateLoginCall;
import com.discover.mobile.common.auth.registration.CreateLoginDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.customui.ConfirmationEditText;
import com.discover.mobile.common.customui.EmailEditText;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.HeaderProgressIndicator;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.register.PushRegistrationStatusSuccessListener;
import com.xtify.sdk.api.XtifySDK;

/**
 * CreateLoginActivity - this is the final step of a user either going through "Forgot Both" or "Register".
 * This activity takes all of the information submitted from step 1 and adds it to the information gathered on
 * this activity. Then all of that information together is submitted to register (or re-register) the user.
 * 
 * @author scottseward
 *
 */
public class CreateLoginActivity extends NotLoggedInRoboActivity {
		
	private CreateLoginDetails formDataTwo;
	private RegistrationConfirmationDetails confirmationDetails;
	
	private final static String UPDATE_PASS_CONFIRM_STATE = "a";
	
	private final static String UPDATE_PASSWORD_STATE = "k";
	private final static String UPDATE_ID_STATE = "l";

//ERROR LABELS
	private TextView mainErrorMessageLabel;
	private TextView mainErrorMessageLabelTwo;
	private TextView errorMessageLabel;
	private TextView idConfirmErrorLabel;
	private TextView passConfirmErrorLabel;
	private TextView emailErrorLabel;

//SCROLL VIEW
	private ScrollView mainScrollView;
		
//INPUT FIELDS
	private EmailEditText emailField;
	private CredentialStrengthEditText idField;
	private ConfirmationEditText idConfirmField;
	private CredentialStrengthEditText passField;
	private ConfirmationEditText passConfirmField;
	
//HEADER PROGRESS BAR
	private HeaderProgressIndicator headerProgressIndicator;
	
//BUTTONS
	private Context currentContext;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_create_credentials);
		loadAllViews();

		attachErrorLabelsToFields();
		mergeAccountDetails();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_STEP2);
		setupStrengthBars();
		setupConfirmationFields();

		setupHeaderProgress();
		setupHelpNumber();
		restoreState(savedInstanceState);

	}
	
	/**
	 * Save the state of the screen.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);

		outState.putBoolean(UPDATE_ID_STATE, idField.isInDefaultState);
		
		outState.putBoolean(UPDATE_PASSWORD_STATE, passField.isInDefaultState);
		outState.putBoolean(UPDATE_PASS_CONFIRM_STATE, passConfirmField.isInDefaultState);
	}
	

	
	/**
	 * Resore the state of the screen.
	 * 
	 * @param savedInstanceState - a Bundle containing saved state information.
	 */
	public void restoreState(final Bundle savedInstanceState){
		if(savedInstanceState != null){
			idField.isInDefaultState = savedInstanceState.getBoolean(UPDATE_ID_STATE);
			passField.isInDefaultState = savedInstanceState.getBoolean(UPDATE_PASSWORD_STATE);
			passConfirmField.isInDefaultState = savedInstanceState.getBoolean(UPDATE_PASS_CONFIRM_STATE);
		}
	}
	
	/**
	 * Attach error lables to be hidden/shown for these input fields based on the valididty of their input.
	 */
	private void attachErrorLabelsToFields() {
		emailField.attachErrorLabel(emailErrorLabel);
		idConfirmField.attachErrorLabel(idConfirmErrorLabel);
		passConfirmField.attachErrorLabel(passConfirmErrorLabel);

	}
	
	/**
	 * Checks to see if all information on the screen is valid.
	 * @return Returns true if all information on the screen is valid.
	 */
	private boolean isFormCompleteAndValid() {
		return emailField.isValid() && 
				idField.isValid() && idConfirmField.isValid() && 
				passField.isValid() && passConfirmField.isValid();
	}
	
	/**
	 * Assign all local variables to view elements that we will need to access.
	 */
	private void loadAllViews() {
		passConfirmField = (ConfirmationEditText) findViewById(R.id.account_info_two_pass_confirm_field);
		passField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_pass_field);
		idConfirmField = (ConfirmationEditText)findViewById(R.id.account_info_two_id_confirm_field);
		idField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_id_field);
		emailField = (EmailEditText)findViewById(R.id.account_info_two_email_field);
		
		mainErrorMessageLabelTwo = (TextView)findViewById(R.id.account_info_error_label_two);
		errorMessageLabel = (TextView)findViewById(R.id.account_info_id_confirm_error_label);
		mainErrorMessageLabel = (TextView)findViewById(R.id.account_info_main_error_label);
		idConfirmErrorLabel = (TextView)findViewById(R.id.account_info_id_confirm_error_label);
		emailErrorLabel = (TextView)findViewById(R.id.account_info_email_error_label);
		passConfirmErrorLabel = (TextView)findViewById(R.id.account_info_pass_two_confirm_error_label);
		
		mainScrollView = (ScrollView)findViewById(R.id.main_scroll);

		headerProgressIndicator = (HeaderProgressIndicator)findViewById(R.id.header);

	}

	/**
	 * Attach password and id confirmation fields to their respective primary fields.
	 */
	private void setupConfirmationFields() {
		idConfirmField.attachEditTextToMatch(idField);
		passConfirmField.attachEditTextToMatch(passField);
	}
	
	/**
	 * Setup the header progress UI element. With proper text and showing that we are on step 2 of a 3 step process.
	 */
	private void setupHeaderProgress() {
		headerProgressIndicator.initChangePasswordHeader(1);
		headerProgressIndicator.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
		headerProgressIndicator.setPosition(1);
	}
	
	/**
	 * Make the help number at the bottom of the screen clickable and when clicked, dial its number.
	 */
	private void setupHelpNumber() {
		currentContext = this;
		final TextView helpText = (TextView)findViewById(R.id.help_number_label);
		helpText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonMethods.dialNumber(helpText.getText().toString(), currentContext);
			}
		});
	}
	
	/**
	 * Set the type of input that the strength bars should check against.
	 */
	private void setupStrengthBars() {
		idField.setCredentialType(CredentialStrengthEditText.USERID);
		passField.setCredentialType(CredentialStrengthEditText.PASSWORD);
	}
	
	/**
	 * Take the account details POJO from step 1 and merge it into a POJO for step 2.
	 */
	private void mergeAccountDetails() {
		formDataTwo = new CreateLoginDetails();
		AccountInformationDetails formDataOne = 
				(AccountInformationDetails)getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);

		formDataTwo.acctNbr = formDataOne.acctNbr;
		formDataTwo.dateOfBirthDay = formDataOne.dateOfBirthDay;
		formDataTwo.dateOfBirthMonth = formDataOne.dateOfBirthMonth;
		formDataTwo.dateOfBirthYear = formDataOne.dateOfBirthYear;
		formDataTwo.expirationMonth = formDataOne.expirationMonth;
		formDataTwo.expirationYear = formDataOne.expirationYear;
		formDataTwo.socialSecurityNumber = formDataOne.socialSecurityNumber;
	
	}

	/**
	 * Called from XML when the cancel link is pressed. 
	 * @param v
	 */
	public void cancel(final View v) {
		goBack();
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
	
	/**
	 * If all of the form information is complete, then save the info in our POJO and submit it to the server
	 * for server side validation. Otherwise, scroll to the top of the page and display an error.
	 * @param v
	 */
	public void checkInputsThenSubmit(final View v){
		CommonMethods.setViewGone(mainErrorMessageLabel);
		
		emailField.updateAppearanceForInput();
		passField.updateAppearanceForInput();
		idField.updateAppearanceForInput();
		passConfirmField.updateAppearanceForInput();
		idConfirmField.updateAppearanceForInput();
		
		if(isFormCompleteAndValid()){
			formDataTwo.email = emailField.getText().toString();
			formDataTwo.password = passField.getText().toString();
			formDataTwo.passwordConfirm = formDataTwo.password;
			formDataTwo.userId = idField.getText().toString();
			formDataTwo.userIdConfirm = formDataTwo.userId;
			submitFormInfo();
		}
		else {
			mainScrollView.smoothScrollTo(0, 0);
			CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
		}
			
	}
	
	private static final int PASSWORD_STRENGTH_HELP = 0;
	private static final int UID_STRENGTH_HELP = 1;
	/**
	 * Show the help screen for the password strength.
	 * @param v
	 */
	public void showPasswordStrengthBarHelp(final View v){
		final Intent passwordHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
		passwordHelpScreen.putExtra("ScreenType", "pass");
		TrackingHelper.trackPageView(AnalyticsPage.PASSWORD_STRENGTH_HELP);
		startActivityForResult(passwordHelpScreen, PASSWORD_STRENGTH_HELP);
	}
	
	/**
	 * Show the help screen for the user id strength.
	 * @param v
	 */
	public void showIdStrengthBarHelp(final View v){
		final Intent uidHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
		uidHelpScreen.putExtra("ScreenType", "id");
		TrackingHelper.trackPageView(AnalyticsPage.UID_STRENGTH_HELP);
		startActivityForResult(uidHelpScreen, UID_STRENGTH_HELP);
	}
	
	/**
	 * Submit all of the information present on this screen along with the information from
	 * register/forgot both step 1. On success, retrieve the users account information.
	 */
	private void submitFormInfo() {
		final ProgressDialog progress = 
				ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = 
				new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {
			@Override
			public void success(final RegistrationConfirmationDetails responseData) {
				progress.dismiss();
				confirmationDetails = responseData;
				getAccountDetails();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				mainScrollView.smoothScrollTo(0, 0);

				switch (errorResponse.getHttpStatusCode()) {
					default:
						CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.unkown_error_text, currentContext);
						return true;
				}
				
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				mainScrollView.smoothScrollTo(0, 0);

				switch(messageErrorResponse.getMessageStatusCode()){
				case REG_AUTHENTICATION_PROBLEM: //Provided information was incorrect.
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					return true;
				case BAD_ACCOUNT_STATUS: //Last attempt with this account number warning.
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.login_attempt_warning, currentContext);
					return true;
				case ID_AND_PASS_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabelTwo, R.string.account_info_two_id_matches_pass_error_text, currentContext);
					return true;
				case ID_AND_SSN_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabelTwo, R.string.id_and_ssn_match_text, currentContext);
					return true;
				case ID_ALREADY_TAKEN:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabelTwo, R.string.account_info_two_username_in_use_error_text, currentContext);
					return true;
				case PLANNED_OUTAGE:
					sendToErrorPage(ScreenType.SCHEDULED_MAINTENANCE);
					return true;
					
				default:
					return false;
				}
			}
		};
				
		final CreateLoginCall registrationCall = 
				new CreateLoginCall(this, callback, formDataTwo);
		registrationCall.submit();

	}
	
	/**
	 * Sends a user to a modal 'lockout' screen. This terminates the registration process.
	 * @param screenType
	 */
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
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
					.clearTextViewsOnComplete(idField, passField)
					.build();
		
			new GetPushRegistrationStatus(this, callback).submit();
		}else{
			navigateToConfirmationScreenWithResponseData(confirmationDetails);
			finish();
		}

	}
					
	@Override
	public void goBack() {
		Intent login = new Intent(this, LoginActivity.class);
		startActivity(login);
		finish();
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

	@Override
	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
