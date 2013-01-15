package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_ALREADY_TAKEN;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_SSN_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.CreateLoginCall;
import com.discover.mobile.common.auth.registration.CreateLoginDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.navigation.NavigationRootActivity;

/**
 * CreateLoginActivity - this is the final step of a user either going through "Forgot Both" or "Register".
 * This activity takes all of the information submitted from step 1 and adds it to the information gathered on
 * this activity. Then all of that information together is submitted to register (or re-register) the user.
 * 
 * @author scottseward
 *
 */
public class CreateLoginActivity extends RoboActivity {
	
	// FIXME replace all extra sets/gets with ScreenType references (constants)
	
	private CreateLoginDetails formDataTwo;

//ERROR LABELS
	private TextView mainErrorMessageLabel;
	private TextView errorMessageLabel;
	private TextView idConfirmErrorLabel;
	private TextView passErrorLabel;
	private TextView passConfirmErrorLabel;
	private TextView emailErrorLabel;

		
//INPUT FIELDS
	private EditText emailField;
	private CredentialStrengthEditText idField;
	private EditText idConfirmField;
	private CredentialStrengthEditText passField;
	private EditText passConfirmField;
	
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_create_credentials);
		
		loadAllViews();
						
//		mergeAccountDetails();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_STEP2);
		setupStrengthBars();
		
	}
	
	/**
	 * Assign all local variables to view elements that we will need to access.
	 */
	private void loadAllViews() {
		passConfirmField = (EditText) findViewById(R.id.account_info_two_pass_confirm_field);
		passField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_pass_field);
		idConfirmField = (EditText)findViewById(R.id.account_info_two_id_confirm_field);
		idField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_id_field);
		emailField = (EditText)findViewById(R.id.account_info_two_email_field);
		emailErrorLabel = (TextView)findViewById(R.id.account_info_email_error_label);
		passConfirmErrorLabel = (TextView)findViewById(R.id.account_info_pass_two_confirm_error_label);
		passErrorLabel = (TextView)findViewById(R.id.account_info_id_pass_error_label);
		idConfirmErrorLabel = (TextView)findViewById(R.id.account_info_id_confirm_error_label);
		
		errorMessageLabel = (TextView)findViewById(R.id.account_info_id_confirm_error_label);
		mainErrorMessageLabel = (TextView)findViewById(R.id.account_info_main_error_label);
	}
	
	private void setupStrengthBars() {
		idField.setCredentialType(CredentialStrengthEditText.USERID);
		passField.setCredentialType(CredentialStrengthEditText.PASSWORD);
	}
	
	private void mergeAccountDetails() {
		formDataTwo = new CreateLoginDetails();

		final AccountInformationDetails formDataOne = 
				(AccountInformationDetails)getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);

		formDataTwo.acctNbr = formDataOne.acctNbr;
		formDataTwo.dateOfBirthDay = formDataOne.dateOfBirthDay;
		formDataTwo.dateOfBirthMonth = formDataOne.dateOfBirthMonth;
		formDataTwo.dateOfBirthYear = formDataOne.dateOfBirthYear;
		formDataTwo.expirationMonth = formDataOne.expirationMonth;
		formDataTwo.expirationYear = formDataOne.expirationYear;
		formDataTwo.socialSecurityNumber = formDataOne.socialSecurityNumber;
	
	}
	
	@Override
	public void onBackPressed() {
		cancel(null);
	}

	public void cancel(final View v) {

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
		final InputValidator validator = new InputValidator();
		
		final String email = emailField.getText().toString();
		final String id1 = idField.getText().toString();
		final String id2 = idConfirmField.getText().toString();
		final String pass1 = passField.getText().toString();
		final String pass2 = passConfirmField.getText().toString();
		
		validator.doPassesMatch(pass1, pass2);
		validator.doIdsMatch(id1,id2);
		validator.isEmailValid(email);
		validator.doPassAndIdMatch(pass1,id1); 
		validator.isUidValid(id1);

		updateErrorLabelsUsingValidator(validator);
		
		if(validator.wasAccountTwoInfoComplete()){
			formDataTwo.email = email;
			formDataTwo.password = pass1;
			formDataTwo.passwordConfirm = formDataTwo.password;
			formDataTwo.userId = id1;
			formDataTwo.userIdConfirm = formDataTwo.userId;
			submitFormInfo();
		}
		else {
			showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text);
		}
			
	}
	
	private void updateErrorLabelsUsingValidator(final InputValidator validator) {
		hideAllErrorLabels();
		
		if( !validator.didIdsMatch ) {
			showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_ids_must_match_text);
			showLabelWithStringResource(idConfirmErrorLabel, R.string.doesnt_match_records);
		}
		else {
			hideLabel(errorMessageLabel);
			hideLabel(idConfirmErrorLabel);
		}
		
		if( !validator.didPassesMatch ){
			showLabelWithStringResource(passErrorLabel, R.string.account_info_two_passwords_dont_match_text);
			showLabelWithStringResource(passConfirmErrorLabel, R.string.doesnt_match_records);
		}
		else {
			hideLabel(passConfirmErrorLabel);
		}
		
		if( !validator.wasEmailValid ){
			showLabelWithStringResource(emailErrorLabel, R.string.doesnt_match_records);
		}
		else {
			hideLabel(emailErrorLabel);
		}
		
		if( validator.didPassAndIdMatch ) {
			showLabelWithStringResource(errorMessageLabel, R.string.id_and_pass_match);
		}
	}
	
	private void hideAllErrorLabels() {
		hideLabel(errorMessageLabel);
		hideLabel(idConfirmErrorLabel);
		hideLabel(passErrorLabel);
		hideLabel(passConfirmErrorLabel);
		hideLabel(emailErrorLabel);
		
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

	private void setupTextChangedListeners(){
	
//		passField.addTextChangedListener(new TextWatcher() {
//
//
//			@Override
//			public void beforeTextChanged(final CharSequence s, final int start, final int count,
//					final int after) {/*not used*/}
//
//			@Override
//			public void onTextChanged(final CharSequence inputText, final int start, final int before,
//					final int count) {
//				updateBarsForPass(inputText, passBarOne, passBarTwo, passBarThree, passStrengthBarLabel);
//			}
//
//			@Override
//			public void afterTextChanged(final Editable s) {/*not used*/}
//		});
//		
//		idField.addTextChangedListener(new TextWatcher(){
//			InputValidator validator = new InputValidator();
//
//			@Override
//			public void afterTextChanged(final Editable s) {/*not used*/}
//			
//			@Override
//			public void beforeTextChanged(final CharSequence s, final int start, final int count,
//					final int after) {/*not used*/}
//
//			@Override
//			public void onTextChanged(final CharSequence inputText, final int start, final int before, 
//			final int count) {
//				updateBarsForUID(inputText, idBarOne, idBarTwo, idBarThree, idStrengthBarLabel);
//				setInputToLowerCase(inputText, idField);
//				if(validator.isUidValid(idField.getText().toString())) {
//					hideLabel(errorMessageLabel);
//				}
//			}
//		});
//		
//		idField.setOnFocusChangeListener(new OnFocusChangeListener() {
//			InputValidator validator = new InputValidator();
//			
//			@Override
//			public void onFocusChange(final View v, final boolean hasFocus) {
//				if(!hasFocus && !validator.isUidValid(idField.getText().toString())) {
//					showLabelWithStringResource(errorMessageLabel, R.string.doesnt_match_records);
//				}
//			}
//		});
//		
//		idConfirmField.setOnFocusChangeListener(new OnFocusChangeListener() {
//			InputValidator validator = new InputValidator();
//			
//			@Override
//			public void onFocusChange(final View v, final boolean hasFocus) {
//				if(!hasFocus && !validator.isUidValid(idConfirmField.getText().toString())) {
//					showLabelWithStringResource(idConfirmErrorLabel, R.string.doesnt_match_records);
//				}
//			}
//		});
//		
//		idConfirmField.addTextChangedListener(new TextWatcher(){
//			InputValidator validator = new InputValidator();
//
//			@Override
//			public void afterTextChanged(final Editable s) {/*not used*/}
//
//			@Override
//			public void beforeTextChanged(final CharSequence s, final int start, final int count,
//					final int after) {/*not used*/}
//
//			@Override
//			public void onTextChanged(final CharSequence s, final int start, final int before,
//					final int count) {
//				setInputToLowerCase(s, idConfirmField);
//				if(validator.isUidValid(idConfirmField.getText().toString())) {
//					hideLabel(idConfirmErrorLabel);
//				}
//				
//			}
//			
//		});
	}

//	public void setInputToLowerCase(final CharSequence input, final EditText field){
//		final String inputString = input.toString();
//		final String lowerCaseInput = inputString.toLowerCase(Locale.getDefault());
//		
//		if( !inputString.equals(lowerCaseInput)){
//			field.setText(lowerCaseInput);
//			field.setSelection(lowerCaseInput.length());
//		}
//		
//	}
	
	// FIXME revise the following gigantic methods
	
	//Currently setup only for a single user id input.
//	public void updateBarsForUID(final CharSequence inputSequence, 
//									final View barOne, final View barTwo, final View barThree, final TextView label) {
//		
//		boolean hasGoodLength   = false;
//		boolean hasUpperCase    = false;
//		boolean hasLowerCase    = false;
//		boolean hasNonAlphaNum  = false;
//		boolean hasInvalidChar  = false;
//		boolean hasNumber 	    = false;
//		boolean looksLikeActNum = false;
//		
//		boolean passAndIdMatch = idField.getText().equals(passField.getText());
//		
//		//Check length of input.
//		if(inputSequence.length() >= 6 && inputSequence.length() <= 16)
//			hasGoodLength = true;
//		
//			for(int i = 0; i < inputSequence.length(); ++i){
//				
//				if(Character.isLowerCase(inputSequence.charAt(i))){
//					hasLowerCase = true;
//				}
//				else if (Character.isUpperCase(inputSequence.charAt(i))){
//					hasUpperCase = true;
//				}
//				else if (Character.isDigit(inputSequence.charAt(i))){
//					hasNumber = true;
//				}
//				else if(inputSequence.charAt(i) == '\\' ||
//						inputSequence.charAt(i) == '`'  ||
//						inputSequence.charAt(i) == '\'' ||
//						inputSequence.charAt(i) == '\"' ||
//						inputSequence.charAt(i) == ' '){
//					hasInvalidChar = true;
//				}
//				else if(!Character.isLetterOrDigit(inputSequence.charAt(i))){
//					hasNonAlphaNum = true;
//				}
//			}
//			
//			if(inputSequence.toString().startsWith("6011")){
//				looksLikeActNum = true;
//				showLabelWithStringResource(errorMessageLabel, R.string.doesnt_match_records);
//			}
//			
//			/*
//			 * Meets minimum requirements and combines a variation of letters, numbers, and special characters.
//			 */
//			if(!passAndIdMatch && !looksLikeActNum && !hasInvalidChar && hasGoodLength && (hasLowerCase || hasUpperCase) && hasNonAlphaNum && hasNumber){
//				barOne.setBackgroundColor(getResources().getColor(R.color.green));		
//				barTwo.setBackgroundColor(getResources().getColor(R.color.green));
//				barThree.setBackgroundColor(getResources().getColor(R.color.green));
//				label.setText(getResources().getString(R.string.strength_bar_strong));
//			}
//			/*
//			 * Meets minimum requirements but does not include a variation of letters, numbers, and special characters.
//			 */
//			else if(!passAndIdMatch && !looksLikeActNum && !hasInvalidChar && hasGoodLength){
//				barOne.setBackgroundColor(getResources().getColor(R.color.yellow));		
//				barTwo.setBackgroundColor(getResources().getColor(R.color.yellow));
//				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
//				label.setText(getResources().getString(R.string.strength_bar_moderate));
//			}
//			/*
//			 * Does not meet minimum requirements (not 6-16 characters, 
//			 * looks like an account number, or uses spaces or the following characters: (`)(')(")(\))
//			 */
//			else {
//				barOne.setBackgroundColor(getResources().getColor(R.color.red));		
//				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
//				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
//				label.setText(getResources().getString(R.string.strength_bar_not_valid));
//			}
//			
//	}

	
//	public void updateBarsForPass(final CharSequence inputSequence, 
//									final View barOne, final View barTwo, final View barThree, final TextView label){
//		boolean hasGoodLength  = false;
//		boolean hasUpperCase   = false;
//		boolean hasLowerCase   = false;
//		boolean hasNonAlphaNum = false;
//		boolean hasNumber 	   = false;
//		
//		boolean passAndIdMatch = idField.getText().equals(passField.getText());
//				
//		//Check length of input.
//		if(inputSequence.length() >= 8 && inputSequence.length() <= 32)
//			hasGoodLength = true;
//					
//		//A password must have at least 1 letter and 1 number and cannot be 'password'
//		//but password doesn't have a number...
//			for(int i = 0; i < inputSequence.length(); ++i){
//				
//				if(Character.isLowerCase(inputSequence.charAt(i))){
//					hasLowerCase = true;
//				}
//				else if (Character.isUpperCase(inputSequence.charAt(i))){
//					hasUpperCase = true;
//				}
//				else if (Character.isDigit(inputSequence.charAt(i))){
//					hasNumber = true;
//				}				
//				else if(!Character.isLetterOrDigit(inputSequence.charAt(i))){
//					hasNonAlphaNum = true;
//				}
//			}
//			
//			final boolean hasUpperAndLowerAndNum = hasLowerCase && hasUpperCase && hasNumber;
//			/*
//			 * Meets minimum requirements and combines upper case letters,
//			 * lower case letters, numbers, and special characters.
//			 */
//			if(!passAndIdMatch && hasGoodLength && hasUpperAndLowerAndNum && hasNonAlphaNum){
//				barOne.setBackgroundColor(getResources().getColor(R.color.green));		
//				barTwo.setBackgroundColor(getResources().getColor(R.color.green));
//				barThree.setBackgroundColor(getResources().getColor(R.color.green));
//				label.setText(getResources().getString(R.string.strength_bar_strong));
//			}
//			/*
//			 * Meets minimum requirements but does not include a
//			 * variation of upper case letters, lower case letters, numbers, and special characters.
//			 */
//			else if(!passAndIdMatch && hasGoodLength && hasNumber && (hasUpperCase || hasLowerCase)){
//				barOne.setBackgroundColor(getResources().getColor(R.color.yellow));		
//				barTwo.setBackgroundColor(getResources().getColor(R.color.yellow));
//				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
//				label.setText(getResources().getString(R.string.strength_bar_moderate));
//			}
//			/*
//			 * Does not meet minimum requirements (not 8-32 characters, 
//			 * does not contain at least 1 letter and 1 number, or is the word "password").
//			 */
//			else{
//				barOne.setBackgroundColor(getResources().getColor(R.color.red));		
//				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
//				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
//				label.setText(getResources().getString(R.string.strength_bar_not_valid));
//			}
//			
//	}
	
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
					showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text);
					return true;
				case BAD_ACCOUNT_STATUS: //Last attemt with this account number warning.
					showLabelWithStringResource(errorMessageLabel, R.string.login_attempt_warning);
					return true;
				case ID_AND_PASS_EQUAL:
					showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text);
					showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_id_matches_pass_error_text);
					return true;
				case ID_AND_SSN_EQUAL:
					showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text);
					showLabelWithStringResource(errorMessageLabel, R.string.id_and_ssn_match_text);
					return true;
				case ID_ALREADY_TAKEN:
					showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text);
					showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_username_in_use_error_text);
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
	
	
	private void showLabelWithStringResource(final TextView label, final int stringResource) {
		label.setText(getString(stringResource));
		showLabel(label);
	}
	
	private void showLabel(final View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	private void hideLabel(final View v) {
		v.setVisibility(View.GONE);
	}
}
