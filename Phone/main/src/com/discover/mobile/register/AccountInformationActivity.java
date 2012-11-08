package com.discover.mobile.register;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.RegistrationCallOne;
import com.discover.mobile.common.auth.registration.RegistrationOneDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;

public class AccountInformationActivity extends Activity {
	private RegistrationOneDetails formData;
	private boolean forgotPass = false;
	private static final String TAG = AccountInformationActivity.class.getSimpleName();
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);
		setupSpinnerAdapters();
		
		/*
		 * Figure out what kind of screen we need to present
		 * The default is registering a new user. If there is an extra
		 * present that matches some screen type we can setup the screen
		 * more specifically and change the flow based on that.
		 */
		if (savedInstanceState == null) {
			final Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		//Setup the screen for a forgotten password.
        		if("Forgot Password".equals(extras.getString("screenType"))){
        			forgotPass = true;
        			//Set the title of the screen to forgot password
        			((TextView)findViewById(R.id.account_info_title_label))
        			.setText(getResources()
        					.getString(R.string.forgot_password_text));
        			
        			//Set the detail label under the title to blank.
        			((TextView)findViewById(
        					R.id.account_information_input_info_label))
        					.setHeight(0);
        				
        			//Set title label of the input field
        			((TextView)findViewById(R.id.account_info_label_one_label))
        				.setText(getResources()
        						.getString(
        							R.string.forgot_password_field_title_text));
        			
        			//Change the input type for the edit text to be valid for
        			//user credentials and not the default card number stuff
        			final EditText mainInputField = 
        				(EditText)findViewById(
        						R.id.account_info_main_input_field);
        			mainInputField.setInputType(
        					InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        			
        			//Change the max input length to 32 characters.
        			final InputFilter[] filterArray = new InputFilter[1];
        			filterArray[0] = new InputFilter.LengthFilter(32);
        			mainInputField.setFilters(filterArray);
        		}
        		//Setup the screen for forgotten both credentials.
        		else if("Forgot Both".equals(extras.getString("screenType"))){
        			((TextView)findViewById(R.id.account_info_title_label))
        			.setText(getResources()
        					.getString(R.string.forgot_both_text));
        		}
        	}
		}
	}
	
	private void startNextActivity(){
		final Intent enhancedAccountSecurityIntent = 
				new Intent(this, AccountInformationTwoActivity.class);
		
		enhancedAccountSecurityIntent
			.putExtra("RegistrationOneDetails", formData);
		
		this.startActivity(enhancedAccountSecurityIntent);
		
	}
	
	private void setupSpinnerAdapters(){
		Spinner spinner;
		ArrayAdapter<CharSequence> adapter;
		
		spinner =  (Spinner) findViewById(R.id.account_info_month_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.month_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner = (Spinner) findViewById(R.id.account_info_dob_month_spinner);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.account_info_year_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.account_info_dob_day_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.day_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	
	public void submitFormInfo(View v) {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				Log.d(TAG, "Success");
				progress.dismiss();
				startNextActivity();

			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				Log.w(TAG, "RegistrationCallOne.errorResponse(ErrorResponse): " + errorResponse);
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_BAD_REQUEST:
						return true;
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;
					case HttpURLConnection.HTTP_INTERNAL_ERROR: //couldn't authenticate user info.
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {

				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				TextView errorMessageLabel = 
						(TextView)findViewById(R.id.account_info_error_label);
				
				switch(messageErrorResponse.getMessageStatusCode()){
				case 1905: //Wrong type of account info provided.
					errorMessageLabel
					.setText(getString(
							R.string.account_info_sams_club_card_error_text));
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
					
				default:
					return false;
					
				}
				
			}
		};

		final InputValidator validator = new InputValidator();
		final EditText accountNum = (EditText)findViewById(R.id.account_info_main_input_field);
		final Spinner cardMonthExp = (Spinner)findViewById(R.id.account_info_month_spinner);
		final Spinner cardYearExp = (Spinner)findViewById(R.id.account_info_year_spinner);
		final Spinner memberDobMonth = (Spinner)findViewById(R.id.account_info_dob_month_spinner);
		final Spinner memberDobDay = (Spinner)findViewById(R.id.account_info_dob_day_spinner);
		final EditText memberDobYear = (EditText)findViewById(R.id.account_info_dob_year_field);
		final EditText memberSsnNum = (EditText)findViewById(R.id.account_info_ssn_input_field);
		
		final String accountNumString = accountNum.getText().toString();
		final String cardMonthExpString = cardMonthExp.getSelectedItem().toString();
		final String cardYearExpString = cardYearExp.getSelectedItem().toString();
		final String memberDobMonthString = memberDobMonth.getSelectedItem().toString();
		final String memberDobDayString = memberDobDay.getSelectedItem().toString();
		final String memberDobYearString = memberDobYear.getText().toString();
		final String memberSsnNumString =  memberSsnNum.getText().toString();
		formData = new RegistrationOneDetails();
				
		validator.isUidValid(accountNumString);
		validator.isCardAccountNumberValid(accountNumString);
		validator.isCardExpMonthValid(cardMonthExpString);
		validator.isCardExpYearValid(cardYearExpString);
		validator.isDobYearValid(memberDobYearString);
		validator.isDobMonthValid(memberDobMonthString);
		validator.isDobDayValid(memberDobDayString);
		validator.isSsnValid(memberSsnNumString);
		
		updateLabelsUsingValidator(validator);
		
		//Submit info based on account information (new user registration)
		if(validator.wasAccountInfoComplete()){
			formData.acctNbr = accountNumString;
			formData.dateOfBirthDay = memberDobDayString;
			formData.dateOfBirthMonth = memberDobMonthString;
			formData.dateOfBirthYear = memberDobYearString;
			formData.expirationMonth = cardMonthExpString;
			formData.expirationYear  = cardYearExpString;
			formData.socialSecurityNumber = memberSsnNumString;
			
			final RegistrationCallOne registrationCall = 
					new RegistrationCallOne(this, callback, formData);
			registrationCall.submit();

		}
		//If this succeeds then we have a user who forgot their password.
		else if(validator.wasForgotPasswordInfoComplete()){
			
		}
	}
	
	private void updateLabelsUsingValidator(InputValidator validator){
		final TextView cardErrorLabel = (TextView)findViewById(R.id.account_info_card_account_number_error_label);
		final TextView ssnErrorLabel = (TextView)findViewById(R.id.account_info_ssn_error_label);
		final TextView dobYearErrorLabel = (TextView)findViewById(R.id.account_info_dob_year_error_label);
		
		final String errorString = getResources().getString(R.string.invalid_value);
		final String emptyString = getResources().getString(R.string.empty);
		
		/*
		 * Set error label based on what is valid.
		 * These should never be both true.
		 * Bitwise AND and inclusive OR used - why not.
		 */
		if((forgotPass & !validator.wasUidValid) | 
				(!forgotPass & !validator.wasAccountNumberValid)){
			cardErrorLabel.setText(errorString);
		}
		else{
			cardErrorLabel.setText(emptyString);
		}
		
		if(!validator.wasSsnValid){
			ssnErrorLabel.setText(errorString);
		}
		else
			ssnErrorLabel.setText(emptyString);
		
		if(!validator.wasDobYearValid){
			dobYearErrorLabel.setText(errorString);
		}
		else
			dobYearErrorLabel.setText(emptyString);
	}

}
