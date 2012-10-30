package com.discover.mobile.register;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.RegistrationDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;

public class AccountInformationTwoActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info_two);
		EditText idField = (EditText)findViewById(R.id.account_info_two_id_field);
		EditText passField = (EditText)findViewById(R.id.account_info_two_pass_field);
		passField.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				updateBarsForPass(s, (View)findViewById(R.id.account_info_two_pass_bar_one), 
							  (View)findViewById(R.id.account_info_two_pass_bar_two),
							  (View)findViewById(R.id.account_info_two_pass_bar_three),
						      (TextView)findViewById(R.id.account_info_two_pass_strength_bar_label));
			}
		});
		
		idField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				updateBarsForUID(s, (View)findViewById(R.id.account_info_two_uid_bar_one), 
							  (View)findViewById(R.id.account_info_two_uid_bar_two),
							  (View)findViewById(R.id.account_info_two_uid_bar_three),
						      (TextView)findViewById(R.id.account_info_two_uid_strength_bar_label));
			}
			
		});
		
	}
	
	
	//Currently setup only for a single user id input.
	public void updateBarsForUID(CharSequence inputSequence, View barOne, View barTwo, View barThree, TextView label){
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

	
	public void updateBarsForPass(CharSequence inputSequence, View barOne, View barTwo, View barThree, TextView label){
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
			
			boolean hasUpperAndLowerAndNum = hasLowerCase && hasUpperCase && hasNumber;
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
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		final String TAG = "Form Submission";
		
		final AsyncCallbackAdapter<RegistrationDetails> callback = new AsyncCallbackAdapter<RegistrationDetails>() {
			@Override
			public void success(final RegistrationDetails value) {
				Log.d(TAG, "Success");
				progress.dismiss();

			}

			// TODO use or remove (commented because AsyncCallbackAdapter now has default handlers for this)
//			@Override
//			public void failure(final Throwable error) {
//				Log.e(TAG, "Error: " + error);
//			}

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
		EditText userIdField = (EditText)findViewById(R.id.account_info_two_id_field);
		EditText userIdConfirmField = (EditText)findViewById(R.id.account_info_two_confirm_id_field);
		EditText passField = (EditText)findViewById(R.id.account_info_two_pass_field);
		EditText passConfirmField = (EditText)findViewById(R.id.account_info_two_pass_confirm_field);
		EditText emailField = (EditText)findViewById(R.id.account_info_two_id_field);


		Spinner cardMonthExp = (Spinner)findViewById(R.id.account_info_month_spinner);
		Spinner cardYearExp = (Spinner)findViewById(R.id.account_info_year_spinner);
		Spinner memberDobMonth = (Spinner)findViewById(R.id.account_info_dob_month_spinner);
		Spinner memberDobDay = (Spinner)findViewById(R.id.account_info_dob_day_spinner);
		EditText memberDobYear = (EditText)findViewById(R.id.account_info_dob_year_field);
		EditText memberSsnNum = (EditText)findViewById(R.id.account_info_ssn_input_field);
		
		String cardMonthExpString = cardMonthExp.getSelectedItem().toString();
		String cardYearExpString = cardYearExp.getSelectedItem().toString();
		String memberDobMonthString = memberDobMonth.getSelectedItem().toString();
		String memberDobDayString = memberDobDay.getSelectedItem().toString();
		String memberDobYearString = memberDobYear.getText().toString();
		String memberSsnNumString =  memberSsnNum.getText().toString();
		
		//final RegistrationCallTwo registrationCall = new RegistrationCallTwo(this, callback, );
		//registrationCall.submit();
	}

}
