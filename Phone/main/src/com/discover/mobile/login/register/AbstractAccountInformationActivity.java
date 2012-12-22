package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.FAILED_SECURITY;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_EXTERNAL_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_ONLINE_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.ONLINE_STATUS_PROHIBITED;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.StandardErrorCodes.UNSCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.FINAL_LOGIN_ATTEMPT;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.NOT_PRIMARY_CARDHOLDER;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.SAMS_CLUB_MEMBER;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.GetStrongAuthQuestionCall;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.strong.StrongAuthCheckCall;
import com.discover.mobile.common.auth.strong.StrongAuthDetails;
import com.discover.mobile.common.auth.strong.StrongAuthErrorResponse;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.CustomDatePickerDialog;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.security.EnhancedAccountSecurityActivity;
/**
 * AbstractAccountInformationActivity this activity handles the forgot user password, both, or registration.
 * 
 * It is an abstract class that is inherited by ForgotPasswordAccountInformationActivity, 
 * ForgotBothAccountInformationActivity, and RegistrationAccountInformationActivity.
 * 
 * All of these steps are similar and only require minor adjustments to the UI, so they all use the same basic layout.
 * 
 * @author scottseward
 *
 */
@ContentView(R.layout.register_enter_account_info)
abstract class AbstractAccountInformationActivity extends RoboActivity {
	
	private static final String TAG = AbstractAccountInformationActivity.class.getSimpleName();
	
	private AccountInformationDetails accountInformationDetails;
	
	private final String ANALYTICS_PAGE_IDENTIFIER;
	
	private ProgressDialog progress;
	
	private String strongAuthQuestion;
	private String strongAuthQuestionId;
	
	private static final int SSN_LENGTH_OK = 4;
	private static final int YEAR_LENGTH_OK = 4;
	
	private String dobYear = "";
	private String dobMonth = "";
	private String dobDay = "";
	
	private String expirationYear = "";
	private String expirationMonth = "";
	
//TEXT LABELS
	
	@InjectView(R.id.account_info_title_label)
	private TextView activityTitleLabel;
	
	@InjectView(R.id.account_info_label_one_label)
	protected TextView accountIdentifierFieldLabel;
	
	@InjectView(R.id.account_information_input_info_label)
	protected TextView accountIdentifierFieldRestrictionsLabel;

//INPUT FIELDS
	
	@InjectView(R.id.account_info_main_input_field)
	protected EditText accountIdentifierField;
	
	@InjectView(R.id.account_info_ssn_input_field)
	EditText ssnField;
	
//ERROR LABELS
	
	@InjectView(R.id.account_info_error_label)
	private TextView errorMessageLabel;
	
	@InjectView(R.id.account_info_card_account_number_error_label)
	TextView cardErrorLabel;
	
	@InjectView (R.id.account_info_ssn_error_label)
	TextView ssnErrorLabel;
	
	@InjectView (R.id.account_info_dob_year_error_label)
	TextView dobYearErrorLabel;
	
	@InjectView(R.id.account_info_expiration_date_error_label)
	TextView expirationDateErrorLabel;
	
//DATE PICKER ELEMENTS
	@InjectView(R.id.account_info_card_exp_date_picker)
	EditText cardExpDatePicker;
	
	@InjectView(R.id.account_info_birth_date_picker)
	EditText birthDatePicker;
	
//DATE PICKER DIALOGS
	CustomDatePickerDialog dobPickerDialog;
	CustomDatePickerDialog cardPickerDialog;
	
	
	protected AbstractAccountInformationActivity(final String analyticsPageIdentifier) {
		ANALYTICS_PAGE_IDENTIFIER = analyticsPageIdentifier;
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
    	setupFieldsAndLabels();
    	setupTextChangedListeners();
    	
    	setupCardDatePicker();
    	setupDatePicker();
    	
    	TrackingHelper.trackPageView(ANALYTICS_PAGE_IDENTIFIER);
	}

	/**
	 * Set the title of the screen to the title that a subclass of this activity responds with.
	 * Then do any custom UI setup that is required.
	 */
	private void setupFieldsAndLabels() {
		activityTitleLabel.setText(getString(getActivityTitleLabelResourceId()));
		
		doCustomUiSetup();
	}
	
