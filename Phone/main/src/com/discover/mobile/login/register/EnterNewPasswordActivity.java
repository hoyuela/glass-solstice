package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;

import java.net.HttpURLConnection;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.auth.forgot.ForgotPasswordTwoCall;
import com.discover.mobile.common.auth.forgot.ForgotPasswordTwoDetails;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.ConfirmationEditText;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.navigation.HeaderProgressIndicator;
/**
 * EnterNewPasswordActivit - this activity inherits from AbstractAccountInformationActivity
 * @author scottseward
 *
 */
public class EnterNewPasswordActivity extends ForgotOrRegisterFinalStep {
	
	private static final String TAG = EnterNewPasswordActivity.class.getSimpleName();
	
	private static AccountInformationDetails passOneDetails;
	private ForgotPasswordTwoDetails passTwoDetails;
	
//TEXT LABELS
	private TextView mainErrorMessageLabel;
	private TextView errorMessageLabel;
	private TextView errorLabelOne;	
	private TextView errorLabelTwo;
		
//INPUT FIELDS
	private CredentialStrengthEditText passOneField;
	private ConfirmationEditText passTwoField;
	
//SCROLL VIEW
	private ScrollView mainScrollView;
		
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_create_password);
		loadAllViews();
		setupInputFields();
		
		passTwoDetails = new ForgotPasswordTwoDetails();
		
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		passOneDetails = (AccountInformationDetails) getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);
    		mergeAccountDetails();
    	}
		
		HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
    	progress.initChangePasswordHeader(1);
    	
    	setupHelpNumber();
    	
	}
	
	/**
	 * Get all of the view elements from the layout and assign them to local variables that will be 
	 * used to access them.
	 */
	private void loadAllViews() {
		passOneField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_pass_field);
		passTwoField = (ConfirmationEditText)findViewById(R.id.account_info_two_pass_confirm_field);
		
		errorLabelTwo = (TextView)findViewById(R.id.enter_new_pass_error_two_label);
		errorLabelOne = (TextView)findViewById(R.id.enter_new_pass_error_one_label);
		errorMessageLabel = (TextView)findViewById(R.id.account_info_error_label);
		mainErrorMessageLabel = (TextView)findViewById(R.id.account_info_main_error_label);
		
		mainScrollView = (ScrollView)findViewById(R.id.main_scroll_view);
		
	}
	
	/**
	 * Setup input fields, attach error labels and set the type of input that the fields will receive.
	 */
	private void setupInputFields() {
		passOneField.setCredentialType(CredentialStrengthEditText.PASSWORD);
		passTwoField.attachEditTextToMatch(passOneField);
		passOneField.attachErrorLabel(errorLabelOne);
		passTwoField.attachErrorLabel(errorLabelTwo);
		
	}
	
	/**
	 * Take the details from the first page and merge them into a POJO that will be send to the server
	 * on this page.
	 */
	private void mergeAccountDetails() {
		passTwoDetails.userId = passOneDetails.userId;
		passTwoDetails.dateOfBirthDay = passOneDetails.dateOfBirthDay;
		passTwoDetails.dateOfBirthMonth = passOneDetails.dateOfBirthMonth;
		passTwoDetails.dateOfBirthYear = passOneDetails.dateOfBirthYear;
		passTwoDetails.expirationMonth = passOneDetails.expirationMonth;
		passTwoDetails.expirationYear = passOneDetails.expirationYear;
		passTwoDetails.socialSecurityNumber = passOneDetails.socialSecurityNumber;
	}
	
	/**
	 * Take the information provided by the user and send it to the server for serverside validation.
	 */
	private void submitFormInfo() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
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
						
					default:
						CommonMethods.showLabelWithStringResource(errorMessageLabel,R.string.unkown_error_text, currentContext);
						return true;
				}
				
			}
			
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				switch(messageErrorResponse.getMessageStatusCode()){

				case REG_AUTHENTICATION_PROBLEM: 
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					return true;
					
				case BAD_ACCOUNT_STATUS:
					CommonMethods.showLabelWithStringResource(errorMessageLabel,R.string.login_attempt_warning, currentContext);
					return true;
					
				case ID_AND_PASS_EQUAL:
					CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonMethods.showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_id_matches_pass_error_text, currentContext);
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
	
	/**
	 * If all of the information is valid on the page then submit the info to get validated
	 * by the server.
	 * @param v
	 */
	public void checkInputsThenSubmit(final View v) {
		passOneField.updateAppearanceForInput();
		passTwoField.updateAppearanceForInput();
		CommonMethods.setViewGone(mainErrorMessageLabel);
		
		//If the info was all valid - submit it to the service call.
		if(passOneField.isValid() && passTwoField.isValid()){
			final String passOneFieldValue = passOneField.getText().toString();

			passTwoDetails.password = passOneFieldValue;
			passTwoDetails.passwordConfirm = passTwoDetails.password;
			submitFormInfo();
		}
		else {
			mainScrollView.smoothScrollTo(0, 0);
			if(!passOneField.isValid())
				passOneField.setStrengthMeterInvalid();
			
			CommonMethods.showLabelWithStringResource(mainErrorMessageLabel, 
					R.string.account_info_bad_input_error_text, this);
		}
		
	}

}
