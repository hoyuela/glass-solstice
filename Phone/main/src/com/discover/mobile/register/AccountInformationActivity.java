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

import com.discover.mobile.R;
import com.discover.mobile.common.auth.registration.RegistrationCallOne;
import com.discover.mobile.common.auth.registration.RegistrationOneDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;

public class AccountInformationActivity extends Activity {
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);
		setupSpinnerAdapters();
		setupButtons();
	}
	
	private void submitCurrentAccountInfo(){
		final Intent enhancedAccountSecurityIntent = new Intent(this, EnhancedAccountSecurity.class);
		this.startActivity(enhancedAccountSecurityIntent);
		
	}
	
	private void setupButtons(){
		final Button submitButton = (Button)findViewById(R.id.account_info_continue_button);
		submitButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				//submitFormInfo();
				submitCurrentAccountInfo();
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
		
		final AsyncCallbackAdapter<RegistrationOneDetails> callback = new AsyncCallbackAdapter<RegistrationOneDetails>() {
			@Override
			public void success(final RegistrationOneDetails value) {
				Log.d(TAG, "Success");
				progress.dismiss();

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
				if(messageErrorResponse.getHttpStatusCode() != HttpURLConnection.HTTP_FORBIDDEN)
					return false;
				
				Log.e(TAG, "AuthenticateCall.messageErrorResponse(MessageErrorResponse): " + messageErrorResponse);
				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				
				return true;
			}
		};
//		(final Context context, final AsyncCallback<RegistrationDetails> callback,
//		final String acctNbr, final String expirationMonth, final String expirationYear, final String dateOfBirthMonth, final String  dateOfBirthDay,
//		final String socialSecurityNumber, final String dateOfBirthYear)
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
		RegistrationOneDetails formData = new RegistrationOneDetails();
		
		final RegistrationCallOne registrationCall = new RegistrationCallOne(this, callback, formData);
		registrationCall.submit();
	}

}
