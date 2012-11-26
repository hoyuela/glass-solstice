package com.discover.mobile.login.forgot;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
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
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.StandardErrorCodes;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.forgot.ForgotPasswordTwoCall;
import com.discover.mobile.common.auth.forgot.ForgotPasswordTwoDetails;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.auth.registration.RegistrationErrorCodes;
import com.discover.mobile.common.net.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.login.register.AccountInformationConfirmationActivity;
import com.discover.mobile.login.register.StrengthBarHelpActivity;
public class EnterNewPasswordActivity extends RoboActivity implements StandardErrorCodes, RegistrationErrorCodes {
	
	private static final String TAG = EnterNewPasswordActivity.class.getSimpleName();
	private static AccountInformationDetails passOneDetails;
	private ForgotPasswordTwoDetails passTwoDetails;
	
//TEXT LABELS
	
	@InjectView(R.id.account_info_main_error_label)
	TextView mainErrorMessageLabel;
	
	@InjectView(R.id.account_info_error_label)
	TextView errorMessageLabel;
	
	@InjectView(R.id.enter_new_pass_error_two_label)
	TextView errorLabelOne;
	
	@InjectView(R.id.enter_new_pass_error_two_label)
	TextView errorLabelTwo;
	
	@InjectView(R.id.account_info_two_pass_strength_bar_label)
	TextView strengthBarLabel;
	
//INPUT FIELDS

	@InjectView(R.id.account_info_two_pass_field)
	EditText passOneField;
	
	@InjectView(R.id.account_info_two_pass_confirm_field)
	EditText passTwoField;
	
//PASSWORD STRENGTH BARS
	
	@InjectView(R.id.account_info_two_pass_bar_one)
	View passBarOne;
	
	@InjectView(R.id.account_info_two_pass_bar_two)
	View passBarTwo;
	
	@InjectView(R.id.account_info_two_pass_bar_three)
	View passBarThree;
		
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		passTwoDetails = new ForgotPasswordTwoDetails();
		
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		passOneDetails = (AccountInformationDetails) getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);
    		passTwoDetails.userId = passOneDetails.userId;
    		passTwoDetails.dateOfBirthDay = passOneDetails.dateOfBirthDay;
    		passTwoDetails.dateOfBirthMonth = passOneDetails.dateOfBirthMonth;
    		passTwoDetails.dateOfBirthYear = passOneDetails.dateOfBirthYear;
    		passTwoDetails.expirationMonth = passOneDetails.expirationMonth;
    		passTwoDetails.expirationYear = passOneDetails.expirationYear;
    		passTwoDetails.socialSecurityNumber = passOneDetails.socialSecurityNumber;
    	}
		
		final EditText passField = (EditText)findViewById(R.id.account_info_two_pass_field);
		passField.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(final Editable s) {/*Intentionally Empty*/}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally Empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				updateBarsForPass(s, passBarOne, passBarTwo, passBarThree, strengthBarLabel);
			}
		});
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
			if(hasGoodLength && (hasLowerCase || hasUpperCase) && hasNonAlphaNum){
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
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		hideAllErrorLabels();
		
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
					case HttpURLConnection.HTTP_BAD_REQUEST: // TODO figure out if this actually happens
						return true;
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;
				}
				
				return false;
			}
			
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				switch(messageErrorResponse.getMessageStatusCode()){

				case REG_AUTHENTICATION_PROBLEM: 
					showErrorMessageLabelWithText(getString(R.string.account_info_bad_input_error_text));
					return true;
					
				case BAD_ACCOUNT_STATUS: //Last attemt with this account number warning.
					showErrorMessageLabelWithText(getString(R.string.login_attempt_warning));
					return true;
					
				case ID_AND_PASS_EQUAL:
					showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
					showErrorMessageLabelWithText(getString(R.string.account_info_two_id_matches_pass_error_text));
					return true;
					
				case ID_ALREADY_TAKEN:
					showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
					showErrorMessageLabelWithText(getString(R.string.account_info_two_username_in_use_error_text));
					return true;
					
				default:
					return false;
					
				}
			}
		};
		
		final ForgotPasswordTwoCall forgotPassTwoCall = 
				new ForgotPasswordTwoCall(this, callback, passTwoDetails);
		forgotPassTwoCall.submit();

	}
	
	public void hideAllErrorLabels() {
		
		hideLabel(mainErrorMessageLabel);
		hideLabel(errorMessageLabel);
		hideLabel(errorLabelOne);
		hideLabel(errorLabelTwo);
	}
	
	public void showMainErrorLabelWithText(String text) {
		
		mainErrorMessageLabel.setText(text);
		showLabel(mainErrorMessageLabel);
	}
	
	private void showErrorMessageLabelWithText(String text) {
		
		errorMessageLabel.setText(text);
		showLabel(errorMessageLabel);
	}
	
	private void showLabel(View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	private void hideLabel(View v) {
		v.setVisibility(View.GONE);
	}
	
	public void showPasswordStrengthBarHelp(final View v) {
		
		final Intent passwordHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
		passwordHelpScreen.putExtra("helpType", "password");
		this.startActivity(passwordHelpScreen);
	}
	
	public void checkInputsThenSubmit(final View v) {
		
		final InputValidator validator = new InputValidator();
		final String passOneFieldValue = passOneField.getText().toString();
		final String passTwoFieldValue = passTwoField.getText().toString();
		
		//If the info was all valid - submit it to the service call.
		if(validator.isPassValid(passOneFieldValue) &&
		   validator.doPassesMatch(passOneFieldValue, passTwoFieldValue)){
			passTwoDetails.password = passOneFieldValue;
			passTwoDetails.passwordConfirm = passTwoDetails.password;
			submitFormInfo();
		}
		else {
			if(!validator.wasPassValid) {
				showLabel(errorLabelOne);
			}
			if(validator.wasPassValid && !validator.didPassesMatch) {
				showLabel(errorLabelTwo);
			}
		}
			
		
	}
	
	public void cancel(final View v) {
		   final Intent navToForgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		   startActivity(navToForgotCredentials);
	}
	
	private void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData) {
		final Intent confirmationScreen = new Intent(this, AccountInformationConfirmationActivity.class);
		confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
		confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
		confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);
		confirmationScreen.putExtra("ScreenType", "forgotPass");
		this.startActivity(confirmationScreen);
	}
	
}
