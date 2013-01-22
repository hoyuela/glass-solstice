package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;

import java.net.HttpURLConnection;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.forgot.ForgotPasswordTwoCall;
import com.discover.mobile.common.auth.forgot.ForgotPasswordTwoDetails;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.ConfirmationEditText;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.navigation.HeaderProgressIndicator;
import com.discover.mobile.navigation.NavigationRootActivity;
/**
 * EnterNewPasswordActivit - this activity inherits from AbstractAccountInformationActivity
 * @author scottseward
 *
 */
public class EnterNewPasswordActivity extends NotLoggedInRoboActivity {
	
	private static final String TAG = EnterNewPasswordActivity.class.getSimpleName();
	
	private static AccountInformationDetails passOneDetails;
	private ForgotPasswordTwoDetails passTwoDetails;
		
//TEXT LABELS
	TextView mainErrorMessageLabel;
	TextView errorMessageLabel;
	TextView errorLabelOne;	
	TextView errorLabelTwo;
		
//INPUT FIELDS
	CredentialStrengthEditText passOneField;
	ConfirmationEditText passTwoField;
		
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_create_password);
		loadAllViews();
		
		passTwoDetails = new ForgotPasswordTwoDetails();
		
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		passOneDetails = (AccountInformationDetails) getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);
    		mergeAccountDetails();
    	}
		
		HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
    	progress.initChangePasswordHeader(1);
    	
	}
	
	private void loadAllViews() {
		passOneField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_pass_field);
		passTwoField = (ConfirmationEditText)findViewById(R.id.account_info_two_pass_confirm_field);
		errorLabelTwo = (TextView)findViewById(R.id.enter_new_pass_error_two_label);
		errorLabelOne = (TextView)findViewById(R.id.enter_new_pass_error_one_label);
		errorMessageLabel = (TextView)findViewById(R.id.account_info_error_label);
	}
	
	private void mergeAccountDetails() {
		passTwoDetails.userId = passOneDetails.userId;
		passTwoDetails.dateOfBirthDay = passOneDetails.dateOfBirthDay;
		passTwoDetails.dateOfBirthMonth = passOneDetails.dateOfBirthMonth;
		passTwoDetails.dateOfBirthYear = passOneDetails.dateOfBirthYear;
		passTwoDetails.expirationMonth = passOneDetails.expirationMonth;
		passTwoDetails.expirationYear = passOneDetails.expirationYear;
		passTwoDetails.socialSecurityNumber = passOneDetails.socialSecurityNumber;
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
					showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text);
					return true;
					
				case BAD_ACCOUNT_STATUS: //Last attemt with this account number warning.
					showLabelWithStringResource(errorMessageLabel,R.string.login_attempt_warning);
					return true;
					
				case ID_AND_PASS_EQUAL:
					showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text);
					showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_id_matches_pass_error_text);
					return true;
					
				case SCHEDULED_MAINTENANCE:
					sendToErrorPage(ScreenType.SCHEDULED_MAINTENANCE);
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
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
	}
	
	private void showLabelWithStringResource(TextView label, int id) {
		label.setText(getString(id));
		showLabel(label);
	}
	
	private void showLabel(final View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	private void hideLabel(final View v) {
		v.setVisibility(View.GONE);
	}
	
	public void showPasswordStrengthBarHelp(final View v) {
		
		final Intent passwordHelpScreen = new Intent(this, StrengthBarHelpActivity.class);
		passwordHelpScreen.putExtra("helpType", "password");
		this.startActivityForResult(passwordHelpScreen, 0);
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {		
	}	
	
	
	public void checkInputsThenSubmit(final View v) {
		
		final InputValidator validator = new InputValidator();
		final String passOneFieldValue = passOneField.getText().toString();
		final String passTwoFieldValue = passTwoField.getText().toString();
		
		//If the info was all valid - submit it to the service call.
		if(InputValidator.isPasswordValid(passOneFieldValue) &&
		   validator.doPassesMatch(passOneFieldValue, passTwoFieldValue)){
			passTwoDetails.password = passOneFieldValue;
			passTwoDetails.passwordConfirm = passTwoDetails.password;
			submitFormInfo();
		}
		else {
			if(!validator.wasPassValid) {
				showLabelWithStringResource(errorLabelOne, R.string.doesnt_match_records);
			}
			if(validator.wasPassValid && !validator.didPassesMatch) {
				showLabelWithStringResource(errorLabelOne, R.string.account_info_two_passwords_dont_match_text);
				showLabelWithStringResource(errorLabelTwo, R.string.doesnt_match_records);
			}
		}
		
	}
	
	@Override public void onBackPressed() {
		cancel(null);
	}
	
	public void cancel(final View v) {
		final Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	private void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData) {
		final Intent confirmationScreen = new Intent(this, NavigationRootActivity.class);
		confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
		confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
		confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);
		confirmationScreen.putExtra(IntentExtraKey.SCREEN_TYPE, IntentExtraKey.SCREEN_FORGOT_PASS);
		this.startActivity(confirmationScreen);
		finish();
	}

	@Override
	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void goBack() {
		cancel(null);
	}
	
}
