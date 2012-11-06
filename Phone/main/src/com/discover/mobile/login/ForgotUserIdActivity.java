package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.register.AccountInformationConfirmationActivity;

@ContentView(R.layout.forgot_id)
public class ForgotUserIdActivity extends RoboActivity{
	
	private static final String TAG = ForgotUserIdActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	/*
	 * Called from XML
	 */
	public void checkInputsAndSubmit(View v){
		InputValidator validator = new InputValidator();
		
		TextView idErrLabel = (TextView)findViewById(
				R.id.forgot_id_id_error_label);
		TextView passErrLabel = ((TextView)findViewById(
				R.id.forgot_id_pass_error_label));
		
		EditText cardNum = (EditText)findViewById(R.id.forgot_id_id_field);
		EditText passText= (EditText)findViewById(R.id.forgot_id_password_field);
		
		String acctNbr = cardNum.getText().toString();
		String pass    = passText.getText().toString();
						
		String errorText = getResources().getString(R.string.invalid_value);
		String emptyText = getResources().getString(R.string.empty);
		
		if(!validator.isCardAccountNumberValid(acctNbr))
			idErrLabel.setText(errorText);
		else
			passErrLabel.setText(emptyText);

		if(!validator.isPassValid(pass))
			passErrLabel.setText(errorText);
		else
			passErrLabel.setText(emptyText);

		
		/*
		 * On failed submission display
		 * 
		 * TextView submissionError = (TextView)findViewById(
		 * R.id.forgot_id_submission_error_label);
		 * String submissionErrorText = getResources().getString(
		 * R.string.forgot_id_submission_error_text);
		 * if FAIL
		 * 		submissionError.setText(submissionErrorText);
		 * else
		 * 		submissionError.setText(emptyText);
		 */
		
		//TODO attempt to submit info when inputs are OK
		if(validator.wasPassValid & validator.wasAccountNumberValid){
			Intent confirmationScreen = 
				new Intent(this, AccountInformationConfirmationActivity.class);
			startActivity(confirmationScreen);
		}
			
	}
	
	
}
