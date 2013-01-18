package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_ALREADY_TAKEN;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_SSN_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;

import java.net.HttpURLConnection;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
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
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.navigation.HeaderProgressIndicator;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * CreateLoginActivity - this is the final step of a user either going through "Forgot Both" or "Register".
 * This activity takes all of the information submitted from step 1 and adds it to the information gathered on
 * this activity. Then all of that information together is submitted to register (or re-register) the user.
 * 
 * @author scottseward
 *
 */
public class CreateLoginActivity extends NotLoggedInRoboActivity {
	
	// FIXME replace all extra sets/gets with ScreenType references (constants)
	
	private CreateLoginDetails formDataTwo;

//ERROR LABELS
	private TextView mainErrorMessageLabel;
	private TextView errorMessageLabel;
	private TextView idConfirmErrorLabel;
	private TextView passErrorLabel;
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
	private Button continueButton;
	
	private Context currentContext;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_create_credentials);
		loadAllViews();
		attachDisabledButtonListeners();
		attachErrorLabelsToFields();
		mergeAccountDetails();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_STEP2);
		setupStrengthBars();
		setupConfirmationFields();

		setupHeaderProgress();
		setupHelpNumber();
	}
	
	/** 
	 * A text watcher that allows every form on the screen to listen for 
	 * when the form info is complete and then update the submit button to enabled
	 */
	private TextWatcher getContinueButtonTextWatcher() {
		return new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if(isFormCompleteAndValid())
					continueButton.setEnabled(true);
				else
					continueButton.setEnabled(false);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {}
		};
	}
	
	private void attachDisabledButtonListeners() {
		emailField.addTextChangedListener(getContinueButtonTextWatcher());
		idField.addTextChangedListener(getContinueButtonTextWatcher());
		idConfirmField.addTextChangedListener(getContinueButtonTextWatcher());
		idConfirmField.setIsUserIdConfirmation(true);
		passField.addTextChangedListener(getContinueButtonTextWatcher());
		passConfirmField.addTextChangedListener(getContinueButtonTextWatcher());
		
	}
	
	private void attachErrorLabelsToFields() {
		emailField.attachErrorLabel(emailErrorLabel);
	}
	
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
		emailErrorLabel = (TextView)findViewById(R.id.account_info_email_error_label);
		passConfirmErrorLabel = (TextView)findViewById(R.id.account_info_pass_two_confirm_error_label);
		passErrorLabel = (TextView)findViewById(R.id.account_info_id_pass_error_label);
		idConfirmErrorLabel = (TextView)findViewById(R.id.account_info_id_confirm_error_label);
		
		errorMessageLabel = (TextView)findViewById(R.id.account_info_id_confirm_error_label);
		mainErrorMessageLabel = (TextView)findViewById(R.id.account_info_main_error_label);
		headerProgressIndicator = (HeaderProgressIndicator)findViewById(R.id.header);
		continueButton  = (Button)findViewById(R.id.account_info_two_submit_button);
		
		mainScrollView = (ScrollView)findViewById(R.id.main_scroll);
	}
	
	private void setupConfirmationFields() {
		idConfirmField.attachEditTextToMatch(idField);
		idConfirmField.attachErrorLabel(idConfirmErrorLabel);
		
		passConfirmField.attachEditTextToMatch(passField);
		passConfirmField.attachErrorLabel(passConfirmErrorLabel);
	}
	private void setupHeaderProgress() {
		headerProgressIndicator.initChangePasswordHeader(1);
		headerProgressIndicator.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
		headerProgressIndicator.setPosition(1);
	}
	
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
	
	private void setupStrengthBars() {
		idField.setCredentialType(CredentialStrengthEditText.USERID);
		passField.setCredentialType(CredentialStrengthEditText.PASSWORD);
	}
	
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

	public void cancel(final View v) {
		goBack();
	}
	
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
	
	public void checkInputsThenSubmit(final View v){

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
	
	public void showPasswordStrengthBarHelp(final View v){
		final Intent passwordHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
		passwordHelpScreen.putExtra("ScreenType", "pass");
		TrackingHelper.trackPageView(AnalyticsPage.PASSWORD_STRENGTH_HELP);
		startActivityForResult(passwordHelpScreen, PASSWORD_STRENGTH_HELP);
	}
	
	public void showIdStrengthBarHelp(final View v){
		final Intent uidHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
		uidHelpScreen.putExtra("ScreenType", "id");
		TrackingHelper.trackPageView(AnalyticsPage.UID_STRENGTH_HELP);
		startActivityForResult(uidHelpScreen, UID_STRENGTH_HELP);
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {		
		//need not do anything we dont care about the result from these screens.
	}

	public void setInputToLowerCase(final CharSequence input, final EditText field){
		final String inputString = input.toString();
		final String lowerCaseInput = inputString.toLowerCase(Locale.getDefault());
		
		if( !inputString.equals(lowerCaseInput)){
			field.setText(lowerCaseInput);
			field.setSelection(lowerCaseInput.length());
		}
		
	}
	
	private void submitFormInfo() {
		final ProgressDialog progress = 
				ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = 
				new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {
			@Override
			public void success(final RegistrationConfirmationDetails responseData) {
				progress.dismiss();
				navigateToConfirmationScreenWithResponseData(responseData);
				finish();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				
				switch(messageErrorResponse.getMessageStatusCode()){
				case REG_AUTHENTICATION_PROBLEM: //Provided information was incorrect.
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					return true;
				case BAD_ACCOUNT_STATUS: //Last attemt with this account number warning.
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.login_attempt_warning, currentContext);
					return true;
				case ID_AND_PASS_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_id_matches_pass_error_text, currentContext);
					return true;
				case ID_AND_SSN_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.id_and_ssn_match_text, currentContext);
					return true;
				case ID_ALREADY_TAKEN:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_username_in_use_error_text, currentContext);
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
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
	}

	@Override
	public void goBack() {
		finish();
	}
}
