package com.discover.mobile.login.register;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
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
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.StrongAuthCall;
import com.discover.mobile.common.auth.StrongAuthDetails;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.forgotpassword.ForgotPasswordCall;
import com.discover.mobile.common.forgotpassword.ForgotPasswordDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.forgotuidpassword.EnterNewPasswordActivity;
import com.discover.mobile.login.LockOutUserActivity;

public class AccountInformationActivity extends RoboActivity {
	private AccountInformationDetails registrationOneDetails;
	private ForgotPasswordDetails forgotPasswordDetails;
	private boolean forgotPass = false;
	private static final String TAG = AccountInformationActivity.class.getSimpleName();
	
	@InjectView(R.id.account_info_title_label)
	private TextView accountInfoTitleLabel;
	
	@InjectView(R.id.account_information_input_info_label)
	private TextView accountInfoInputLabel;
	
	@InjectView(R.id.account_info_label_one_label)
	private TextView accountInfoLabelOne;
	
	@InjectView(R.id.account_info_main_input_field)
	private EditText mainInputField;
	
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
        		switch(extras.getInt(IntentExtraKey.SCREEN_TYPE)) {
        			case ScreenType.FORGOT_PASSWORD:
        				TrackingHelper.trackPageView(AnalyticsPage.FORGOT_PASSWORD_STEP1);

            			forgotPass = true;
            			//Set the title of the screen to forgot password
            			accountInfoTitleLabel.setText(getString(R.string.forgot_password_text));
            			
            			//Set the detail label under the title to blank.
            			accountInfoInputLabel.setHeight(0);
            				
            			//Set title label of the input field
            			accountInfoLabelOne.setText(getString(R.string.forgot_password_field_title_text));
            			
            			//Change the input type for the edit text to be valid for
            			//user credentials and not the default card number stuff
            			mainInputField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            			
            			//Change the max input length to 32 characters.
            			final InputFilter[] filterArray = new InputFilter[1];
            			filterArray[0] = new InputFilter.LengthFilter(32);
            			mainInputField.setFilters(filterArray);
        			case ScreenType.FORGOT_BOTH:
        				TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_STEP1);
        				accountInfoTitleLabel.setText(getString(R.string.forgot_both_text));
            		default:
            			break;
        		}
        	}
		}
	}
	
	private void startNextActivity(){
		if(forgotPass){
			final Intent enterNewPasswordActivity = 
					new Intent(this, EnterNewPasswordActivity.class);
			enterNewPasswordActivity
			.putExtra("ForgotPasswordDetails", forgotPasswordDetails);
			this.startActivity(enterNewPasswordActivity);

		}else{
			final Intent enhancedAccountSecurityIntent = 
					new Intent(this, CreateLoginActivity.class);
			
			enhancedAccountSecurityIntent
				.putExtra("AccountInformationDetails", registrationOneDetails);
			
			this.startActivity(enhancedAccountSecurityIntent);
		}
		
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
	
	public void validateInfoAndSubmitOnSuccess(View v){
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
					
		validator.isUidValid(accountNumString);
		validator.isCardAccountNumberValid(accountNumString);
		validator.isCardExpMonthValid(cardMonthExpString);
		validator.isCardExpYearValid(cardYearExpString);
		validator.isDobYearValid(memberDobYearString);
		validator.isDobMonthValid(memberDobMonthString);
		validator.isDobDayValid(memberDobDayString);
		validator.isSsnValid(memberSsnNumString);
		
		updateLabelsUsingValidator(validator);
		
		if(validator.wasAccountInfoComplete() || validator.wasForgotPasswordInfoComplete()){
			if(forgotPass){
				forgotPasswordDetails = new ForgotPasswordDetails();
				forgotPasswordDetails.userId = accountNumString;
				forgotPasswordDetails.dateOfBirthDay = memberDobDayString;
				forgotPasswordDetails.dateOfBirthMonth = memberDobMonthString;
				forgotPasswordDetails.dateOfBirthYear = memberDobYearString;
				forgotPasswordDetails.expirationMonth = cardMonthExpString;
				forgotPasswordDetails.expirationYear  = cardYearExpString;
				forgotPasswordDetails.socialSecurityNumber = memberSsnNumString;
				submitFormInfo(validator);
			}
			else{
				registrationOneDetails = new AccountInformationDetails();
				registrationOneDetails.acctNbr = accountNumString;
				registrationOneDetails.dateOfBirthDay = memberDobDayString;
				registrationOneDetails.dateOfBirthMonth = memberDobMonthString;
				registrationOneDetails.dateOfBirthYear = memberDobYearString;
				registrationOneDetails.expirationMonth = cardMonthExpString;
				registrationOneDetails.expirationYear  = cardYearExpString;
				registrationOneDetails.socialSecurityNumber = memberSsnNumString;
				submitFormInfo(validator);
			}
			

		}

	}
	
	private void submitFormInfo(InputValidator validator) {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				progress.dismiss();
				checkForPreAuth();
				
				startNextActivity();

			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
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
				final TextView errorMessageLabel = 
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
				case 1916:
					errorMessageLabel
					.setText(getString(
							R.string.account_info_bad_input_error_text));
					return true;
				default:
					return false;
				}
				
			}
		};

		//Submit info based on account information (new user registration)
				if(validator.wasAccountInfoComplete()){
					final AccountInformationCall registrationCall = 
							new AccountInformationCall(this, callback, registrationOneDetails);
					registrationCall.submit();

				}
				//If this succeeds then we have a user who forgot their password.
				else if(validator.wasForgotPasswordInfoComplete()){
					final ForgotPasswordCall passwordCall = new ForgotPasswordCall(this, callback, forgotPasswordDetails);
					passwordCall.submit();
				}
		
		
		
	}
	
private void checkForPreAuth(){
	final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallback<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final StrongAuthDetails value) {
				Log.d(TAG, "Success");
				progress.dismiss();
				if(value.questionText != null && !"".equals(value.questionText)){
					startNextActivity();
				}
				//TODO handle question if strong auth returns one.

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
					case HttpURLConnection.HTTP_FORBIDDEN:
						return true;
				}
				
				//TODO properly handle these ^ v
				return true;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {

				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				TextView errorMessageLabel = 
						(TextView)findViewById(R.id.account_info_error_label);
				
				switch(messageErrorResponse.getMessageStatusCode()){
				case 1402:
						sendToErrorPage(ScreenType.STRONG_AUTH_LOCKED_OUT);
					return true;
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
				case 1916:
					errorMessageLabel
					.setText(getString(
							R.string.account_info_bad_input_error_text));
					return true;
				default://TODO properly handle these ^ v
					return true;
					
				}
				
			}
		};

		final StrongAuthCall strongAuthCall = 
				new StrongAuthCall(this, callback);
		strongAuthCall.submit();
		
	}
	
	private void sendToErrorPage(final int screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		maintenancePageIntent.putExtra(IntentExtraKey.SCREEN_TYPE, screenType);
		startActivity(maintenancePageIntent);
	}
	
	private void updateLabelsUsingValidator(final InputValidator validator){
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
