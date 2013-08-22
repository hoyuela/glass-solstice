package com.discover.mobile.card.login.register;

import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.FAILED_SECURITY;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.FINAL_LOGIN_ATTEMPT;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.INVALID_EXTERNAL_STATUS;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.INVALID_ONLINE_STATUS;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.ONLINE_STATUS_PROHIBITED;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM_SECOND;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.EnhancedAccountSecurityActivity;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.auth.strong.StrongAuthListener;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.ui.CardNotLoggedInCommonActivity;
import com.discover.mobile.card.common.uiwidget.CardExpirationDateEditText;
import com.discover.mobile.card.common.uiwidget.CustomDatePickerDialog;
import com.discover.mobile.card.common.uiwidget.DatePickerEditText;
import com.discover.mobile.card.common.uiwidget.HeaderProgressIndicator;
import com.discover.mobile.card.common.uiwidget.SsnEditText;
import com.discover.mobile.card.common.uiwidget.UsernameOrAccountNumberEditText;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.services.auth.forgot.ForgotBoth;
import com.discover.mobile.card.services.auth.forgot.ForgotPassword;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.utils.CommonUtils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * AbstractAccountInformationActivity this activity handles the forgot user
 * password, both, and registration.
 * 
 * It is an abstract class that is inherited by
 * ForgotPasswordAccountInformationActivity,
 * ForgotBothAccountInformationActivity, and
 * RegistrationAccountInformationActivity.
 * 
 * All of these steps are similar and only require minor adjustments to the UI,
 * so they all use the same basic layout.
 * 
 * @author scottseward
 * 
 */

