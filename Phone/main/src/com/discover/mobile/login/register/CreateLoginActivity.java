package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_ALREADY_TAKEN;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_SSN_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.CreateLoginCall;
import com.discover.mobile.common.auth.registration.CreateLoginDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.ConfirmationEditText;
import com.discover.mobile.common.customui.EmailEditText;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.error.BaseExceptionFailureHandler;
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
	private final String TAG = ForgotOrRegisterFinalStep.class.getSimpleName();
	
	private CreateLoginDetails formDataTwo;
	
	private final String UPDATE_PASS_CONFIRM_STATE = "a";
	private final String UPDATE_ID_CONFIRM_STATE ="b";
	private final String UPDATE_EMAIL = "c";
	private final String ERROR_1 = "d";
	private final String EROR_STRING_1 = "e";
	private final String ERROR_2 = "f";
	private final String ERROR_STRING_2 = "g";
	private final String SERVER_ERROR = "h";
	private final String SERVER_ERROR_STRING = "i";
	private final String UPDATE_PASSWORD_STATE = "k";
	private final String UPDATE_ID_STATE = "l";
	
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
	
	private boolean idIsError = false;
	private boolean isPassError = false;
	private boolean isEmailError = false;
	private boolean isError1 = false;
	private boolean isError2 = false;
	private boolean isServerError = false;
	
//HEADER PROGRESS BAR
	private HeaderProgressIndicator headerProgressIndicator;
	
//BUTTONS
	private final Activity currentActivity = this;
	
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
		getPreviousScreenType();

		setupHeaderProgress();
		setupHelpNumber();
		restoreState(savedInstanceState);
	}
	
	protected void getPreviousScreenType() {
		isForgotFlow = getIntent().getBooleanExtra(IntentExtraKey.SCREEN_FORGOT_BOTH, false);
	}
	
	/**
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();
		showErrors();
	}
	
	private void showErrors() {
		if(isEmailError){
			emailField.setErrors();
		}
		if(idIsError){
			idConfirmField.setErrors();
		} else{
			idConfirmField.setupDefaultAppearance();
		}
		if(isPassError){
			passConfirmField.setErrors();
		} else{
			passConfirmField.setupDefaultAppearance();
		}
		if(isError1){
			mainErrorMessageLabel.setVisibility(View.VISIBLE);
		}
		if(isError2){
			mainErrorMessageLabelTwo.setVisibility(View.VISIBLE);
		}
		if(isServerError){
			errorMessageLabel.setVisibility(View.VISIBLE);
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
		if(mainErrorMessageLabel.getVisibility() == View.VISIBLE){
			outState.putBoolean(ERROR_1, true);
			outState.putString(EROR_STRING_1, mainErrorMessageLabel.getText().toString());
		}
		if(mainErrorMessageLabelTwo.getVisibility() == View.VISIBLE){
			outState.putBoolean(ERROR_2, true);
			outState.putString(ERROR_STRING_2, mainErrorMessageLabelTwo.getText().toString());
		}
		if(errorMessageLabel.getVisibility() == View.VISIBLE){
			outState.putBoolean(SERVER_ERROR, true);
			outState.putString(SERVER_ERROR_STRING, errorMessageLabel.getText().toString());
		}
	}

	/**
	 * Restore the state of the screen.
	 * 
	 * @param savedInstanceState - a Bundle containing saved state information.
	 */
	public void restoreState(final Bundle savedInstanceState){
		if(savedInstanceState != null){
			idField.isInDefaultState = savedInstanceState.getBoolean(UPDATE_ID_STATE);
			passField.isInDefaultState = savedInstanceState.getBoolean(UPDATE_PASSWORD_STATE);

			idIsError = savedInstanceState.getBoolean(UPDATE_ID_CONFIRM_STATE, false);
			isPassError = savedInstanceState.getBoolean(UPDATE_PASS_CONFIRM_STATE, false);
			isEmailError = savedInstanceState.getBoolean(UPDATE_EMAIL, false);
			isError1 = savedInstanceState.getBoolean(ERROR_1, false);
			isError2 = savedInstanceState.getBoolean(ERROR_2, false);
			isServerError = savedInstanceState.getBoolean(SERVER_ERROR, false);
			mainErrorMessageLabel.setText(savedInstanceState.getString(EROR_STRING_1));
			mainErrorMessageLabelTwo.setText(savedInstanceState.getString(ERROR_STRING_2));
			errorMessageLabel.setText(savedInstanceState.getString(SERVER_ERROR_STRING));
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
		final AccountInformationDetails formDataOne = 
				(AccountInformationDetails)getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);

		if(formDataOne != null){
			formDataTwo = new CreateLoginDetails();

			formDataTwo.acctNbr = formDataOne.acctNbr;
			formDataTwo.dateOfBirthDay = formDataOne.dateOfBirthDay;
			formDataTwo.dateOfBirthMonth = formDataOne.dateOfBirthMonth;
			formDataTwo.dateOfBirthYear = formDataOne.dateOfBirthYear;
			formDataTwo.expirationMonth = formDataOne.expirationMonth;
			formDataTwo.expirationYear = formDataOne.expirationYear;
			formDataTwo.socialSecurityNumber = formDataOne.socialSecurityNumber;
		}else{
			Log.e(TAG, "UNABLE TO MERGE ACCOUNT DETAILS");
		}
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
		
		//Lock orientation while request is being processed
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = 
				new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {
			
			@Override
			public void complete(final NetworkServiceCall<?> sender, final Object result) {
				//Unlock orientation after request has been processed
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}
			
			@Override
			public void success(final NetworkServiceCall<?> sender, final RegistrationConfirmationDetails responseData) {
				progress.dismiss();
				confirmationDetails = responseData;
				retrieveAccountDetailsFromServer();
			}

			@Override
			public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
				progress.dismiss();
				mainScrollView.smoothScrollTo(0, 0);

				switch (errorResponse.getHttpStatusCode()) {
					default:
						Log.e(TAG, "Create Login submission error : " + errorResponse.toString());
						CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.unkown_error_text, currentActivity);
						return true;
				}
				
			}

			@Override
			public void failure(final NetworkServiceCall<?> sender, final Throwable executionException) {
				//Catch all exception handler
				final BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(sender, executionException);
			}
			
			@Override
			public boolean handleMessageErrorResponse(final NetworkServiceCall<?> sender, final JsonMessageErrorResponse messageErrorResponse) {
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
					showErrorModal(R.string.could_not_complete_request, R.string.unknown_error, false);
					return true;
					
				default:
					Log.e(TAG, "UNHANDLED ERROR " + messageErrorResponse.toString());
					return false;
				}
			}
		};
				
		final CreateLoginCall registrationCall = 
				new CreateLoginCall(this, callback, formDataTwo);
		registrationCall.submit();
	}

}
