package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_ALREADY_TAKEN;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_SSN_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.CreateLoginCall;
import com.discover.mobile.common.auth.registration.CreateLoginDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.ConfirmationEditText;
import com.discover.mobile.common.customui.EmailEditText;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.navigation.HeaderProgressIndicator;

/**
 * CreateLoginActivity - this is the final step of a user either going through "Forgot Both" or "Register".
 * This activity takes all of the information submitted from step 1 and adds it to the information gathered on
 * this activity. Then all of that information together is submitted to register (or re-register) the user.
 * 
 * @author scottseward
 *
 */
public class CreateLoginActivity extends ForgotOrRegisterFinalStep {
		
	private CreateLoginDetails formDataTwo;
	
	private final static String UPDATE_PASS_CONFIRM_STATE = "a";
	private final static String UPDATE_ID_CONFIRM_STATE ="b";
	private final static String UPDATE_EMAIL = "c";
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
	
	private boolean idIsError;
	private boolean isPassError;
	private boolean isEmailError;
	
//HEADER PROGRESS BAR
	private HeaderProgressIndicator headerProgressIndicator;
	
//BUTTONS
	private Activity currentActivity = this;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		super.isForgot = false;
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
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();
		if(isEmailError){
			emailField.setErrors();
		}
		if(idIsError){
			idConfirmField.setErrors();
		}
		if(isPassError){
			passConfirmField.setErrors();
		}
	}
	
	/**
	 * Save the state of the screen.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);

		outState.putBoolean(UPDATE_ID_STATE, idField.isInDefaultState);
		outState.putBoolean(UPDATE_EMAIL, emailField.isInErrorState);
		outState.putBoolean(UPDATE_ID_CONFIRM_STATE, idConfirmField.isInErrorState);
		outState.putBoolean(UPDATE_PASSWORD_STATE, passField.isInDefaultState);
		outState.putBoolean(UPDATE_PASS_CONFIRM_STATE, passConfirmField.isInErrorState);
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

			idIsError = savedInstanceState.getBoolean(UPDATE_ID_CONFIRM_STATE);
			isPassError = savedInstanceState.getBoolean(UPDATE_PASS_CONFIRM_STATE);
			isEmailError = savedInstanceState.getBoolean(UPDATE_EMAIL);
			
		}
	}
	
	/**
	 * Set the error states if needed
	 */
	private void setErrorStates(){
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
			CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentActivity);
		}
			
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
				retrieveAccountDetailsFromServer();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				mainScrollView.smoothScrollTo(0, 0);

				switch (errorResponse.getHttpStatusCode()) {
					default:
						CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.unkown_error_text, currentActivity);
						return true;
				}
				
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				mainScrollView.smoothScrollTo(0, 0);

				switch(messageErrorResponse.getMessageStatusCode()){
				case REG_AUTHENTICATION_PROBLEM: //Provided information was incorrect.
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text, currentActivity);
					return true;
				case BAD_ACCOUNT_STATUS: //Last attempt with this account number warning.
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.login_attempt_warning, currentActivity);
					return true;
				case ID_AND_PASS_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentActivity);
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabelTwo, R.string.account_info_two_id_matches_pass_error_text, currentActivity);
					return true;
				case ID_AND_SSN_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentActivity);
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabelTwo, R.string.id_and_ssn_match_text, currentActivity);
					return true;
				case ID_ALREADY_TAKEN:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentActivity);
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabelTwo, R.string.account_info_two_username_in_use_error_text, currentActivity);
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

}