abstract class ForgotOrRegisterFirstStep extends CardNotLoggedInCommonActivity
        implements OnClickListener {

    protected AccountInformationDetails accountInformationDetails;
    protected static final String FORGOTPASSWORDREFERER = "forgot-password-step1-pg";
    protected static final String FORGOTBOTHREFERER = "forgot-both-step1-pg";

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

    private final String MAIN_ICON_VISIBILITY_KEY = "r";

    private int modalTitleText;
    private int modalBodyText;
    private boolean modalClosesActivity = false;

    // TEXT LABELS
    protected TextView accountIdentifierFieldLabel;
    protected TextView accountIdentifierFieldRestrictionsLabel;
    protected TextView helpNumber;
    protected TextView provideFeedback;
    protected TextView welcomeHeading;
  //Defect id 95853
    protected TextView privacy_terms ;
  //Defect id 95853
    protected TextView cancel;

    // INPUT FIELDS
    protected UsernameOrAccountNumberEditText accountIdentifierField;
    protected SsnEditText ssnField;

    // ERROR LABELS
    protected TextView errorMessageLabel;
    protected TextView cardErrorLabel;
    protected TextView ssnErrorLabel;
    protected TextView dobErrorLabel;
    protected TextView expirationDateErrorLabel;
    protected ImageView errorIcon;

    // SCROLL VIEW
    private ScrollView mainScrollView;

    // DATE PICKER ELEMENTS
    protected CardExpirationDateEditText cardExpDatePicker;
    protected DatePickerEditText birthDatePicker;

    // DATE PICKER DIALOGS
    protected CustomDatePickerDialog dobPickerDialog;
    protected CustomDatePickerDialog cardPickerDialog;

    // BUTTONS
    protected Button continueButton;

    final Calendar currentDate = Calendar.getInstance();

    protected void doCustomUiSetup() {/* Intentionally empty */
    }

    protected abstract void addCustomFieldToDetails(
            AccountInformationDetails details, String value);

    protected abstract Class<?> getSuccessfulStrongAuthIntentClass();
    
    /*  13.4 Code CleanUp*/
/*    protected abstract NetworkServiceCall<?> createServiceCall(
            AsyncCallback<Object> callback, AccountInformationDetails details);*/

    protected ForgotOrRegisterFirstStep(final String analyticsPageIdentifier) {
        ANALYTICS_PAGE_IDENTIFIER = analyticsPageIdentifier;
    }

    protected abstract void setHeaderProgressText();

    protected void setupCustomTextChangedListeners() {
    }

    protected abstract String getScreenType(); 

    private StrongAuthListener strongAuthCheckListener;
    private final int REQUEST_CODE = 0x01;
    
    private boolean isAccountUnlock() {
    	return IntentExtraKey.SCREEN_ACCOUNT_UNLOCK.equals(getScreenType());
    }
    
    private boolean isForgotBoth() {
    	return IntentExtraKey.SCREEN_FORGOT_BOTH.equals(getScreenType());
    }

    private boolean isRegistration() {
    	return IntentExtraKey.SCREEN_REGISTRATION.equals(getScreenType());
    }

    private boolean isForgotPassword() {
    	return IntentExtraKey.SCREEN_FORGOT_PASS.equals(getScreenType());
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (isAccountUnlock()) {
        	setContentView(R.layout.account_unlock_enter_account_info);
        } else {
        	setContentView(R.layout.register_enter_account_info);
        }

        final HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
        progress.initChangePasswordHeader(0);

        loadAllViews();
  /*      13.4 chnages start*/
        setSpinnerStyle();
        /*      13.4 chnages End*/
        setupFieldsAndLabels();
        setupCustomTextChangedListeners();
        provideFeedback.setOnClickListener(this);
        privacy_terms.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setupClickablePhoneNumbers();
        setHeaderProgressText();

        restoreState(savedInstanceState);
        TrackingHelper.trackPageView(ANALYTICS_PAGE_IDENTIFIER);

        strongAuthCheckListener = new StrongAuthListener() {

            @Override
            public void onStrongAuthSucess(final Object data) {
                // TODO Auto-generated method stub
                navToNextScreenWithDetails(accountInformationDetails);
            }

            @Override
            public void onStrongAuthSkipped(final Object data) {
                // TODO Auto-generated method stub
                navToNextScreenWithDetails(accountInformationDetails);
            }

            @Override
            public void onStrongAuthNotEnrolled(final Object data) {
                // TODO Auto-generated method stub
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        ForgotOrRegisterFirstStep.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }

            @Override
            public void onStrongAuthError(final Object data) {
                // TODO Auto-generated method stub
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        ForgotOrRegisterFirstStep.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }

            @Override
            public void onStrongAuthCardLock(final Object data) {
                // TODO Auto-generated method stub
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        ForgotOrRegisterFirstStep.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }
        };
    }

    /*      13.4 chnages start*/
    private void setSpinnerStyle() {
		// TODO Auto-generated method stub
    	cardExpDatePicker.setUpSpinnerStyle(R.drawable.card_spinner_holo , R.drawable.card_spinner_invalid_holo_light);
    	birthDatePicker.setUpSpinnerStyle(R.drawable.card_spinner_holo, R.drawable.card_spinner_invalid_holo_light);
    	cardExpDatePicker.setupDefaultAppearance();
    	birthDatePicker.setupDefaultAppearance();
	}

    /*      13.4 chnages End*/
	/**
     * Initialize the member variables that will reference UI elements.
     */
    public void loadAllViews() {
        welcomeHeading = (TextView)findViewById(R.id.forgot_password);
        accountIdentifierFieldLabel = (TextView) findViewById(R.id.account_info_label_one_label);
        accountIdentifierFieldRestrictionsLabel = (TextView) findViewById(R.id.account_information_input_info_label);
        accountIdentifierField = (UsernameOrAccountNumberEditText) findViewById(R.id.account_info_main_input_field);
        ssnField = (SsnEditText) findViewById(R.id.account_info_ssn_input_field);
        errorMessageLabel = (TextView) findViewById(R.id.account_info_error_label);
        errorIcon = (ImageView) findViewById(R.id.icon);
        cardErrorLabel = (TextView) findViewById(R.id.account_info_card_account_number_error_label);
        ssnErrorLabel = (TextView) findViewById(R.id.account_info_ssn_error_label);
        dobErrorLabel = (TextView) findViewById(R.id.account_info_dob_year_error_label);
        expirationDateErrorLabel = (TextView) findViewById(R.id.account_info_expiration_date_error_label);
        mainScrollView = (ScrollView) findViewById(R.id.account_info_scroll_view);
        birthDatePicker = (DatePickerEditText) findViewById(R.id.account_info_birth_date_picker);
        cardExpDatePicker = (CardExpirationDateEditText) findViewById(R.id.account_info_card_exp_date_picker);
        helpNumber = (TextView) findViewById(R.id.help_number_label);
        continueButton = (Button) findViewById(R.id.account_info_continue_button);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);
        cancel = (TextView) findViewById(R.id.account_info_cancel_label);
      //Defect id 95853
        privacy_terms= (TextView)findViewById(R.id.privacy_terms);
      //Defect id 95853
    }

    /**
     * Check to see if all of the fields on the page contain valid input.
     * 
     * @return true if all fields contain valid information.
     */
    public boolean isFormCompleteAndValid() {
        return accountIdentifierField.isValid() && cardExpDatePicker.isValid()
                && birthDatePicker.isValid() && ssnField.isValid();
    }

    /**
     * When the state of the screen needs to be saved (on orientation change)
     * then save the fields to a bundle.
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putString(MAIN_FIELD_KEY, accountIdentifierField.getText()
                .toString());

        saveCardExpirationDateEditText(outState);
        saveBirthDatePicker(outState);

        outState.putString(SSN_KEY, ssnField.getText().toString());

        outState.putString(MAIN_ERROR_TEXT_KEY, errorMessageLabel.getText()
                .toString());
        outState.putInt(MAIN_ERROR_VISIBILITY_KEY,
                errorMessageLabel.getVisibility());
        outState.putInt(MAIN_ICON_VISIBILITY_KEY, errorIcon.getVisibility());

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

    	if (isAccountUnlock()) {
    		//Account unlock doens't have phone numbers
    		return;
    	}

        final Context currentContext = this;
        helpNumber.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                CommonUtils.dialNumber(helpNumber.getText().toString(),
                        currentContext);
            }
        });
    }

    /**
     * If a valid date of birth has been entered and the state of the screen
     * needs to be saved, save the state to the passed Bundle.
     * 
     * @param outState
     *            the Bundle to save state to.
     */
    private void saveBirthDatePicker(final Bundle outState) {
        if (birthDatePicker.isValid()) {
            outState.putInt(DOB_DAY_KEY, birthDatePicker.getDay());
            outState.putInt(DOB_MONTH_KEY, birthDatePicker.getMonth());
            outState.putInt(DOB_YEAR_KEY, birthDatePicker.getYear());
        }
    }

    /**
     * If a valid expiration date has been entered and the state of the screen
     * needs to be saved, save the state to the passed Bundle.
     * 
     * @param outState
     *            the Bundle to save state to.
     */
    private void saveCardExpirationDateEditText(final Bundle outState) {
        if (cardExpDatePicker.isValid()) {
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

        if (savedInstanceState != null) {
            accountIdentifierField.setText(savedInstanceState
                    .getString(MAIN_FIELD_KEY));

            cardErrorLabel.setVisibility(savedInstanceState
                    .getInt(MAIN_FIELD_ERROR_KEY));
            if (cardErrorLabel.getVisibility() == View.VISIBLE) {
                accountIdentifierField.updateAppearanceForInput();
            }

            ssnField.setText(savedInstanceState.getString(SSN_KEY));

            ssnErrorLabel.setVisibility(savedInstanceState
                    .getInt(SSN_ERROR_KEY));
            if (ssnErrorLabel.getVisibility() == View.VISIBLE) {
                ssnField.updateAppearanceForInput();
            }

            restoreCardExpDatePicker(savedInstanceState);
            restoreDatePickerEditText(savedInstanceState);
            restoreMainErrorLabel(savedInstanceState);

            modalIsPresent = savedInstanceState
                    .getBoolean(MODAL_IS_SHOWING_KEY);
            if (modalIsPresent) {
                displayModal(savedInstanceState.getInt(MODAL_TITLE_KEY),
                        savedInstanceState.getInt(MODAL_BODY_KEY),
                        savedInstanceState
                                .getBoolean(MODAL_CLOSES_ACTIVITY_KEY));
            }
        }
    }

    /**
     * Restore the main error label's text and visibility
     * 
     * @param savedInstanceState
     */
    private void restoreMainErrorLabel(final Bundle savedInstanceState) {
        errorMessageLabel.setVisibility(savedInstanceState
                .getInt(MAIN_ERROR_VISIBILITY_KEY));
        errorIcon.setVisibility(savedInstanceState
                .getInt(MAIN_ICON_VISIBILITY_KEY));
        errorMessageLabel.setText(savedInstanceState
                .getString(MAIN_ERROR_TEXT_KEY));
    }

    /**
     * Restores the DOB date picker to its previous state from a Bundle. If the
     * saved values in the Bundle were invalid, don't update the picker and set
     * its variables to invalid values.
     * 
     * @param savedInstanceState
     *            a Bundle that contains save state information about this date
     *            picker.
     */
    private void restoreDatePickerEditText(final Bundle savedInstanceState) {
        birthDatePicker.setDay(savedInstanceState.getInt(DOB_DAY_KEY));
        birthDatePicker.setMonth(savedInstanceState.getInt(DOB_MONTH_KEY));
        birthDatePicker.setYear(savedInstanceState.getInt(DOB_YEAR_KEY));
        dobErrorLabel.setVisibility(savedInstanceState.getInt(DOB_ERROR_KEY));

        if (birthDatePicker.isValid()) {
            birthDatePicker.updateLabelWithSavedDate();
            birthDatePicker.updateAppearanceForInput();
        } else {
            birthDatePicker.clearData();
            if (dobErrorLabel.getVisibility() == View.VISIBLE) {
                birthDatePicker.updateAppearanceForInput();
            }
        }
    }

    /**
     * Restores the card expiration date picker to its previous state from a
     * Bundle. If the saved values in the Bundle were invalid, don't update the
     * picker and set its variables to invalid values.
     * 
     * @param savedInstanceState
     *            a Bundle that contains save state information about this date
     *            picker.
     */
    private void restoreCardExpDatePicker(final Bundle savedInstanceState) {
        cardExpDatePicker.setMonth(savedInstanceState.getInt(EXP_MONTH_KEY));
        cardExpDatePicker.setYear(savedInstanceState.getInt(EXP_YEAR_KEY));
        expirationDateErrorLabel.setVisibility(savedInstanceState
                .getInt(EXP_ERROR_KEY));

        if (cardExpDatePicker.isValid()) {
            cardExpDatePicker.updateLabelWithSavedDate();
            cardExpDatePicker.updateAppearanceForInput();
        } else {
            cardExpDatePicker.clearData();
            if (expirationDateErrorLabel.getVisibility() == View.VISIBLE) {
                cardExpDatePicker.updateAppearanceForInput();
            }
        }
    }

    /**
     * Set the title of the screen to the title that a subclass of this activity
     * responds with. Then do any custom UI setup that is required.
     */
    private void setupFieldsAndLabels() {
        doCustomUiSetup();

        accountIdentifierField.attachErrorLabel(cardErrorLabel);
        ssnField.attachErrorLabel(ssnErrorLabel);
        birthDatePicker.attachErrorLabel(dobErrorLabel);
        cardExpDatePicker.attachErrorLabel(expirationDateErrorLabel);
    }

    /**
     * Checks to see if the provided information in the form on the screen
     * abides to what is valid, and then if everything passes validation, it is
     * submitted to the server for confirmation of the validation. Called from
     * XML.
     * 
     * @param v
     *            - the calling View.
     */
    public void validateInfoAndSubmitOnSuccess(final View v) {
        updateAllErrorStates();
        
        if (isFormCompleteAndValid()) {
            // submitFormInfo();
            submit();
        } else {
        	/*Defect id 95859*/
        	if(!(accountIdentifierField.isNull()&& ssnField.isNull() && birthDatePicker.isNull() && cardExpDatePicker.isNull())){
        		showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
        	}
            
            resetScrollPosition();
        }

    }

    /**
     * Update the state of all input fields based on their input and hide the
     * main error label.
     */
    private void updateAllErrorStates() {
        CommonUtils.setViewGone(errorMessageLabel);
        CommonUtils.setViewGone(errorIcon);
        accountIdentifierField.updateAppearanceForInput();
        birthDatePicker.updateAppearanceForInput();
        cardExpDatePicker.updateAppearanceForInput();
        ssnField.updateAppearanceForInput();
    }

    /**
     * Takes all of the information in the form and saves it to the
     * accountInformationDetails object. addCustomFieldToDetails determines the
     * type of account information to provide from the main input field. It is
     * overridden in subclasses of AbstractAccountInformationActivity. This
     * object is used when submitting information to the server.
     */
    private void saveFormDetailsToObject() {
        final String accountNumString = accountIdentifierField.getText()
                .toString();
        final String memberSsnNumString = ssnField.getText().toString();

        accountInformationDetails = new AccountInformationDetails();
        addCustomFieldToDetails(accountInformationDetails, accountNumString);
        accountInformationDetails.socialSecurityNumber = memberSsnNumString;
        accountInformationDetails.dateOfBirthDay = String
                .valueOf(birthDatePicker.getDay());
        accountInformationDetails.dateOfBirthMonth = String
                .valueOf(birthDatePicker.getMonth() + 1);
        accountInformationDetails.dateOfBirthYear = String
                .valueOf(birthDatePicker.getYear());
        accountInformationDetails.expirationMonth = String
                .valueOf(cardExpDatePicker.getMonth() + 1);
        accountInformationDetails.expirationYear = String
                .valueOf(cardExpDatePicker.getYear());
    }

    /**
     * Set the text of the main error label to the given String value and set it
     * to visible.
     * 
     * @param text
     *            the String value to set the error label to.
     */
    public void showMainErrorLabelWithText(final String text) {
        errorMessageLabel.setText(text);
        CommonUtils.setViewVisible(errorMessageLabel);
        CommonUtils.setViewVisible(errorIcon);
    }

    /**
     * Called from XML for the cancel 'hyperlink' style button.
     * 
     * @param v
     *            the calling View
     */
    public void goBack(@SuppressWarnings("unused") final View v) {
        goBack();
    }

    /**
     * The inherited goBack method from NotLoggedInRoboActivity for the software
     * back button.
     */
    @Override
    public void goBack() {
        final Intent forgotCredentials = new Intent(this,
                ForgotCredentialsActivity.class);
        startActivity(forgotCredentials);
        finish();
    }

    /**
     * Animate scrolling the screen to the top. Used when something has gone
     * wrong. Bad input etc.
     */
    public void resetScrollPosition() {
        mainScrollView.smoothScrollTo(0, 0);
    }

    /**
     * This method handles the result of the Strong Auth activity. When Strong
     * Auth finishes, either navigate to the next screen, or cancel the
     * registration process.
     */
    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE) { // REQUEST_CODE
                                           // //STRONG_AUTH_ACTIVITY
            if (resultCode == RESULT_OK) {
                navToNextScreenWithDetails(accountInformationDetails);
            } else if (resultCode == RESULT_CANCELED) {
                goBack();
            }
            // If account locked by strong auth, user will be redirect back
            else if (resultCode == EnhancedAccountSecurityActivity.STRONG_AUTH_LOCKED) {
                goBack();
            }
        }
    }

    /**
     * Put all of the form details as a serializable object extra and pass it to
     * the next activity which will append more info onto that object.
     */
    protected void navToNextScreenWithDetails(
            final AccountInformationDetails details) {
        final Intent createLoginActivity = new Intent(this,
                getSuccessfulStrongAuthIntentClass());
        createLoginActivity.putExtra(IntentExtraKey.SCREEN_TYPE, getScreenType());
        createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, details);

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

    private void displayModal(final int titleText, final int bodyText,
            final boolean finishActivityOnClose) {
        modalBodyText = bodyText;
        modalTitleText = titleText;
        modalClosesActivity = finishActivityOnClose;

        showErrorModalForRegistration(titleText, bodyText,
                finishActivityOnClose);
    }

    /**
     * Submitting account information to server. If server returns success then
     * check for strong authentication
     */
    public void submit() {

        saveFormDetailsToObject();
        // Check whether its is for Forgot Password or Forgot Both/Register
        try {
            if (accountIdentifierField.isUsernameField()) {
                final ForgotPassword forgotPassword = new ForgotPassword(
                        ForgotOrRegisterFirstStep.this,
                        new CardEventListener() {

                            @Override
                            public void onSuccess(final Object data) {

                                // This is hook for Strong Auth Check
                                final StrongAuthHandler authHandler = new StrongAuthHandler(
                                        ForgotOrRegisterFirstStep.this,
                                        strongAuthCheckListener, false);
                                authHandler.strongAuth();
                            }

                            @Override
                            public void OnError(final Object data) {
                                final String errorCode = ((CardErrorBean) data)
                                        .getErrorCode();
                                final String[] errorMsgSplit = errorCode
                                        .split("_");
                                final int errorCodeNumber = Integer
                                        .parseInt(errorMsgSplit[0]);
                                switch (errorCodeNumber) {

                                case REG_AUTHENTICATION_PROBLEM_SECOND:
                                case REG_AUTHENTICATION_PROBLEM:
                                    showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
                                    break;
                                case FINAL_LOGIN_ATTEMPT:
                                    showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
                                    break;
                                case FAILED_SECURITY:
                                    showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
                                    break;
                                case BAD_ACCOUNT_STATUS:
                                case MAX_LOGIN_ATTEMPTS:
                                case INVALID_EXTERNAL_STATUS:
                                case ONLINE_STATUS_PROHIBITED:
                                case INVALID_ONLINE_STATUS:
                                default:
                                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                                            ForgotOrRegisterFirstStep.this);
                                    cardErrorResHandler
                                            .handleCardError((CardErrorBean) data);
                                }

                            }
                        }, accountInformationDetails);
                forgotPassword.sendRequest();
            } else {
                final ForgotBoth forgotBoth = new ForgotBoth(
                        ForgotOrRegisterFirstStep.this,
                        new CardEventListener() {

                            @Override
                            public void onSuccess(final Object data) {

                                // This is hook for Strong Auth Check
                                final StrongAuthHandler authHandler = new StrongAuthHandler(
                                        ForgotOrRegisterFirstStep.this,
                                        strongAuthCheckListener, false);
                                authHandler.strongAuth();
                            }

                            @Override
                            public void OnError(final Object data) {
                                final String errorCode = ((CardErrorBean) data)
                                        .getErrorCode();
                                final String[] errorMsgSplit = errorCode
                                        .split("_");
                                final int errorCodeNumber = Integer
                                        .parseInt(errorMsgSplit[0]);
                                switch (errorCodeNumber) {

                                case REG_AUTHENTICATION_PROBLEM_SECOND:
                                case REG_AUTHENTICATION_PROBLEM:
                                    showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
                                    break;
                                case FINAL_LOGIN_ATTEMPT:
                                    showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
                                    break;
                                case FAILED_SECURITY:
                                    showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
                                    break;
                                case BAD_ACCOUNT_STATUS:
                                case MAX_LOGIN_ATTEMPTS:
                                case INVALID_EXTERNAL_STATUS:
                                case ONLINE_STATUS_PROHIBITED:
                                case INVALID_ONLINE_STATUS:
                                default:
                                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                                            ForgotOrRegisterFirstStep.this);
                                    cardErrorResHandler
                                            .handleCardError((CardErrorBean) data);
                                }

                            }
                        }, accountInformationDetails);
                forgotBoth.sendRequest();
            }
        } catch (final JsonGenerationException e) {
            handleError(e);
        } catch (final JsonMappingException e) {
            handleError(e);
        } catch (final IOException e) {
            handleError(e);
        }
    }

    /**
     * This method application error if any occurs
     * 
     * @param Exception
     */
    private void handleError(final Exception e) {
        e.printStackTrace();
        final CardErrorBean cardErrorBean = new CardErrorBean(e.toString(),
                true);
        final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                ForgotOrRegisterFirstStep.this);
        cardErrorResHandler.handleCardError(cardErrorBean);
    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        return CardErrorUIWrapper.getInstance();
    }

    @Override
    public void onSuccess(final Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnError(final Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextView getErrorLabel() {
        return errorMessageLabel;
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public List<EditText> getInputFields() {
        return null;

    }

}
