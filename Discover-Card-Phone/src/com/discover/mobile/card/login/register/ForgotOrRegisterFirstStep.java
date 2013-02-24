package com.discover.mobile.card.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.FAILED_SECURITY;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_EXTERNAL_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_ONLINE_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.ONLINE_STATUS_PROHIBITED;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.net.error.RegistrationErrorCodes.FINAL_LOGIN_ATTEMPT;
import static com.discover.mobile.common.net.error.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;
import static com.discover.mobile.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.common.net.error.RegistrationErrorCodes.SAMS_CLUB_MEMBER;

import java.net.HttpURLConnection;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.EnhancedAccountSecurityActivity;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.card.services.auth.strong.GetStrongAuthQuestionCall;
import com.discover.mobile.card.services.auth.strong.StrongAuthCheckCall;
import com.discover.mobile.card.services.auth.strong.StrongAuthDetails;
import com.discover.mobile.card.services.auth.strong.StrongAuthErrorResponse;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.ui.widgets.CardExpirationDateEditText;
import com.discover.mobile.common.ui.widgets.CustomDatePickerDialog;
import com.discover.mobile.common.ui.widgets.DatePickerEditText;
import com.discover.mobile.common.ui.widgets.SsnEditText;
import com.discover.mobile.common.ui.widgets.UsernameOrAccountNumberEditText;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * AbstractAccountInformationActivity this activity handles the forgot user password, both, and registration.
 * 
 * It is an abstract class that is inherited by ForgotPasswordAccountInformationActivity, 
 * ForgotBothAccountInformationActivity, and RegistrationAccountInformationActivity.
 * 
 * All of these steps are similar and only require minor adjustments to the UI, so they all use the same basic layout.
 * 
 * @author scottseward
 *
 */

abstract class ForgotOrRegisterFirstStep extends NotLoggedInRoboActivity {

	private final String TAG = ForgotOrRegisterFirstStep.class.getSimpleName();

	protected AccountInformationDetails accountInformationDetails;

	protected final String ANALYTICS_PAGE_IDENTIFIER;

	protected ProgressDialog progress;

	protected String strongAuthQuestion;
	protected String strongAuthQuestionId;

	/**
	 * Keys for use when saving a restoring activity state on screen rotation.
	 */
	private final String MAIN_ERROR_TEXT_KEY = "a";
	private final String MAIN_ERROR_VISIBILITY_KEY = "b";

	private final String MAIN_FIELD_KEY = "c";
	private final String MAIN_FIELD_ERROR_KEY = "d";

	private final String EXP_MONTH_KEY = "e";
	private final String EXP_YEAR_KEY = "f";
	private final String EXP_ERROR_KEY = "g";

	private final String DOB_DAY_KEY = "h";
	private final String DOB_MONTH_KEY = "i";
	private final String DOB_YEAR_KEY = "j";
	private final String DOB_ERROR_KEY = "k";

	private final String SSN_KEY = "l";
	private final String SSN_ERROR_KEY = "m";

	private final String MODAL_IS_SHOWING_KEY = "n";
	private final String MODAL_BODY_KEY = "o";
	private final String MODAL_TITLE_KEY = "p";
	private final String MODAL_CLOSES_ACTIVITY_KEY = "q";

	private int modalTitleText;
	private int modalBodyText;
	private boolean modalClosesActivity = false;

	//TEXT LABELS
	protected TextView accountIdentifierFieldLabel;
	protected TextView accountIdentifierFieldRestrictionsLabel;
	protected TextView helpNumber;

	//INPUT FIELDS
	protected UsernameOrAccountNumberEditText accountIdentifierField;
	protected SsnEditText ssnField;

	//ERROR LABELS
	protected TextView errorMessageLabel;
	protected TextView cardErrorLabel;
	protected TextView ssnErrorLabel;
	protected TextView dobErrorLabel;
	protected TextView expirationDateErrorLabel;

	//SCROLL VIEW
	private ScrollView mainScrollView;

	//DATE PICKER ELEMENTS
	protected CardExpirationDateEditText cardExpDatePicker;
	protected DatePickerEditText birthDatePicker;

	//DATE PICKER DIALOGS
	protected CustomDatePickerDialog dobPickerDialog;
	protected CustomDatePickerDialog cardPickerDialog;

