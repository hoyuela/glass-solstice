package com.discover.mobile.register;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);
		setupSpinnerAdapters();
		setupButtons();
		
		/*
		 * Figure out what kind of screen we need to present
		 * The default is registering a new user. If there is an extra
		 * present that matches some screen type we can setup the screen
		 * more specifically and change the flow based on that.
		 */
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		if("Forgot Password".equals(extras.getString("screenType"))){
        			((TextView)findViewById(R.id.account_info_title_label))
        			.setText(getResources()
        					.getString(R.string.forgot_password_text));
        		}
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
	
	private void setupButtons(){
		final Button submitButton = (Button)findViewById(R.id.account_info_continue_button);
		submitButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(final View v) {
				submitFormInfo();
			}
		});
		
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
	
	
	private void submitFormInfo() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		final String TAG = "Form Submission";
		
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
					case HttpURLConnection.HTTP_INTERNAL_ERROR: //couldnt authenticate user info.
						{
							((TextView)findViewById(R.id.account_info_error_label))
							.setText(getResources().getString(R.string.account_info_error_text));
							return true;
						}
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {

				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				
				return true;
			}
		};
//		(final Context context, final AsyncCallback<RegistrationDetails> callback,
//		final String acctNbr, final String expirationMonth, final String expirationYear, final String dateOfBirthMonth, final String  dateOfBirthDay,
//		final String socialSecurityNumber, final String dateOfBirthYear)
		final InputValidator validator = new InputValidator();
		final EditText accountNum = (EditText)findViewById(R.id.account_info_card_account_number_field);
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
				
		validator.isCardAccountNumberValid(accountNumString);
		validator.isCardExpMonthValid(cardMonthExpString);
		validator.isCardExpYearValid(cardYearExpString);
		validator.isDobYearValid(memberDobYearString);
		validator.isDobMonthValid(memberDobMonthString);
		validator.isDobDayValid(memberDobDayString);
		validator.isSsnValid(memberSsnNumString);
		
		if(validator.wasAccountInfoComplete()){
			formData.acctNbr = accountNumString;
			formData.dateOfBirthDay = memberDobDayString;
			formData.dateOfBirthMonth = memberDobMonthString;
			formData.dateOfBirthYear = memberDobYearString;
			formData.expirationMonth = cardMonthExpString;
			formData.expirationYear  = cardYearExpString;
			formData.socialSecurityNumber = memberSsnNumString;
			
			final RegistrationCallOne registrationCall = new RegistrationCallOne(this, callback, formData);
			registrationCall.submit();

		}
		else{
			progress.dismiss();
			final TextView cardErrorLabel = (TextView)findViewById(R.id.account_info_card_account_number_error_label);
			final TextView ssnErrorLabel = (TextView)findViewById(R.id.account_info_ssn_error_label);
			final TextView dobYearErrorLabel = (TextView)findViewById(R.id.account_info_dob_year_error_label);
			
			final String errorString = getResources().getString(R.string.invalid_value);
			final String emptyString = getResources().getString(R.string.empty);
			
			if(!validator.wasAccountNumberValid){
				cardErrorLabel.setText(errorString);
			}
			else
				cardErrorLabel.setText(emptyString);
			
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

}
