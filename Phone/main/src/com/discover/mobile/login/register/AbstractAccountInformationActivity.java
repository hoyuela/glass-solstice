package com.discover.mobile.login.register;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.StandardErrorCodes;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.GetStrongAuthQuestionCall;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.auth.registration.RegistrationErrorCodes;
import com.discover.mobile.common.auth.strong.StrongAuthCheckCall;
import com.discover.mobile.common.auth.strong.StrongAuthDetails;
import com.discover.mobile.common.auth.strong.StrongAuthErrorResponse;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.callback.AsyncCallback;
import com.discover.mobile.common.net.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.security.EnhancedAccountSecurityActivity;

@ContentView(R.layout.account_info)
abstract class AbstractAccountInformationActivity extends RoboActivity implements RegistrationErrorCodes, 
																						StandardErrorCodes{
	
	private static final String TAG = AbstractAccountInformationActivity.class.getSimpleName();
	
	private AccountInformationDetails accountInformationDetails;
	
	private final String ANALYTICS_PAGE_IDENTIFIER;
	
	private ProgressDialog progress;
	
	private String strongAuthQuestion;
	private String strongAuthQuestionId;
	
	
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
	
	@InjectView(R.id.account_info_dob_year_field)
	EditText dobYearField;
	
//ERROR LABELS
	
	@InjectView(R.id.account_info_error_label)
	private TextView errorMessageLabel;
	
	@InjectView(R.id.account_info_card_account_number_error_label)
	TextView cardErrorLabel;
	
	@InjectView (R.id.account_info_ssn_error_label)
	TextView ssnErrorLabel;
	
	@InjectView (R.id.account_info_dob_year_error_label)
	TextView dobYearErrorLabel;
	
	protected AbstractAccountInformationActivity(final String analyticsPageIdentifier) {
		ANALYTICS_PAGE_IDENTIFIER = analyticsPageIdentifier;
	}
	
	
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setupSpinnerAdapters();
    	setupFieldsAndLabels();
    	setupTextChangedListeners();
    	
    	TrackingHelper.trackPageView(ANALYTICS_PAGE_IDENTIFIER);
	}

	@InjectView(R.id.account_info_month_spinner)
	private Spinner expirationMonthSpinner;

	@InjectView(R.id.account_info_year_spinner)
	private Spinner expirationYearSpinner;

	@InjectView(R.id.account_info_dob_month_spinner)
	private Spinner birthMonthSpinner;

	@InjectView(R.id.account_info_dob_day_spinner)
	private Spinner birthDaySpinner;
	
	private void setupSpinnerAdapters() {
		setupTimeSpinner(expirationMonthSpinner, R.array.month_array);
		setupTimeSpinner(expirationYearSpinner, R.array.year_array);
		setupTimeSpinner(birthMonthSpinner, R.array.month_array);
		setupTimeSpinner(birthDaySpinner, R.array.day_array);
	}
	
	private void setupTimeSpinner(final Spinner spinner, final int timeResId) {
		final ArrayAdapter<CharSequence> adapter =
				ArrayAdapter.createFromResource(this, timeResId, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	private void setupFieldsAndLabels() {
		activityTitleLabel.setText(getString(getActivityTitleLabelResourceId()));
		
		doCustomUiSetup();
	}
	
	protected abstract int getActivityTitleLabelResourceId();
	
	protected void doCustomUiSetup() {
		// Intentional no-op for subclasses to override
	}
	
	protected void setupTextChangedListeners(){
    	setupYearTextChangedListeners();
    	setupSsnTextChangedListeners();
    	setupCustomTextChangedListeners();
	}
	
	/*Override in subclass*/
	protected void setupCustomTextChangedListeners() {

		accountIdentifierField.setOnFocusChangeListener(new OnFocusChangeListener() {
			InputValidator validator = new InputValidator();
			
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					
					String acctNbr = ((EditText)v).getText().toString();
					if(!hasFocus && !validator.isCardAccountNumberValid(acctNbr)){
						showLabel( cardErrorLabel );
					}
				}
				
		});
		
		accountIdentifierField.addTextChangedListener(new TextWatcher(){
			InputValidator validator = new InputValidator();
			@Override
			public void afterTextChanged(final Editable s) {
				if(validator.isCardAccountNumberValid(s.toString())){
					hideLabel( cardErrorLabel );
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {/*Intentionally empty*/}
			
		});
		

	}
	
	private void setupYearTextChangedListeners(){
		dobYearField.setOnFocusChangeListener(new OnFocusChangeListener() {
			EditText inputField;

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				inputField = (EditText)v;
				if(!hasFocus && inputField.getText().length() < 4) {
					showLabel(dobYearErrorLabel);
				}
			}
		});
		
		dobYearField.addTextChangedListener(new TextWatcher(){
			
			@Override
			public void afterTextChanged(final Editable s) {
				if(s.length() == 4) {
					//hide error label
					hideLabel(dobYearErrorLabel);
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
			EditText inputField;

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				inputField = (EditText)v;
				
				if(!hasFocus && inputField.getText().length() < 4) {
					showLabel(ssnErrorLabel);
				}
			}
		});
		
		ssnField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(final Editable s) {
				if(s.length() == 4) {
					//hide error label
					hideLabel(ssnErrorLabel);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {/*Intentionally empty*/}
			
		});
	}
	
	public void validateInfoAndSubmitOnSuccess(final View v){
		final InputValidator validator = new InputValidator();
		
		final String accountNumString = accountIdentifierField.getText().toString();
		final String cardMonthExpString = expirationMonthSpinner.getSelectedItem().toString();
		final String cardYearExpString = expirationYearSpinner.getSelectedItem().toString();
		final String memberDobMonthString = birthMonthSpinner.getSelectedItem().toString();
		final String memberDobDayString = birthDaySpinner.getSelectedItem().toString();
		final String memberDobYearString = dobYearField.getText().toString();
		final String memberSsnNumString =  ssnField.getText().toString();
					
		validator.isUidValid(accountNumString);
		validator.isCardAccountNumberValid(accountNumString);
		validator.isCardExpMonthValid(cardMonthExpString);
		validator.isCardExpYearValid(cardYearExpString);
		validator.isDobYearValid(memberDobYearString);
		validator.isDobMonthValid(memberDobMonthString);
		validator.isDobDayValid(memberDobDayString);
		validator.isSsnValid(memberSsnNumString);
		
		updateLabelsUsingValidator(validator);

		accountInformationDetails = new AccountInformationDetails();
		addCustomFieldToDetails(accountInformationDetails, accountNumString);
		accountInformationDetails.dateOfBirthDay = memberDobDayString;
		accountInformationDetails.dateOfBirthMonth = memberDobMonthString;
		accountInformationDetails.dateOfBirthYear = memberDobYearString;
		accountInformationDetails.expirationMonth = cardMonthExpString;
		accountInformationDetails.expirationYear  = cardYearExpString;
		accountInformationDetails.socialSecurityNumber = memberSsnNumString;
		
		if(areDetailsValid(validator))
			submitFormInfo();
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
				
				// FIXME convert literals to RegistrationErrorCodes
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
						
					case MAX_LOGIN_ATTEMPTS:
						sendToErrorPage(ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
						return true;
						
					case INVALID_EXTERNAL_STATUS:
					case ONLINE_STATUS_PROHIBITED:
					case INVALID_ONLINE_STATUS:
						sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
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
	
	public void showMainErrorLabelWithText(String text) {
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
					navToFinalScreen();
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

				// FIXME convert literals to RegistrationErrorCodes
				// FIXME add "assertions" for what the HTTP status code should be
				switch(messageErrorResponse.getMessageStatusCode()){
				
					case LOCKED_OUT_ACCOUNT:
						sendToErrorPage(ScreenType.STRONG_AUTH_LOCKED_OUT);
						return true;
						
					case SAMS_CLUB_MEMBER:
						showMainErrorLabelWithText(getString(R.string.account_info_sams_club_card_error_text));
						return true;
						
					case REG_AUTHENTICATION_PROBLEM: // Provided information was incorrect.
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
						
					case BAD_ACCOUNT_STATUS: // Last attemt with this account number warning.
						showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
						return true;
						
					case FAILED_SECURITY:
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
						
					default:// TODO properly handle these ^ v
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
				//TODO handle these properly ^ v
				return false;
			}
			
			@InjectView(R.id.account_info_error_label)
			TextView errorMessageLabel;
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {

				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				
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
				navToFinalScreen();
			} else {
				// TODO if strong auth exits with failure.
			}
		}
	}
	
	private void navToFinalScreen() {
		final Intent createLoginActivity = new Intent(this, getSuccessfulStrongAuthIntentClass());
		createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, accountInformationDetails);
		if("Forgot Both".equals(activityTitleLabel.getText())) 			//$NON-NLS-1$
			createLoginActivity.putExtra("ScreenType", "forgotBoth");   //$NON-NLS-1$ //$NON-NLS-2$
		else if("Forgot Password".equals(activityTitleLabel.getText())) //$NON-NLS-1$
			createLoginActivity.putExtra("ScreenType", "forgotPass");   //$NON-NLS-1$ //$NON-NLS-2$
		
		startActivity(createLoginActivity);
	}
	
	protected abstract Class<?> getSuccessfulStrongAuthIntentClass();
	
	private void updateLabelsUsingValidator(final InputValidator validator){

		// FIXME with a better input validator.
		/*
		 * Set error label based on what is valid.
		 * These should never be both true.
		 */
		if("Forgot Password".equals(activityTitleLabel.getText()) && !validator.wasUidValid){
			showLabel(cardErrorLabel);
		}
		else if(!"Forgot Password".equals(activityTitleLabel.getText()) && !validator.wasAccountNumberValid){
			showLabel(cardErrorLabel);
		}
		else{
			hideLabel(cardErrorLabel);
		}
		
		if(!validator.wasSsnValid){
			showLabel(ssnErrorLabel);
		}
		else
			hideLabel(ssnErrorLabel);
		
		if(!validator.wasDobYearValid){
			showLabel(dobYearErrorLabel);
		}
		else
			hideLabel(dobYearErrorLabel);
	}
	
	public void showLabel(final View v){
		v.setVisibility(View.VISIBLE);
	}
	
	public void hideLabel(final View v){
		v.setVisibility(View.GONE);
	}

}