	private void setupCardDatePicker() {
		cardPickerDialog = new CustomDatePickerDialog(this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				cardExpDatePicker.setText( getFormattedDate(monthOfYear, year) );
				cardExpDatePicker.setBackgroundResource(R.drawable.edit_text_default);
				hideLabel(expirationDateErrorLabel );
				expirationYear = String.valueOf( year );
				expirationMonth = String.valueOf( monthOfYear + 1 );
			}
		}, 2011, 10, 10);
		cardPickerDialog.setTitle(removeLastChar(R.string.card_expiration_date_text));
		cardPickerDialog.hideDayPicker();
	}
	
	private String removeLastChar(final int res) {
		final String subStr = getResources().getString(res);
		return subStr.substring(0, subStr.length() - 1);
	}
	
	private void setupDatePicker() {
		dobPickerDialog = new CustomDatePickerDialog(this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				birthDatePicker.setText( getFormattedDate(monthOfYear, dayOfMonth, year) );
				dobDay = String.valueOf( dayOfMonth );
				dobMonth = String.valueOf( monthOfYear );
				dobYear = String.valueOf( year );
				birthDatePicker.setBackgroundResource(R.drawable.edit_text_default);
				hideLabel(dobYearErrorLabel);
			}
		}, 2011, 10, 10) ;
		dobPickerDialog.setTitle(removeLastChar(R.string.account_info_dob_text));
		
	}
	
	protected String getFormattedDate(final int monthOfYear, final int dayOfMonth, final int year) {
		int month = monthOfYear + 1;
		
		String yearString = String.valueOf(year);
		
		//Get the last 2 digits of the year
		if(yearString.length() > 2){
			yearString = yearString.substring(yearString.length() - 2, yearString.length());
		}
		
		//If the month is a single digit, append a 0 to the front of it.
		if(month < 10) {
			yearString = "0" + month + "/" + yearString;
		}else {
			yearString = month + "/" + dayOfMonth + "/" + yearString;
		}
		
		return yearString;
	}
	
	protected String getFormattedDate(final int monthOfYear, final int year) {
		int month = monthOfYear + 1;
		
		String yearString = String.valueOf(year);
		
		//Get the last 2 digits of the year
		if(yearString.length() > 2){
			yearString = yearString.substring(yearString.length() - 2, yearString.length());
		}
		
		//If the month is a single digit, append a 0 to the front of it.
		if(month < 10) {
			yearString = "0" + month + "/" + yearString;
		}else {
			yearString = month + "/" + yearString;
		}
		
		return yearString;
	}
	
	protected abstract int getActivityTitleLabelResourceId();
	
	protected void doCustomUiSetup() {
		// Intentional no-op for subclasses to override
	}
	
	protected void setupTextChangedListeners(){
    	setupSsnTextChangedListeners();
    	setupCustomTextChangedListeners();
	}
	
	/*Override in subclass*/
	protected void setupCustomTextChangedListeners() {

		accountIdentifierField.setOnFocusChangeListener(new OnFocusChangeListener() {
			InputValidator validator = new InputValidator();
			
				@Override
				public void onFocusChange(final View v, final boolean hasFocus) {
					
					final String acctNbr = accountIdentifierField.getText().toString();
					if(!hasFocus && !validator.isCardAccountNumberValid(acctNbr)){
						showLabel( cardErrorLabel );
						accountIdentifierField.setBackgroundResource(R.drawable.edit_text_red);
						accountIdentifierField.
						setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.x_red), null);

					}
				}
				
		});
		
		accountIdentifierField.addTextChangedListener(new TextWatcher(){
			InputValidator validator = new InputValidator();
			@Override
			public void afterTextChanged(final Editable s) {
				if(validator.isCardAccountNumberValid(s.toString())){
					hideLabel( cardErrorLabel );
					accountIdentifierField.setBackgroundResource(R.drawable.edit_text_default);
					accountIdentifierField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {/*Intentionally empty*/}
			
		});
		

	}
	
	
	private void setupSsnTextChangedListeners(){
		
		ssnField.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				
				if( !hasFocus && ssnField.getText().length() < SSN_LENGTH_OK ) {
					showLabel(ssnErrorLabel);
					ssnField.setBackgroundResource(R.drawable.edit_text_red);
				}
			}
		});
		
		ssnField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(final Editable s) {
				if(s.length() == SSN_LENGTH_OK) {
					//hide error label
					hideLabel(ssnErrorLabel);
					ssnField.setBackgroundResource(R.drawable.edit_text_default);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) 
			{/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) 
			{/*Intentionally empty*/}
			
		});
	}
	
	public void showBirthDatePickerDialog(final View v) {
		dobPickerDialog.show();
	}
	
	public void showCardDatePickerDialog(final View v) {
		
		cardPickerDialog.show();
	}
	
	public void validateInfoAndSubmitOnSuccess(final View v){
		final InputValidator validator = new InputValidator();
		
		final String accountNumString = accountIdentifierField.getText().toString();
		final String memberSsnNumString =  ssnField.getText().toString();
						
		validator.isDobDayValid(dobDay);
		validator.isDobMonthValid(dobMonth);
		validator.isDobYearValid(dobYear);
		validator.isCardExpMonthValid(expirationMonth);
		validator.isCardExpYearValid(expirationYear);
		
		validator.isUidValid(accountNumString);
		validator.isCardAccountNumberValid(accountNumString);
		validator.isSsnValid(memberSsnNumString);
		
		updateLabelsUsingValidator(validator);

		if(areDetailsValid(validator)) {

		accountInformationDetails = new AccountInformationDetails();
		addCustomFieldToDetails(accountInformationDetails, accountNumString);
		accountInformationDetails.socialSecurityNumber = memberSsnNumString;
		accountInformationDetails.dateOfBirthDay = dobDay;
		accountInformationDetails.dateOfBirthMonth = dobMonth;
		accountInformationDetails.dateOfBirthYear = dobYear;
		accountInformationDetails.expirationMonth = expirationMonth;
		accountInformationDetails.expirationYear = expirationYear;
		
			submitFormInfo();
		}
	}
	
	protected abstract void addCustomFieldToDetails(AccountInformationDetails details, String value);
	protected abstract boolean areDetailsValid(InputValidator validator);
	
	private void submitFormInfo() {
		hideLabel(errorMessageLabel);
		progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				checkForStrongAuth();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						// TODO handle
						return true;
						// TEMP temp fix for strange 503 coming back from server on some accounts. v
					case HttpURLConnection.HTTP_UNAVAILABLE:
						showMainErrorLabelWithText(getString(R.string.unkown_error_text));
					return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				Log.d(TAG, "Error message: " + messageErrorResponse.getMessage());
				
				// FIXME add "assertions" for what the HTTP status code should be
				switch (messageErrorResponse.getMessageStatusCode()) {
					case SAMS_CLUB_MEMBER: // Wrong type of account info provided.
						showMainErrorLabelWithText(getString(R.string.account_info_sams_club_card_error_text));
						return true;
						
					case REG_AUTHENTICATION_PROBLEM: // Provided information was incorrect.
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));					
						return true;
						
					case BAD_ACCOUNT_STATUS: // Last attemt with this account number warning.
						showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
						return true;
					
					case FINAL_LOGIN_ATTEMPT:
						showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
						return true;
						
					case MAX_LOGIN_ATTEMPTS:
						sendToErrorPage(ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
						return true;
						
					case INVALID_EXTERNAL_STATUS:
					case ONLINE_STATUS_PROHIBITED:
					case INVALID_ONLINE_STATUS:
						sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
						return true;
						
					case STRONG_AUTH_NOT_ENROLLED:
						sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
						return true;
						
					case PLANNED_OUTAGE:
						sendToErrorPage(ScreenType.SCHEDULED_MAINTENANCE);
						return true;
						
					case FAILED_SECURITY:	
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
						
					default:
						return false;
				}
			}
		};
		
		final NetworkServiceCall<?> serviceCall = createServiceCall(callback, accountInformationDetails);
		serviceCall.submit();
	}
	
	public void showMainErrorLabelWithText(final String text) {
		errorMessageLabel.setText(text);
		showLabel(errorMessageLabel);
	}
	
	protected abstract NetworkServiceCall<?> createServiceCall(
			AsyncCallback<Object> callback, AccountInformationDetails details);
	
	public void goBack(@SuppressWarnings("unused") final View v) {
		// finish() -> same action as pressing the hardware back button.
		finish();
	}
	
	// TODO go through old code and make sure this is called every time
	private void checkForStrongAuth() {
		final AsyncCallback<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final StrongAuthDetails value) {
				Log.d(TAG, "Success");
				progress.dismiss();
				
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {

				progress.dismiss();
				
				if(errorResponse instanceof StrongAuthErrorResponse)
					return handleStrongAuthErrorResponse((StrongAuthErrorResponse)errorResponse);
				
				// TODO handle or remove cases where we don't have handling
				switch (errorResponse.getHttpStatusCode()) {
					
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						getStrongAuthQuestion();
						return true;
				}
				
				return false;
			}
			
			private boolean handleStrongAuthErrorResponse(final StrongAuthErrorResponse errorResponse) {
				if(errorResponse.getResult().endsWith("skipped")) {
					navToNextScreenWithDetails(accountInformationDetails);
					return true;
				}
				else if (errorResponse.getResult().endsWith("challenge")) { 
					getStrongAuthQuestion();
					return true;
				}
				else
					return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());

				// FIXME add "assertions" for what the HTTP status code should be
				switch(messageErrorResponse.getMessageStatusCode()){
				
					case LOCKED_OUT_ACCOUNT:
						sendToErrorPage(ScreenType.STRONG_AUTH_LOCKED_OUT);
						return true;
						
					case STRONG_AUTH_NOT_ENROLLED:
						sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
						return true;
						
					case SAMS_CLUB_MEMBER:
						showMainErrorLabelWithText(getString(R.string.account_info_sams_club_card_error_text));
						return true;
						
					case REG_AUTHENTICATION_PROBLEM: // Provided information was incorrect.
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
						
					case FAILED_SECURITY:
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
					
					case NOT_PRIMARY_CARDHOLDER:
						sendToErrorPage(ScreenType.NOT_PRIMARY_CARDHOLDER);
						return true;
					
					case UNSCHEDULED_MAINTENANCE:
						sendToErrorPage(ScreenType.UNSCHEDULED_MAINTENANCE);
						return true;
						
					case MAX_LOGIN_ATTEMPTS:
						sendToErrorPage(ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
						return true;
						
					case LAST_ATTEMPT_WARNING:
						showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
						return true;
						
					default:
						return false;
				}
			}
		};

		final StrongAuthCheckCall strongAuthCall = new StrongAuthCheckCall(this, callback);
		strongAuthCall.submit();
		
	}
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
	}
	
	private void getStrongAuthQuestion() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		final AsyncCallback<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final StrongAuthDetails value) {

				progress.dismiss();
				strongAuthQuestion = value.questionText;
				strongAuthQuestionId = value.questionId;
				
				navToStrongAuth();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				// FIXME
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
				
				return false;
			}
			
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				
				// FIXME
				switch(messageErrorResponse.getMessageStatusCode()){
				
				default://TODO properly handle these ^ v
					return true;
				}
				
			}
		};

		final GetStrongAuthQuestionCall strongAuthCall = 
				new GetStrongAuthQuestionCall(this, callback);
		strongAuthCall.submit();
		
	}
	
	private static final int STRONG_AUTH_ACTIVITY = 0;
	
	private void navToStrongAuth() {
		
		final Intent strongAuth = new Intent(this, EnhancedAccountSecurityActivity.class);
		
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, strongAuthQuestion);
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, strongAuthQuestionId);
		
		startActivityForResult(strongAuth, STRONG_AUTH_ACTIVITY);
		
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {		
		if(requestCode == STRONG_AUTH_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				navToNextScreenWithDetails(accountInformationDetails);
			} else if (resultCode == RESULT_CANCELED){
				finish();
			}
		}
	}
	
	protected abstract void navToNextScreenWithDetails(AccountInformationDetails details);
	
	protected abstract Class<?> getSuccessfulStrongAuthIntentClass();
	
	private void updateLabelsUsingValidator(final InputValidator validator){

		// FIXME with a better input validator.
		/*
		 * Set error label based on what is valid.
		 * These should never be both true.
		 */
		if("Forgot Password".equals(activityTitleLabel.getText()) && !validator.wasUidValid){
			showLabel(cardErrorLabel);
			accountIdentifierField.setBackgroundResource(R.drawable.edit_text_red);
		}
		else if(!"Forgot Password".equals(activityTitleLabel.getText()) && !validator.wasAccountNumberValid){
			showLabel(cardErrorLabel);
			accountIdentifierField.setBackgroundResource(R.drawable.edit_text_red);
			accountIdentifierField.setCompoundDrawablesWithIntrinsicBounds(null, null, 
					getResources().getDrawable(R.drawable.x_red), null);
		}
		else{
			hideLabel(cardErrorLabel);
			accountIdentifierField.setBackgroundResource(R.drawable.edit_text_default);
			accountIdentifierField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);		
		}
		
		if(!validator.wasCardExpMonthValid || !validator.wasCardExpYearValid){
			showLabel(expirationDateErrorLabel);
			cardExpDatePicker.setBackgroundResource(R.drawable.edit_text_red);
		}else{
			hideLabel(expirationDateErrorLabel);
			cardExpDatePicker.setBackgroundResource(R.drawable.edit_text_default);
		}
		
		if(!validator.wasSsnValid){
			showLabel(ssnErrorLabel);
			ssnField.setBackgroundResource(R.drawable.edit_text_red);
		}
		else{
			hideLabel(ssnErrorLabel);
			ssnField.setBackgroundResource(R.drawable.edit_text_default);

		}
		
		if(!validator.wasDobYearValid || !validator.wasDobDayValid || !validator.wasDobMonthValid){
			showLabel(dobYearErrorLabel);
			birthDatePicker.setBackgroundResource(R.drawable.edit_text_red);
		}
		else{
			hideLabel(dobYearErrorLabel);
			birthDatePicker.setBackgroundResource(R.drawable.edit_text_default);

		}
	}
	
	public void showLabel(final View v){
		v.setVisibility(View.VISIBLE);
	}
	
	public void hideLabel(final View v){
		v.setVisibility(View.GONE);
	}

}
