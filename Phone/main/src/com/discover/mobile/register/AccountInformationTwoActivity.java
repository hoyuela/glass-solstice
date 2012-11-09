package com.discover.mobile.register;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.RegistrationCallTwo;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.auth.registration.RegistrationOneDetails;
import com.discover.mobile.common.auth.registration.RegistrationTwoDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.ErrorResponse;

public class AccountInformationTwoActivity extends Activity{

	private RegistrationOneDetails formDataOne;
	private RegistrationTwoDetails formDataTwo;
	private static final String TAG = AccountInformationTwoActivity.class.getSimpleName();

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_STEP2);
		
		setContentView(R.layout.account_info_two);
		formDataTwo = new RegistrationTwoDetails();
		
		if (savedInstanceState == null) {
			final Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		formDataOne = (RegistrationOneDetails)getIntent().getSerializableExtra("RegistrationOneDetails");
        		formDataTwo.acctNbr = formDataOne.acctNbr;
        		formDataTwo.dateOfBirthDay = formDataOne.dateOfBirthDay;
        		formDataTwo.dateOfBirthMonth = formDataOne.dateOfBirthMonth;
        		formDataTwo.dateOfBirthYear = formDataOne.dateOfBirthYear;
        		formDataTwo.expirationMonth = formDataOne.expirationMonth;
        		formDataTwo.expirationYear = formDataOne.expirationYear;
        		formDataTwo.socialSecurityNumber = formDataOne.socialSecurityNumber;
        	}
		}
		
		setupTextChangedListeners();
	}
	
	private void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData){
		final Intent confirmationScreen = new Intent(this, AccountInformationConfirmationActivity.class);
		confirmationScreen.putExtra("id", responseData.userId);
		confirmationScreen.putExtra("email", responseData.email);
		confirmationScreen.putExtra("acctNbr", responseData.acctLast4);
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_CONFIRMATION);
		this.startActivity(confirmationScreen);
	}
	
	public void checkInputsThenSubmit(final View v){
		final InputValidator validator = new InputValidator();
		final String email = ((EditText)findViewById(R.id.account_info_two_email_field)).getText().toString();
		final String id1 =((EditText)findViewById(R.id.account_info_two_id_field)).getText().toString();
		final String id2 = ((EditText)findViewById(R.id.account_info_two_id_confirm_field)).getText().toString();
		final String pass1=((EditText)findViewById(R.id.account_info_two_pass_field)).getText().toString();
		final String pass2=((EditText)findViewById(R.id.account_info_two_pass_confirm_field)).getText().toString();
		
		validator.doPassesMatch(
				pass1, pass2);
		
		validator.doIdsMatch(id1,id2);
		
		validator.isEmailValid(email);
		
		validator.doPassAndIdMatch(pass1,id1); 

		if(validator.wasAccountTwoInfoComplete()){
			
			formDataTwo.email = email;
			formDataTwo.password = pass1;
			formDataTwo.passwordConfirm = formDataTwo.password;
			formDataTwo.userId = id1;
			formDataTwo.userIdConfirm = formDataTwo.userId;
			submitFormInfo();
		}
		
	}
	
	public void showPasswordStrengthBarHelp(final View v){
		final Intent passwordHelpScreen = new Intent(this, AccountInformationHelpActivity.class);
		passwordHelpScreen.putExtra("helpType", "password");
		TrackingHelper.trackPageView(AnalyticsPage.PASSWORD_STRENGTH_HELP);
		this.startActivity(passwordHelpScreen);
	}
	
	public void showIdStrengthBarHelp(final View v){
		final Intent passwordHelpScreen = new Intent(this, AccountInformationHelpActivity.class);
		passwordHelpScreen.putExtra("helpType", "id");
		TrackingHelper.trackPageView(AnalyticsPage.UID_STRENGTH_HELP);
		this.startActivity(passwordHelpScreen);
	}
	
	private void setupTextChangedListeners(){
		final EditText idField = (EditText)findViewById(R.id.account_info_two_id_field);
		final EditText passField = (EditText)findViewById(R.id.account_info_two_pass_field);
		passField.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(final Editable s) {
				
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after) {
				
			}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				updateBarsForPass(s, findViewById(R.id.account_info_two_pass_bar_one), 
							  findViewById(R.id.account_info_two_pass_bar_two),
							  findViewById(R.id.account_info_two_pass_bar_three),
						      (TextView)findViewById(R.id.account_info_two_pass_strength_bar_label));
			}
		});
		
		idField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(final Editable s) {
				
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after) {
				
			}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				updateBarsForUID(s, findViewById(R.id.account_info_two_uid_bar_one), 
							  findViewById(R.id.account_info_two_uid_bar_two),
							  findViewById(R.id.account_info_two_uid_bar_three),
						      (TextView)findViewById(R.id.account_info_two_uid_strength_bar_label));
			}
			
		});
	}
	
	
	//Currently setup only for a single user id input.
	public void updateBarsForUID(final CharSequence inputSequence, final View barOne, final View barTwo, final View barThree, final TextView label){
		boolean hasGoodLength  = false;
		boolean hasUpperCase   = false;
		boolean hasLowerCase   = false;
		boolean hasNonAlphaNum = false;
		boolean hasInvalidChar = false;
		boolean hasNumber 	   = false;
		boolean looksLikeActNum= false;
		//Check length of input.
		if(inputSequence.length() >= 6 && inputSequence.length() <= 16)
			hasGoodLength = true;
		
		
			for(int i = 0; i < inputSequence.length(); ++i){
				
				if(Character.isLowerCase(inputSequence.charAt(i))){
					hasLowerCase = true;
				}
				else if (Character.isUpperCase(inputSequence.charAt(i))){
					hasUpperCase = true;
				}
				else if (Character.isDigit(inputSequence.charAt(i))){
					hasNumber = true;
				}
				else if(inputSequence.charAt(i) == '\\' ||
						inputSequence.charAt(i) == '`'  ||
						inputSequence.charAt(i) == '\'' ||
						inputSequence.charAt(i) == '\"' ||
						inputSequence.charAt(i) == ' '){
					hasInvalidChar = true;
				}
				else if(!Character.isLetterOrDigit(inputSequence.charAt(i))){
					hasNonAlphaNum = true;
				}
			}
			
			if(inputSequence.toString().startsWith("6011") && 
			   inputSequence.length() == 16 &&
			   !hasLowerCase && !hasUpperCase && !hasNonAlphaNum){
				looksLikeActNum = true;
				
			}

			/*
			 * Meets minimum requirements and combines a variation of letters, numbers, and special characters.
			 */
			if(!looksLikeActNum && !hasInvalidChar && hasGoodLength && hasLowerCase && hasUpperCase && hasNonAlphaNum && hasNumber){
				barOne.setBackgroundColor(getResources().getColor(R.color.green));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.green));
				barThree.setBackgroundColor(getResources().getColor(R.color.green));
				label.setText(getResources().getString(R.string.strength_bar_strong));
			}
			/*
			 * Meets minimum requirements but does not include a variation of letters, numbers, and special characters.
			 */
			else if(!looksLikeActNum && !hasInvalidChar && hasGoodLength){
				barOne.setBackgroundColor(getResources().getColor(R.color.yellow));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.yellow));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.strength_bar_moderate));
			}
			/*
			 * Does not meet minimum requirements (not 6-16 characters, 
			 * looks like an account number, or uses spaces or the following characters: (`)(')(")(\))
			 */
			else {
				barOne.setBackgroundColor(getResources().getColor(R.color.red));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.strength_bar_not_valid));
			}
			
	}

	
	public void updateBarsForPass(final CharSequence inputSequence, final View barOne, final View barTwo, final View barThree, final TextView label){
		boolean hasGoodLength  = false;
		boolean hasUpperCase   = false;
		boolean hasLowerCase   = false;
		boolean hasNonAlphaNum = false;
		boolean hasNumber 	   = false;
				
		//Check length of input.
		if(inputSequence.length() >= 8 && inputSequence.length() <= 32)
			hasGoodLength = true;
					
		//A password must have at least 1 letter and 1 number and cannot be 'password'
		//but password doesn't have a number...
			for(int i = 0; i < inputSequence.length(); ++i){
				
				if(Character.isLowerCase(inputSequence.charAt(i))){
					hasLowerCase = true;
				}
				else if (Character.isUpperCase(inputSequence.charAt(i))){
					hasUpperCase = true;
				}
				else if (Character.isDigit(inputSequence.charAt(i))){
					hasNumber = true;
				}				
				else if(!Character.isLetterOrDigit(inputSequence.charAt(i))){
					hasNonAlphaNum = true;
				}
			}
			
			final boolean hasUpperAndLowerAndNum = hasLowerCase && hasUpperCase && hasNumber;
			/*
			 * Meets minimum requirements and combines upper case letters,
			 * lower case letters, numbers, and special characters.
			 */
			if(hasGoodLength && hasUpperAndLowerAndNum && hasNonAlphaNum){
				barOne.setBackgroundColor(getResources().getColor(R.color.green));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.green));
				barThree.setBackgroundColor(getResources().getColor(R.color.green));
				label.setText(getResources().getString(R.string.strength_bar_strong));
			}
			/*
			 * Meets minimum requirements but does not include a
			 * variation of upper case letters, lower case letters, numbers, and special characters.
			 */
			else if(hasGoodLength && hasNumber && (hasUpperCase || hasLowerCase)){
				barOne.setBackgroundColor(getResources().getColor(R.color.yellow));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.yellow));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.strength_bar_moderate));
			}
			/*
			 * Does not meet minimum requirements (not 8-32 characters, 
			 * does not contain at least 1 letter and 1 number, or is the word "password").
			 */
			else{
				barOne.setBackgroundColor(getResources().getColor(R.color.red));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.strength_bar_not_valid));
			}
			
	}
	
	private void submitFormInfo() {
		final ProgressDialog progress = 
				ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = 
				new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {
			@Override
			public void success(final RegistrationConfirmationDetails responseData) {
				Log.d(TAG, "Success");
				progress.dismiss();
				navigateToConfirmationScreenWithResponseData(responseData);
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				Log.w(TAG, "RegistrationCallOne.errorResponse(ErrorResponse): " + errorResponse);
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
//					case HttpURLConnection.HTTP_BAD_REQUEST: // TODO figure out if this actually happens
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				final TextView mainErrorMessageLabel = 
						(TextView)findViewById(R.id.account_info_main_error_label);
				final TextView errorMessageLabel =
						(TextView)findViewById(R.id.account_info_error_label);
				switch(messageErrorResponse.getMessageStatusCode()){
				case 1906: //Provided information was incorrect.
					errorMessageLabel
					.setText(getString(
							R.string.account_info_bad_input_error_text));
					return true;
				case 1907: //Last attemt with this account number warning.
					errorMessageLabel
					.setText(getString(
							R.string.login_attempt_warning));
					return true;
				case 1919:
					mainErrorMessageLabel
					.setText(getString(
							R.string.account_info_bad_input_error_text));
					errorMessageLabel
					.setText(getString(
							R.string.account_info_two_id_matches_pass_error_text));
					return true;
				case 1921:
					mainErrorMessageLabel
					.setText(getString(
							R.string.account_info_bad_input_error_text));
					errorMessageLabel
					.setText(getString(
							R.string.account_info_two_username_in_use_error_text));
					
				default:
					return false;
					
				}
			}
		};
		
		final RegistrationCallTwo registrationCall = 
				new RegistrationCallTwo(this, callback, formDataTwo);
		registrationCall.submit();

	}

}
