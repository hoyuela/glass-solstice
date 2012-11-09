package com.discover.mobile.forgotuidpassword;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
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
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.forgotpassword.ForgotPasswordDetails;
import com.discover.mobile.common.forgotpassword.ForgotPasswordTwoCall;
import com.discover.mobile.common.forgotpassword.ForgotPasswordTwoDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.login.register.AccountInformationConfirmationActivity;
import com.discover.mobile.login.register.StrengthBarHelpActivity;

@ContentView(R.layout.enter_new_password)

public class EnterNewPasswordActivity extends RoboActivity{
	private static final String TAG = EnterNewPasswordActivity.class.getSimpleName();
	private ForgotPasswordDetails passOneDetails;
	private ForgotPasswordTwoDetails passTwoDetails;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		passTwoDetails = new ForgotPasswordTwoDetails();
		
		if (savedInstanceState == null) {
			final Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		passOneDetails = (ForgotPasswordDetails)getIntent().getSerializableExtra("ForgotPasswordDetails");
        		passTwoDetails.userId = passOneDetails.userId;
        		passTwoDetails.dateOfBirthDay = passOneDetails.dateOfBirthDay;
        		passTwoDetails.dateOfBirthMonth = passOneDetails.dateOfBirthMonth;
        		passTwoDetails.dateOfBirthYear = passOneDetails.dateOfBirthYear;
        		passTwoDetails.expirationMonth = passOneDetails.expirationMonth;
        		passTwoDetails.expirationYear = passOneDetails.expirationYear;
        		passTwoDetails.socialSecurityNumber = passOneDetails.socialSecurityNumber;
        	}
		}
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
						case HttpURLConnection.HTTP_BAD_REQUEST: // TODO figure out if this actually happens
							return true;
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
					case 1004:
						return true;
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
		
		public void showPasswordStrengthBarHelp(final View v){
			final Intent passwordHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
			passwordHelpScreen.putExtra("helpType", "password");
			this.startActivity(passwordHelpScreen);
		}
		
		public void checkInputsThenSubmit(final View v){
			InputValidator validator = new InputValidator();
			EditText pass1 = (EditText)findViewById(R.id.account_info_two_pass_field);
			EditText pass2 = (EditText)findViewById(R.id.account_info_two_pass_confirm_field);
			String pass1Value = pass1.getText().toString();
			String pass2Value = pass2.getText().toString();
			
			//If the info was all valid - submit it to the service call.
			if(validator.isPassValid(pass1Value) &&
			   validator.doPassesMatch(pass1Value, pass2Value)){
				passTwoDetails.password = pass1Value;
				passTwoDetails.passwordConfirm = passTwoDetails.password;
				submitFormInfo();
			}
			else{
				if(!validator.wasPassValid){
					TextView errorLabelOne = 
					(TextView)findViewById(R.id.enter_new_pass_error_two_label);
					errorLabelOne.setText(getString(R.string.invalid_value));
				}
				if(validator.wasPassValid && !validator.didPassesMatch){
					TextView errorLabelTwo = 
							(TextView)findViewById(R.id.enter_new_pass_error_two_label);
							errorLabelTwo.setText(getString(R.string.invalid_value));
				}
			}
				
			
		}
		
		private void navigateToConfirmationScreenWithResponseData(RegistrationConfirmationDetails responseData){
			Intent confirmationScreen = new Intent(this, AccountInformationConfirmationActivity.class);
			confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
			confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
			confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);
			
			this.startActivity(confirmationScreen);
		}
	
}