	//BUTTONS
	protected Button continueButton;

	final Calendar currentDate = Calendar.getInstance();

	protected void doCustomUiSetup(){/*Intentionally empty*/}

	protected abstract void addCustomFieldToDetails(AccountInformationDetails details, String value);
	protected abstract Class<?> getSuccessfulStrongAuthIntentClass();
	protected abstract NetworkServiceCall<?> 
	createServiceCall(AsyncCallback<Object> callback, AccountInformationDetails details);

	protected ForgotOrRegisterFirstStep(final String analyticsPageIdentifier) {
		ANALYTICS_PAGE_IDENTIFIER = analyticsPageIdentifier;
	}

	protected abstract void setHeaderProgressText();
	protected void setupCustomTextChangedListeners() {}
	protected abstract boolean isForgotFlow();

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_enter_account_info);

		final HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
		progress.initChangePasswordHeader(0);

		loadAllViews();
		setupFieldsAndLabels();
		setupCustomTextChangedListeners();
		setupClickablePhoneNumbers();
		setHeaderProgressText();

		restoreState(savedInstanceState);
		TrackingHelper.trackPageView(ANALYTICS_PAGE_IDENTIFIER);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!accountIdentifierField.isUsernameField())
			accountIdentifierField.setText(accountIdentifierField.getText().toString());
	}


	/**
	 * Initialize the member variables that will reference UI elements.
	 */
	public void loadAllViews() {
		accountIdentifierFieldLabel = (TextView)findViewById(R.id.account_info_label_one_label);
		accountIdentifierFieldRestrictionsLabel  = (TextView)findViewById(R.id.account_information_input_info_label);
		accountIdentifierField = (UsernameOrAccountNumberEditText)findViewById(R.id.account_info_main_input_field);
		ssnField = (SsnEditText)findViewById(R.id.account_info_ssn_input_field);
		errorMessageLabel = (TextView)findViewById(R.id.account_info_error_label);
		cardErrorLabel = (TextView)findViewById(R.id.account_info_card_account_number_error_label);
		ssnErrorLabel = (TextView)findViewById(R.id.account_info_ssn_error_label);
		dobErrorLabel = (TextView)findViewById(R.id.account_info_dob_year_error_label);
		expirationDateErrorLabel = (TextView)findViewById(R.id.account_info_expiration_date_error_label);
		mainScrollView = (ScrollView)findViewById(R.id.account_info_scroll_view);
		birthDatePicker =(DatePickerEditText)findViewById(R.id.account_info_birth_date_picker);
		cardExpDatePicker = (CardExpirationDateEditText)findViewById(R.id.account_info_card_exp_date_picker);
		helpNumber = (TextView)findViewById(R.id.help_number_label);
		continueButton = (Button)findViewById(R.id.account_info_continue_button);
	}

	/**
	 * Check to see if all of the fields on the page contain valid input.
	 * @return true if all fields contain valid information.
	 */
	public boolean isFormCompleteAndValid() {
		return accountIdentifierField.isValid() && cardExpDatePicker.isValid() && birthDatePicker.isValid()
				&& ssnField.isValid();
	}

	/**
	 * When the state of the screen needs to be saved (on orientation change) then save the fields to a bundle.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		final String mainFieldSpacelessText = CommonUtils.getSpacelessString(accountIdentifierField.getText().toString());
		
		if(!accountIdentifierField.isUsernameField())
			outState.putString(MAIN_FIELD_KEY, mainFieldSpacelessText);
		else
			outState.putString(MAIN_FIELD_KEY, accountIdentifierField.getText().toString());

		saveCardExpirationDateEditText(outState);
		saveBirthDatePicker(outState);

		outState.putString(SSN_KEY, ssnField.getText().toString());

		outState.putString(MAIN_ERROR_TEXT_KEY, errorMessageLabel.getText().toString());
		outState.putInt(MAIN_ERROR_VISIBILITY_KEY, errorMessageLabel.getVisibility());

		outState.putInt(MAIN_FIELD_ERROR_KEY, cardErrorLabel.getVisibility());
		outState.putInt(DOB_ERROR_KEY, dobErrorLabel.getVisibility());
		outState.putInt(EXP_ERROR_KEY, expirationDateErrorLabel.getVisibility());
		outState.putInt(SSN_ERROR_KEY, ssnErrorLabel.getVisibility());

		outState.putBoolean(MODAL_IS_SHOWING_KEY, modalIsPresent);
		outState.putInt(MODAL_TITLE_KEY, modalTitleText);
		outState.putInt(MODAL_BODY_KEY, modalBodyText);
		outState.putBoolean(MODAL_CLOSES_ACTIVITY_KEY, modalClosesActivity);

	}

	/**
	 * Make the help number clickable and dialable.
	 */
	protected void setupClickablePhoneNumbers() {
		final Context currentContext = this;
		helpNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				CommonUtils.dialNumber(helpNumber.getText().toString(), currentContext);				
			}
		});
	}

	/**
	 * If a valid date of birth has been entered and the state of the screen needs to be saved,
	 * save the state to the passed Bundle.
	 * 
	 * @param outState the Bundle to save state to.
	 */
	private void saveBirthDatePicker(final Bundle outState) {
		if(birthDatePicker.isValid()){
			outState.putInt(DOB_DAY_KEY, birthDatePicker.getDay());
			outState.putInt(DOB_MONTH_KEY, birthDatePicker.getMonth());
			outState.putInt(DOB_YEAR_KEY, birthDatePicker.getYear());
		}
	}

	/**
	 * If a valid expiration date has been entered and the state of the screen needs to be saved,
	 * save the state to the passed Bundle.
	 * 
	 * @param outState the Bundle to save state to.
	 */
	private void saveCardExpirationDateEditText(final Bundle outState) {
		if(cardExpDatePicker.isValid()){
			outState.putInt(EXP_MONTH_KEY, cardExpDatePicker.getMonth());
			outState.putInt(EXP_YEAR_KEY, cardExpDatePicker.getYear());
		}
	}

	/**
	 * Restore the savedInstanceState of the activity.
	 * 
	 * @param savedInstanceState
	 */
	public void restoreState(final Bundle savedInstanceState) {

		if(savedInstanceState != null){	
			accountIdentifierField.setText(savedInstanceState.getString(MAIN_FIELD_KEY));
				
			cardErrorLabel.setVisibility(savedInstanceState.getInt(MAIN_FIELD_ERROR_KEY));
			if(cardErrorLabel.getVisibility() == View.VISIBLE)
				accountIdentifierField.updateAppearanceForInput();

			ssnField.setText(savedInstanceState.getString(SSN_KEY));

			ssnErrorLabel.setVisibility(savedInstanceState.getInt(SSN_ERROR_KEY));
			if(ssnErrorLabel.getVisibility() == View.VISIBLE)
				ssnField.updateAppearanceForInput();

			restoreCardExpDatePicker(savedInstanceState);
			restoreDatePickerEditText(savedInstanceState);
			restoreMainErrorLabel(savedInstanceState);

			modalIsPresent = savedInstanceState.getBoolean(MODAL_IS_SHOWING_KEY);
			if(modalIsPresent){
				displayModal(savedInstanceState.getInt(MODAL_TITLE_KEY), 
						savedInstanceState.getInt(MODAL_BODY_KEY), 			
						savedInstanceState.getBoolean(MODAL_CLOSES_ACTIVITY_KEY));
			}
		}
	}

	/**
	 * Restore the main error label's text and visibility
	 * @param savedInstanceState
	 */
	private void restoreMainErrorLabel(final Bundle savedInstanceState) {
		errorMessageLabel.setVisibility(savedInstanceState.getInt(MAIN_ERROR_VISIBILITY_KEY));
		errorMessageLabel.setText(savedInstanceState.getString(MAIN_ERROR_TEXT_KEY));
	}

	/**
	 * Restores the DOB date picker to its previous state from a Bundle. If the saved values
	 * in the Bundle were invalid, don't update the picker and set its variables to invalid
	 * values.
	 * 
	 * @param savedInstanceState a Bundle that contains save state information about this date picker.
	 */
	private void restoreDatePickerEditText(final Bundle savedInstanceState) {
		birthDatePicker.setDay(savedInstanceState.getInt(DOB_DAY_KEY));
		birthDatePicker.setMonth(savedInstanceState.getInt(DOB_MONTH_KEY));
		birthDatePicker.setYear(savedInstanceState.getInt(DOB_YEAR_KEY));
		dobErrorLabel.setVisibility(savedInstanceState.getInt(DOB_ERROR_KEY));

		if(birthDatePicker.isValid()){
			birthDatePicker.updateLabelWithSavedDate();
			birthDatePicker.updateAppearanceForInput();
		}else{
			birthDatePicker.clearData();
			if(dobErrorLabel.getVisibility() == View.VISIBLE)
				birthDatePicker.updateAppearanceForInput();		
		}
	}

	/**
	 * Restores the card expiration date picker to its previous state from a Bundle. If the saved values
	 * in the Bundle were invalid, don't update the picker and set its variables to invalid values.
	 * 
	 * @param savedInstanceState a Bundle that contains save state information about this date picker.
	 */
	private void restoreCardExpDatePicker(final Bundle savedInstanceState) {
		cardExpDatePicker.setMonth(savedInstanceState.getInt(EXP_MONTH_KEY));
		cardExpDatePicker.setYear(savedInstanceState.getInt(EXP_YEAR_KEY));
		expirationDateErrorLabel.setVisibility(savedInstanceState.getInt(EXP_ERROR_KEY));

		if(cardExpDatePicker.isValid()) {
			cardExpDatePicker.updateLabelWithSavedDate();
			cardExpDatePicker.updateAppearanceForInput();
		}else{
			cardExpDatePicker.clearData();
			if(expirationDateErrorLabel.getVisibility() == View.VISIBLE)
				cardExpDatePicker.updateAppearanceForInput();
		}
	}

	/**
	 * Set the title of the screen to the title that a subclass of this activity responds with.
	 * Then do any custom UI setup that is required.
	 */
	private void setupFieldsAndLabels() {
		doCustomUiSetup();

		accountIdentifierField.attachErrorLabel(cardErrorLabel);
		ssnField.attachErrorLabel(ssnErrorLabel);
		birthDatePicker.attachErrorLabel(dobErrorLabel);
		cardExpDatePicker.attachErrorLabel(expirationDateErrorLabel);
	}

	/**
	 * Checks to see if the provided information in the form on the screen abides to what is valid, and then if
	 * everything passes validation, it is submitted to the server for confirmation of the validation. Called from XML.
	 * 
	 * @param v - the calling View.
	 */
	public void validateInfoAndSubmitOnSuccess(final View v){
		updateAllErrorStates();

		if(isFormCompleteAndValid()){
			submitFormInfo();
		}
		else{
			showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));					
			resetScrollPosition();
		}

	}

	/**
	 * Update the state of all input fields based on their input and hide the main error label.
	 */
	private void updateAllErrorStates() {
		CommonUtils.setViewGone(errorMessageLabel);
		accountIdentifierField.updateAppearanceForInput();
		birthDatePicker.updateAppearanceForInput();
		cardExpDatePicker.updateAppearanceForInput();
		ssnField.updateAppearanceForInput();
	}
	/**
	 * Takes all of the information in the form and saves it to the accountInformationDetails object.
	 * addCustomFieldToDetails determines the type of account information to provide from the main
	 * input field. It is overridden in subclasses of AbstractAccountInformationActivity.
	 * This object is used when submitting information to the server.
	 */
	private void saveFormDetailsToObject() {
		final String accountNumString = accountIdentifierField.getText().toString();
		final String memberSsnNumString = ssnField.getText().toString();

		accountInformationDetails = new AccountInformationDetails();
		addCustomFieldToDetails(accountInformationDetails, accountNumString);
		accountInformationDetails.socialSecurityNumber = memberSsnNumString;
		accountInformationDetails.dateOfBirthDay = String.valueOf(birthDatePicker.getDay());
		accountInformationDetails.dateOfBirthMonth = String.valueOf(birthDatePicker.getMonth() + 1);
		accountInformationDetails.dateOfBirthYear = String.valueOf(birthDatePicker.getYear());
		accountInformationDetails.expirationMonth = String.valueOf(cardExpDatePicker.getMonth() + 1);
		accountInformationDetails.expirationYear = String.valueOf(cardExpDatePicker.getYear());
	}

	/**
	 * Send the accountInformationDetails object to the server for server side verification.
	 */
	private void submitFormInfo() {
		saveFormDetailsToObject();

		progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		//Lock orientation while request is being processed
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final NetworkServiceCall<?> sender, final Object value) {
				checkForStrongAuth();

			}

			@Override
			public void complete(final NetworkServiceCall<?> sender, final Object result) {
				//Unlock orientation after request has been processed
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}

			@Override
			public void failure(final NetworkServiceCall<?> sender, final Throwable executionException) {
				//Catch all exception handler
				final BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(sender, executionException);
			}

			@Override
			public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
				progress.dismiss();
				resetScrollPosition();
				switch (errorResponse.getHttpStatusCode()) {
				// TEMP temp fix for strange 503 coming back from server on some accounts. v
				case HttpURLConnection.HTTP_UNAVAILABLE:
					showMainErrorLabelWithText(getString(R.string.unkown_error_text));
					return true;

				default:
					Log.e(TAG, "ERROR SUBMITTING FORM INFO: " + errorResponse.toString());
					showErrorModal(R.string.could_not_complete_request, R.string.unknown_error, false);
					return true;
				}

			}

			@Override
			public boolean handleMessageErrorResponse(final NetworkServiceCall<?> sender, final JsonMessageErrorResponse messageErrorResponse) {
				boolean handled = false;
				progress.dismiss();
				resetScrollPosition();

				// FIXME add "assertions" for what the HTTP status code should be
				switch (messageErrorResponse.getMessageStatusCode()) {
				case SAMS_CLUB_MEMBER: 
					displayModal(R.string.we_are_sorry, R.string.account_info_sams_club_card_error_text, true);
					handled = true;
					break;
				case REG_AUTHENTICATION_PROBLEM: 
					showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));					
					handled = true;
					break;
				case BAD_ACCOUNT_STATUS:
					displayModal(R.string.could_not_complete_request, R.string.problem_authenticating, true);
					handled = true;
					break;
				case FINAL_LOGIN_ATTEMPT:
					showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
					handled = true;
					break;
				case MAX_LOGIN_ATTEMPTS:
					displayModal(R.string.lockout_title, R.string.locked_account, true);
					handled = true;
					break;
				case INVALID_EXTERNAL_STATUS:
				case ONLINE_STATUS_PROHIBITED:
				case INVALID_ONLINE_STATUS:
					displayModal(R.string.could_not_complete_request, R.string.zluba_error, true);
					handled = true;
					break;
				case STRONG_AUTH_NOT_ENROLLED:
					displayModal(R.string.account_security_title_text, R.string.account_security_not_enrolled, true);
					handled = true;
					break;
				case PLANNED_OUTAGE:
					displayModal(R.string.could_not_complete_request, R.string.planned_outage_one, true);
					handled = true;
					break;
				case FAILED_SECURITY:	
					showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
					handled = true;
					break;
				default:
					Log.e(TAG, "UNHANDLED ERROR: " + messageErrorResponse.getMessage());
					break;
				}
				return handled;
			}	
		};

		final NetworkServiceCall<?> serviceCall = createServiceCall(callback, accountInformationDetails);
		serviceCall.submit();
	}

	/**
	 * Set the text of the main error label to the given String value and set it to visible.
	 * 
	 * @param text the String value to set the error label to.
	 */
	public void showMainErrorLabelWithText(final String text) {
		errorMessageLabel.setText(text);
		CommonUtils.setViewVisible(errorMessageLabel);
	}

	/**
	 * Called from XML for the cancel 'hyperlink' style button.
	 * @param v the calling View
	 */
	public void goBack(@SuppressWarnings("unused") final View v) {
		goBack();
	}

	/**
	 * The inherited goBack method from NotLoggedInRoboActivity for the software back button.
	 */
	@Override
	public void goBack() {
		final Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();		
	}


	/**
	 * Animate scrolling the screen to the top. Used when something has gone wrong. Bad input etc.
	 */
	public void resetScrollPosition(){
		mainScrollView.smoothScrollTo(0, 0);
	}

	protected void checkForStrongAuth() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		final AsyncCallbackAdapter<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final NetworkServiceCall<?> sender, final StrongAuthDetails value) {
				progress.dismiss();
				navToNextScreenWithDetails(accountInformationDetails);
			}

			@Override
			public void complete(final NetworkServiceCall<?> sender, final Object result) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}

			@Override
			public void failure(final NetworkServiceCall<?> sender, final Throwable executionException) {
				final BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(sender, executionException);
			}

			@Override
			public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {

				progress.dismiss();

				if(errorResponse instanceof StrongAuthErrorResponse)
					return handleStrongAuthErrorResponse((StrongAuthErrorResponse)errorResponse);

				// TODO handle or remove cases where we don't have handling
				switch (errorResponse.getHttpStatusCode()) {

				case HttpURLConnection.HTTP_UNAUTHORIZED:
					getStrongAuthQuestion();
					return true;

				default:
					Log.e(TAG, "UNHANDLED ERROR: " + errorResponse.toString());
					displayModal(R.string.could_not_complete_request, R.string.unknown_error, false);
					return true;
				}

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
			public boolean handleMessageErrorResponse(final NetworkServiceCall<?> sender, final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();

				switch(messageErrorResponse.getMessageStatusCode()){

				case LOCKED_OUT_ACCOUNT:
					displayModal(R.string.account_security_title_text, R.string.account_security_locked_out, true);
					return true;

				case STRONG_AUTH_NOT_ENROLLED:
					displayModal(R.string.account_security_title_text, R.string.account_security_not_enrolled, true);
					return true;


				default:
					Log.e(TAG, "UNHANDLED ERROR: " + messageErrorResponse.getMessage());
					return false;
				}
			}
		};

		final StrongAuthCheckCall strongAuthCall = new StrongAuthCheckCall(this, callback);
		strongAuthCall.submit();

	}

	/**
	 * If strong auth is required, this call is made to retrieve the question and question ID for Strong Auth.
	 * On success Strong Auth is launched and the question and id are passed to the activity.
	 * It is launched for intent, so once Strong Auth is done, we come back to the launching activity
	 * and decide how to proceed.
	 */
	private void getStrongAuthQuestion() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		final AsyncCallback<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final NetworkServiceCall<?> sender, final StrongAuthDetails value) {

				progress.dismiss();
				strongAuthQuestion = value.questionText;
				strongAuthQuestionId = value.questionId;

				final Intent strongAuth = new Intent(ForgotOrRegisterFirstStep.this, EnhancedAccountSecurityActivity.class);

				strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, strongAuthQuestion);
				strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, strongAuthQuestionId);

				startActivityForResult(strongAuth, STRONG_AUTH_ACTIVITY);

			}

			@Override
			public void complete(final NetworkServiceCall<?> sender, final Object result) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}

			@Override
			public void failure(final NetworkServiceCall<?> sender, final Throwable executionException) {
				final BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(sender, executionException);
			}

			@Override
			public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
				progress.dismiss();

				switch (errorResponse.getHttpStatusCode()) {
				default:
					Log.e(TAG, "UNHANDLED ERROR: " + errorResponse.toString());
					displayModal(R.string.could_not_complete_request, R.string.unknown_error, false);
					return false;
				}
			}

		};

		final GetStrongAuthQuestionCall strongAuthCall = 
				new GetStrongAuthQuestionCall(this, callback);
		strongAuthCall.submit();

	}

	protected final int STRONG_AUTH_ACTIVITY = 0;
	

	/**
	 * This method handles the result of the Strong Auth activity.
	 * When Strong Auth finishes, either navigate to the next screen, or cancel the registration process.
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {	
		if(requestCode == STRONG_AUTH_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				navToNextScreenWithDetails(accountInformationDetails);
			} else if (resultCode == RESULT_CANCELED){
				goBack();
			}
		}
	}

	/**
	 * Put all of the form details as a serializable object extra and pass it to the next activity
	 * which will append more info onto that object.
	 */
	protected void navToNextScreenWithDetails(final AccountInformationDetails details) {
		final Intent createLoginActivity = new Intent(this, getSuccessfulStrongAuthIntentClass());
		createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, details);
		createLoginActivity.putExtra(IntentExtraKey.SCREEN_FORGOT_BOTH, isForgotFlow());

		startActivity(createLoginActivity);
		finish();
	}

	/**
	 * When the hardware back button is pressed, call the goBack method.
	 */
	@Override
	public void onBackPressed() {
		goBack(null);
	}

	private void displayModal(final int titleText, final int bodyText, final boolean finishActivityOnClose){
		modalBodyText = bodyText;
		modalTitleText = titleText;
		modalClosesActivity = finishActivityOnClose;

		showErrorModalForRegistration(titleText, bodyText, finishActivityOnClose);
	}
}
