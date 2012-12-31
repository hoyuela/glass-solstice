package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.FAILED_SECURITY;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_EXTERNAL_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.INVALID_ONLINE_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.ONLINE_STATUS_PROHIBITED;
import static com.discover.mobile.common.StandardErrorCodes.PLANNED_OUTAGE;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.FINAL_LOGIN_ATTEMPT;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.SAMS_CLUB_MEMBER;

import java.net.HttpURLConnection;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.CardExpirationDatePicker;
import com.discover.mobile.common.customui.CustomDatePickerDialog;
import com.discover.mobile.common.customui.DobDatePicker;
import com.discover.mobile.common.customui.SsnEditText;
import com.discover.mobile.common.customui.UsernameOrAccountNumberEditText;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
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

abstract class AbstractAccountInformationActivity extends NotLoggedInRoboActivity {
	
	private static final String TAG = AbstractAccountInformationActivity.class.getSimpleName();
	
	protected AccountInformationDetails accountInformationDetails;
	
	protected final String ANALYTICS_PAGE_IDENTIFIER;
	
	protected ProgressDialog progress;
	
	protected String strongAuthQuestion;
	protected String strongAuthQuestionId;
	
	/**
	 * Keys for use when saving a restoring activity state on screen rotation.
	 */
	private final static String MAIN_FIELD_KEY = "mfk";
	private static final String MAIN_ERROR_KEY = "mek";

	private static final String EXP_MONTH_KEY = "expmk";
	private static final String EXP_YEAR_KEY = "expyk";
	private static final String EXP_ERROR_KEY = "expek";
	
	private static final String DOB_DAY_KEY = "dobdk";
	private static final String DOB_MONTH_KEY = "dobmk";
	private static final String DOB_YEAR_KEY = "dobyk";
	private static final String DOB_ERROR_KEY = "dobek";
	
	private static final String SSN_KEY = "ssnk";
	private static final String SSN_ERROR_KEY = "ssnek";
	
//TEXT LABELS
	protected TextView accountIdentifierFieldLabel;
	protected TextView accountIdentifierFieldRestrictionsLabel;

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
	protected CardExpirationDatePicker cardExpDatePicker;
	protected DobDatePicker birthDatePicker;
	
//DATE PICKER DIALOGS
	protected CustomDatePickerDialog dobPickerDialog;
	protected CustomDatePickerDialog cardPickerDialog;
	
	// TODO go through old code and make sure this is called every time
	protected void checkForStrongAuth() {}
	protected void doCustomUiSetup(){/*Intentionally empty*/}

	protected abstract void addCustomFieldToDetails(AccountInformationDetails details, String value);
	protected abstract void navToNextScreenWithDetails(AccountInformationDetails details);
	protected abstract Class<?> getSuccessfulStrongAuthIntentClass();
	protected abstract NetworkServiceCall<?> 
					createServiceCall(AsyncCallback<Object> callback, AccountInformationDetails details);
	
	protected AbstractAccountInformationActivity(final String analyticsPageIdentifier) {
		ANALYTICS_PAGE_IDENTIFIER = analyticsPageIdentifier;
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_enter_account_info);
		
		loadAllViews();
		setupFieldsAndLabels();
    	setupCustomTextChangedListeners();
    	setupCardDatePicker();
    	setupDatePicker();
    	    	
    	restoreState(savedInstanceState);
    	TrackingHelper.trackPageView(ANALYTICS_PAGE_IDENTIFIER);
	}
	
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
		birthDatePicker =(DobDatePicker)findViewById(R.id.account_info_birth_date_picker);
		cardExpDatePicker = (CardExpirationDatePicker)findViewById(R.id.account_info_card_exp_date_picker);
	}
    
	/**
	 * When the state of the screen needs to be saved (on orientation change) then save the fields to a bundle.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		outState.putString(MAIN_FIELD_KEY, accountIdentifierField.getText().toString());
		
		saveCardExpirationDatePicker(outState);
		saveBirthDatePicker(outState);
		
		outState.putString(SSN_KEY, ssnField.getText().toString());
		
		outState.putInt(MAIN_ERROR_KEY, cardErrorLabel.getVisibility());
		outState.putInt(DOB_ERROR_KEY, dobErrorLabel.getVisibility());
		outState.putInt(EXP_ERROR_KEY, expirationDateErrorLabel.getVisibility());
		outState.putInt(SSN_ERROR_KEY, ssnErrorLabel.getVisibility());
	}

	/**
	 * If a valid date of birth has been entered and the state of the screen needs to be saved,
	 * save the state to the passed Bundle.
	 * 
	 * @param outState the Bundle to save state to.
	 */
	private void saveBirthDatePicker(final Bundle outState) {
		if(birthDatePicker.isValid()){
			outState.putInt(DOB_DAY_KEY, birthDatePicker.getDobDay());
			outState.putInt(DOB_MONTH_KEY, birthDatePicker.getDobMonth());
			outState.putInt(DOB_YEAR_KEY, birthDatePicker.getDobYear());
		}
	}
	
	/**
	 * If a valid expiration date has been entered and the state of the screen needs to be saved,
	 * save the state to the passed Bundle.
	 * 
	 * @param outState the Bundle to save state to.
	 */
	private void saveCardExpirationDatePicker(final Bundle outState) {
		if(cardExpDatePicker.isValid()){
			outState.putInt(EXP_MONTH_KEY, cardExpDatePicker.getExpirationMonth());
			outState.putInt(EXP_YEAR_KEY, cardExpDatePicker.getExpirationYear());
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
			
			cardErrorLabel.setVisibility(savedInstanceState.getInt(MAIN_ERROR_KEY));
			if(cardErrorLabel.getVisibility() == View.VISIBLE)
				accountIdentifierField.updateAppearanceForInput();
			
			ssnField.setText(savedInstanceState.getString(SSN_KEY));

			ssnErrorLabel.setVisibility(savedInstanceState.getInt(SSN_ERROR_KEY));
			if(ssnErrorLabel.getVisibility() == View.VISIBLE)
				ssnField.updateAppearanceForInput();
			
			restoreCardExpDatePicker(savedInstanceState);
			restoreDobDatePicker(savedInstanceState);
			
		}
	}

	/**
	 * Restores the DOB date picker to its previous state from a Bundle. If the saved values
	 * in the Bundle were invalid, don't update the picker and set its variables to invalid
	 * values.
	 * 
	 * @param savedInstanceState a Bundle that contains save state information about this date picker.
	 */
	private void restoreDobDatePicker(final Bundle savedInstanceState) {
		birthDatePicker.setDobDay(savedInstanceState.getInt(DOB_DAY_KEY));
		birthDatePicker.setDobMonth(savedInstanceState.getInt(DOB_MONTH_KEY));
		birthDatePicker.setDobYear(savedInstanceState.getInt(DOB_YEAR_KEY));
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
		cardExpDatePicker.setExpirationMonth(savedInstanceState.getInt(EXP_MONTH_KEY));
		cardExpDatePicker.setExpirationYear(savedInstanceState.getInt(EXP_YEAR_KEY));
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
				
		accountIdentifierField.attachErrorLabel(cardErrorLabel);
		ssnField.attachErrorLabel(ssnErrorLabel);
		birthDatePicker.attachErrorLabel(dobErrorLabel);
		cardExpDatePicker.attachErrorLabel(expirationDateErrorLabel);

		doCustomUiSetup();
	}
	
	/**
	 * setupCardDatePicker() Initializes the card expiration date spinner. Creates a new one with an OnDateSetListener
	 * The OnDateSetListener sets the expirationYear/Month ivars to be used later when submitting 
	 * the form information with a service call
	 * 
	 * Because the card expiration date only includes month and year, the date field is removed by
	 * the method hideDayPicker.
	 */
	private void setupCardDatePicker() {
		cardPickerDialog = new CustomDatePickerDialog(this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				cardExpDatePicker.setExpirationMonth(monthOfYear);
				cardExpDatePicker.setExpirationYear(year);
				cardExpDatePicker.updateLabelWithSavedDate();
				cardExpDatePicker.updateAppearanceForInput();
			}
		}, 2011, 10, 10);
		cardPickerDialog.setTitle(removeLastChar(R.string.card_expiration_date_text));
		cardPickerDialog.hideDayPicker();
	}
	
	
	/**
	 * Returns a String representation of the passed String resource with its last character removed.
	 * Used when setting the title of a date picker dialog. The Strings that are used include an ugly colon that 
	 * doesnt make sense in a popup dialog.
	 * 
	 * @param res A String resource.
	 * @return A String representation of the passed resource with its last character removed.
	 */
	private String removeLastChar(final int res) {
		final String subStr = getResources().getString(res);
		return subStr.substring(0, subStr.length() - 1);
	}
	
	/**
	 * Initialize the date of birth date picker. Assigns an OnDateSetListener to modify the dobDay/Month/Year ivars.
	 * 
	 */
	private void setupDatePicker() {
		dobPickerDialog = new CustomDatePickerDialog(this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				birthDatePicker.setDobDay(dayOfMonth);
				birthDatePicker.setDobMonth( monthOfYear );
				birthDatePicker.setDobYear(year);
				birthDatePicker.updateLabelWithSavedDate();
				birthDatePicker.updateAppearanceForInput();
			}
		}, 2011, 10, 10) ;
		dobPickerDialog.setTitle(removeLastChar(R.string.account_info_dob_text));
		
	}

	protected void setupCustomTextChangedListeners() {}

	/**
	 * present the date of birth date picker dialog. Called from XML.
	 * 
	 * @param v the calling View.
	 */
	public void showBirthDatePickerDialog(final View v) {
		dobPickerDialog.show();
	}
	
	
	/**
	 * Present the date picker dialog for card expiration dates. Called from XML.
	 * 
	 * @param v the calling View
	 */
	public void showCardDatePickerDialog(final View v) {
		
		cardPickerDialog.show();
	}
	
	/**
	 * Checks to see if the provided information in the form on the screen abides to what is valid, and then if
	 * everything passes validation, it is submitted to the server for confirmation of the validation. Called from XML.
	 * 
	 * @param v - the calling View.
	 */
	public void validateInfoAndSubmitOnSuccess(final View v){

		boolean formIsComplete =
				accountIdentifierField.isValid() &&
				birthDatePicker.isValid() &&
				cardExpDatePicker.isValid() &&
				ssnField.isValid();	
		
		updateLabelsForInput();
		
		if(formIsComplete){
			submitFormInfo();
		}
		else{
			resetScrollPosition();
		}
		
	}
	
	/**
	 * Takes all of the information in the form and saves it to the accountInformationDetails object.
	 * addCustomFieldToDetails determines the type of account information to provide from the main
	 * input field. It is overridden in subclasses of AbstractAccountInformationActivity.
	 * This object is used when submitting information to the server.
	 */
	private void saveFormDetailsToObject() {
		String accountNumString = accountIdentifierField.getText().toString();
		String memberSsnNumString = ssnField.getText().toString();
		
		accountInformationDetails = new AccountInformationDetails();
		addCustomFieldToDetails(accountInformationDetails, accountNumString);
		accountInformationDetails.socialSecurityNumber = memberSsnNumString;
		accountInformationDetails.dateOfBirthDay = String.valueOf(birthDatePicker.getDobDay());
		accountInformationDetails.dateOfBirthMonth = String.valueOf(birthDatePicker.getDobMonth());
		accountInformationDetails.dateOfBirthYear = String.valueOf(birthDatePicker.getDobYear());
		accountInformationDetails.expirationMonth = String.valueOf(cardExpDatePicker.getExpirationMonth());
		accountInformationDetails.expirationYear = String.valueOf(cardExpDatePicker.getExpirationYear());
	}
	
	/**
	 * Send the accountInformationDetails object to the server for server side verification.
	 */
	private void submitFormInfo() {
		saveFormDetailsToObject();
		
		progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				checkForStrongAuth();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				resetScrollPosition();
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
				resetScrollPosition();
				
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
	
	/**
	 * Set the text of the main error label to the given String value and set it to visible.
	 * 
	 * @param text the String value to set the error label to.
	 */
	public void showMainErrorLabelWithText(final String text) {
		errorMessageLabel.setText(text);
		showLabel(errorMessageLabel);
	}
	
	/**
	 * Called from XML for the cancel 'hyperlink' style button.
	 * @param v the calling View
	 */
	public void goBack(@SuppressWarnings("unused") final View v) {
		finish();
	}

	/**
	 * If something has gone wrong and the user is not allowed to proceed, this will send them to an error screen.
	 * It also finishes the current activity so that upon a back button press, they will not come back here.
	 * @param screenType the type of error to present to the user.
	 */
	protected void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
	}
	
	/**
	 * Ask all of the input fields to update their apperance for their current inputs.
	 * So if a given field isValid, then clear any possible errors, or if not, they show errors.
	 */
	private void updateLabelsForInput(){
		hideLabel(errorMessageLabel);

		accountIdentifierField.updateAppearanceForInput();
		cardExpDatePicker.updateAppearanceForInput();
		ssnField.updateAppearanceForInput();
		birthDatePicker.updateAppearanceForInput();
	}
	
	/**
	 * Show a view.
	 * @param v the view you want to show.
	 */
	public void showLabel(final View v){
		v.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide a view.
	 * @param v the view you want to hide
	 */
	public void hideLabel(final View v){
		v.setVisibility(View.GONE);
	}

	/**
	 * Animate scrolling the screen to the top. Used when something has gone wrong. Bad input etc.
	 */
	protected void resetScrollPosition(){
		mainScrollView.smoothScrollTo(0, 0);
	}
}
