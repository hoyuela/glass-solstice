package com.discover.mobile.card.login.register;

import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.ID_ALREADY_TAKEN;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.ID_AND_SSN_EQUAL;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM_SECOND;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.utils.CommonUtils;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.common.uiwidget.ConfirmationEditText;
import com.discover.mobile.card.common.uiwidget.EmailEditText;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.card.services.auth.registration.CreateLoginDetails;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * CreateLoginActivity - this is the final step of a user either going through
 * "Forgot Both" or "Register". This activity takes all of the information
 * submitted from step 1 and adds it to the information gathered on this
 * activity. Then all of that information together is submitted to register (or
 * re-register) the user.
 * 
 * @author scottseward
 * 
 */
public class CreateLoginActivity extends ForgotOrRegisterFinalStep implements
        CardErrorHandlerUi, OnClickListener,CardEventListener { //DEFECT 96936
    private final String TAG = ForgotOrRegisterFinalStep.class.getSimpleName();

    private CreateLoginDetails formDataTwo;

    private static final String UPDATE_PASS_CONFIRM_STATE = "a";
    private static final String UPDATE_ID_CONFIRM_STATE = "b";
    private static final String UPDATE_EMAIL = "c";
    private static final String ERROR_1 = "d";
    private static final String EROR_STRING_1 = "e";
    private static final String ERROR_2 = "f";
    private static final String ERROR_STRING_2 = "g";
    private static final String SERVER_ERROR = "h";
    private static final String SERVER_ERROR_STRING = "i";
    private static final String UPDATE_PASSWORD_STATE = "k";
    private static final String UPDATE_ID_STATE = "l";
    private static final String REFERER = "forgot-both-step2-pg";
    private static final String ERROR_ICON = "m";

    // TEXT LABELS
    private TextView provideFeedback;

    // ERROR LABELS
    private TextView mainErrorMessageLabel;
    private TextView mainErrorMessageLabelTwo;
    private TextView errorMessageLabel;
    private TextView idConfirmErrorLabel;
    private TextView passConfirmErrorLabel;
    private TextView emailErrorLabel;
    private ImageView errorIcon;

    // SCROLL VIEW
    private ScrollView mainScrollView;

    // INPUT FIELDS
    private EmailEditText emailField;
    private CredentialStrengthEditText idField;
    private ConfirmationEditText idConfirmField;
    private CredentialStrengthEditText passField;
    private ConfirmationEditText passConfirmField;

    private boolean idIsError = false;
    private boolean isPassError = false;
    private boolean isEmailError = false;
    private boolean isError1 = false;
    private boolean isError2 = false;
    private boolean isServerError = false;

    // HEADER PROGRESS BAR
    private HeaderProgressIndicator headerProgressIndicator;

    // BUTTONS
    private final Activity currentActivity = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_create_credentials);
        loadAllViews();
        Utils.isSpinnerShow =true;        
        Utils.hideSpinner();
        attachErrorLabelsToFields();
        mergeAccountDetails();

        TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_STEP2);
        setupStrengthBars();
        setupConfirmationFields();
        getPreviousScreenType();

        setupHeaderProgress();
        setupHelpNumber();
        provideFeedback.setOnClickListener(this);
        restoreState(savedInstanceState);
        
        
        Utils.log("PageTimeOutUtil.getInstance","in side CreateLoginActivity");
        PageTimeOutUtil.getInstance(this.getContext()).startPageTimer();
        
    }

    protected void getPreviousScreenType() {
        isForgotFlow = getIntent().getBooleanExtra(
                IntentExtraKey.SCREEN_FORGOT_BOTH, false);
    }

    /**
     * Resume the fragment
     */
    @Override
    public void onResume() {
        super.onResume();
        showErrors();
    }

    private void showErrors() {
        if (isEmailError) {
            emailField.setErrors();
        }
        if (idIsError) {
            idConfirmField.setErrors();
        } else {
            idConfirmField.setupDefaultAppearance();
        }
        if (isPassError) {
            passConfirmField.setErrors();
        } else {
            passConfirmField.setupDefaultAppearance();
        }
        //Defect id 95719
        /*if (isError1) {
            mainErrorMessageLabel.setVisibility(View.VISIBLE);
        }*/
        //Defect id 95719
        if (isError2 ) {
            mainErrorMessageLabelTwo.setVisibility(View.VISIBLE);
            errorIcon.setVisibility(View.VISIBLE);
        }
        if (isServerError) {
            errorMessageLabel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Save the state of the screen.
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(UPDATE_ID_STATE, idField.isInDefaultState);
        outState.putBoolean(UPDATE_EMAIL, emailField.isInErrorState);
        outState.putBoolean(UPDATE_ID_CONFIRM_STATE,
                idConfirmField.isInErrorState);
        outState.putBoolean(UPDATE_PASSWORD_STATE, passField.isInDefaultState);
        outState.putBoolean(UPDATE_PASS_CONFIRM_STATE,
                passConfirmField.isInErrorState);
        //Defect id 95719
      /*  if (mainErrorMessageLabel.getVisibility() == View.VISIBLE) {
            outState.putBoolean(ERROR_1, true);
            outState.putString(EROR_STRING_1, mainErrorMessageLabel.getText()
                    .toString());
        }*/
        //Defect id 95719
        if (mainErrorMessageLabelTwo.getVisibility() == View.VISIBLE) {
            outState.putBoolean(ERROR_2, true);
            outState.putBoolean(ERROR_ICON, true);
            outState.putString(ERROR_STRING_2, mainErrorMessageLabelTwo
                    .getText().toString());
        }
        if (errorMessageLabel.getVisibility() == View.VISIBLE) {
            outState.putBoolean(SERVER_ERROR, true);
            outState.putString(SERVER_ERROR_STRING, errorMessageLabel.getText()
                    .toString());
        }
    }

    /**
     * Restore the state of the screen.
     * 
     * @param savedInstanceState
     *            - a Bundle containing saved state information.
     */
    public void restoreState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            idField.isInDefaultState = savedInstanceState
                    .getBoolean(UPDATE_ID_STATE);
            passField.isInDefaultState = savedInstanceState
                    .getBoolean(UPDATE_PASSWORD_STATE);

            idIsError = savedInstanceState.getBoolean(UPDATE_ID_CONFIRM_STATE,
                    false);
            isPassError = savedInstanceState.getBoolean(
                    UPDATE_PASS_CONFIRM_STATE, false);
            isEmailError = savedInstanceState.getBoolean(UPDATE_EMAIL, false);
            isError1 = savedInstanceState.getBoolean(ERROR_1, false);
            isError2 = savedInstanceState.getBoolean(ERROR_2, false);
            isServerError = savedInstanceState.getBoolean(SERVER_ERROR, false);
            //Defect id 95719
           /* mainErrorMessageLabel.setText(savedInstanceState
                    .getString(EROR_STRING_1));*/
            //Defect id 95719
            mainErrorMessageLabelTwo.setText(savedInstanceState
                    .getString(ERROR_STRING_2));
            errorMessageLabel.setText(savedInstanceState
                    .getString(SERVER_ERROR_STRING));
        }
    }

    /**
     * Attach error lables to be hidden/shown for these input fields based on
     * the valididty of their input.
     */
    private void attachErrorLabelsToFields() {
        emailField.attachErrorLabel(emailErrorLabel);
        idConfirmField.attachErrorLabel(idConfirmErrorLabel);
        passConfirmField.attachErrorLabel(passConfirmErrorLabel);
    }

    /**
     * Checks to see if all information on the screen is valid.
     * 
     * @return Returns true if all information on the screen is valid.
     */
    private boolean isFormCompleteAndValid() {
        return emailField.isValid() && idField.isValid()
                && idConfirmField.isValid() && passField.isValid()
                && passConfirmField.isValid();
    }

    /**
     * Assign all local variables to view elements that we will need to access.
     */
    private void loadAllViews() {
        passConfirmField = (ConfirmationEditText) findViewById(R.id.account_info_two_pass_confirm_field);
        passField = (CredentialStrengthEditText) findViewById(R.id.account_info_two_pass_field);
        idConfirmField = (ConfirmationEditText) findViewById(R.id.account_info_two_id_confirm_field);
        idField = (CredentialStrengthEditText) findViewById(R.id.account_info_two_id_field);
        emailField = (EmailEditText) findViewById(R.id.account_info_two_email_field);

        mainErrorMessageLabelTwo = (TextView) findViewById(R.id.account_info_error_label_two);
        errorIcon = (ImageView) findViewById(R.id.icon);
        errorMessageLabel = (TextView) findViewById(R.id.account_info_id_confirm_error_label);
        //mainErrorMessageLabel = (TextView) findViewById(R.id.account_info_main_error_label);
        idConfirmErrorLabel = (TextView) findViewById(R.id.account_info_id_confirm_error_label);
        emailErrorLabel = (TextView) findViewById(R.id.account_info_email_error_label);
        passConfirmErrorLabel = (TextView) findViewById(R.id.account_info_pass_two_confirm_error_label);

        mainScrollView = (ScrollView) findViewById(R.id.main_scroll);

        headerProgressIndicator = (HeaderProgressIndicator) findViewById(R.id.header);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);

    }

    /**
     * Attach password and id confirmation fields to their respective primary
     * fields.
     */
    private void setupConfirmationFields() {
        idConfirmField.attachEditTextToMatch(idField);
        passConfirmField.attachEditTextToMatch(passField);
    }

    /**
     * Setup the header progress UI element. With proper text and showing that
     * we are on step 2 of a 3 step process.
     */
    private void setupHeaderProgress() {
        headerProgressIndicator.initChangePasswordHeader(1);
        headerProgressIndicator.setTitle(R.string.enter_info,
                R.string.create_login, R.string.confirm);
        headerProgressIndicator.setPosition(1);
    }

    /**
     * Set the type of input that the strength bars should check against.
     */
    private void setupStrengthBars() {
        idField.setCredentialType(CredentialStrengthEditText.USERID);
        passField.setCredentialType(CredentialStrengthEditText.PASSWORD);
    }

    /**
     * Take the account details POJO from step 1 and merge it into a POJO for
     * step 2.
     */
    private void mergeAccountDetails() {
        final AccountInformationDetails formDataOne = (AccountInformationDetails) getIntent()
                .getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);

        if (formDataOne != null) {
            formDataTwo = new CreateLoginDetails();

            formDataTwo.acctNbr = formDataOne.acctNbr;
            formDataTwo.dateOfBirthDay = formDataOne.dateOfBirthDay;
            formDataTwo.dateOfBirthMonth = formDataOne.dateOfBirthMonth;
            formDataTwo.dateOfBirthYear = formDataOne.dateOfBirthYear;
            formDataTwo.expirationMonth = formDataOne.expirationMonth;
            formDataTwo.expirationYear = formDataOne.expirationYear;
            formDataTwo.socialSecurityNumber = formDataOne.socialSecurityNumber;
        } else {
            Utils.log(TAG, "UNABLE TO MERGE ACCOUNT DETAILS");
        }
    }

    /**
     * If all of the form information is complete, then save the info in our
     * POJO and submit it to the server for server side validation. Otherwise,
     * scroll to the top of the page and display an error.
     * 
     * @param v
     */
    public void checkInputsThenSubmit(final View v) {
        //Defect id 95719
        CommonUtils.setViewGone(mainErrorMessageLabelTwo);
        CommonUtils.setViewGone(errorIcon);
        //Defect id 95719
        
        emailField.updateAppearanceForInput();
        passField.updateAppearanceForInput();
        idField.updateAppearanceForInput();
        passConfirmField.updateAppearanceForInput();
        idConfirmField.updateAppearanceForInput();

        if (isFormCompleteAndValid()) {
            formDataTwo.email = emailField.getText().toString();
            formDataTwo.password = passField.getText().toString();
            formDataTwo.passwordConfirm = formDataTwo.password;
            formDataTwo.userId = idField.getText().toString();
            formDataTwo.userIdConfirm = formDataTwo.userId;
            submitFormInfo();
        } else {
            mainScrollView.smoothScrollTo(0, 0);
            //Defect id 95719
            CommonUtils
                    .showLabelWithStringResource(mainErrorMessageLabelTwo,
                            R.string.account_info_bad_input_error_text,
                            currentActivity);
            CommonUtils.setViewVisible(errorIcon);
            //Defect id 95719
        }

    }

    /**
     * Submit all of the information present on this screen along with the
     * information from register/forgot both step 1. On success, retrieve the
     * users account information.
     */
    private void submitFormInfo() {

        final CardEventListener cardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(final Object data) {
                // TODO Auto-generated method stub
                final RegistrationConfirmationDetails registrationConfirmationDetails = (RegistrationConfirmationDetails) data;
                retrieveAccountDetailsFromServer(registrationConfirmationDetails);
            }

            @Override
            public void OnError(final Object data) {
                // TODO Auto-generated method stub

                final String errorCode = ((CardErrorBean) data).getErrorCode();
                final String[] errorMsgSplit = errorCode.split("_");
                final int errorCodeNumber = Integer.parseInt(errorMsgSplit[0]);

                switch (errorCodeNumber) {
                case REG_AUTHENTICATION_PROBLEM_SECOND:
                case REG_AUTHENTICATION_PROBLEM: // Provided information was
                                                 // incorrect.
                    //Defect id 95719
                    CommonUtils.showLabelWithStringResource(mainErrorMessageLabelTwo,
                            R.string.account_info_bad_input_error_text,
                            currentActivity);
                    CommonUtils.setViewVisible(errorIcon);
                    //Defect id 95719
                    break;
                case ID_AND_PASS_EQUAL:

                    CommonUtils
                            .showLabelWithStringResource(
                                    mainErrorMessageLabelTwo,
                                    R.string.account_info_two_id_matches_pass_error_text,
                                    currentActivity);
                    CommonUtils.setViewVisible(errorIcon);
                    break;
                case ID_AND_SSN_EQUAL:

                    CommonUtils.showLabelWithStringResource(
                            mainErrorMessageLabelTwo,
                            R.string.id_and_ssn_match_text, currentActivity);
                    CommonUtils.setViewVisible(errorIcon);
                    break;
                case ID_ALREADY_TAKEN:

                    CommonUtils
                            .showLabelWithStringResource(
                                    mainErrorMessageLabelTwo,
                                    R.string.account_info_two_username_in_use_error_text,
                                    currentActivity);
                    CommonUtils.setViewVisible(errorIcon);
                    break;

                default:
                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            CreateLoginActivity.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);

                }

            }
        };
        final WSRequest request = new WSRequest();
        final String authString = NetworkUtility.getAuthorizationString(
                formDataTwo.acctNbr, formDataTwo.password);
        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("Authorization", authString);
        headers.put("X-Override-UID", "true");
        final String url = NetworkUtility.getWebServiceUrl(this,
                R.string.createlogin_url);

        request.setUrl(url);
        request.setHeaderValues(headers);
        request.setMethodtype("POST");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            JacksonObjectMapperHolder.getMapper().writeValue(baos, formDataTwo);
        } catch (final JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        request.setInput(baos.toByteArray());

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(this,
                new RegistrationConfirmationDetails(), "Discover",
                "Loading...", cardEventListener);
        serviceCall.execute(request);
    }

    
  //DEFECT 96936
    public void idealTimeoutLogout() {
        Utils.log("CardNavigationRootActivity", "inside logout...");
        // super.logout();
        
         Utils.isSpinnerAllowed = true;
        final WSRequest request = new WSRequest();
        final String url = NetworkUtility.getWebServiceUrl(this,
                R.string.logOut_url);
        request.setUrl(url);
        request.setMethodtype("POST");
        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, null,
                "Discover", "Signing Out...", this);
        serviceCall.execute(request);

    }


    @Override
    public void OnError(final Object data) {
        // CardErrorResponseHandler cardErrorResHandler = new
        // CardErrorResponseHandler(
        // this);
        // cardErrorResHandler.handleCardError((CardErrorBean) data);
        finish();
        final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
        //clearNativeCache();
        //clearJQMCache(); // Call this method to clear JQM cache.

       
        PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();
    }

    @Override
    public void onSuccess(final Object data) {
        finish();
        final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
        //clearNativeCache();
        //clearJQMCache(); // Call this method to clear JQM cache.
       
        PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();
    }

  //DEFECT 96936
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.discover.mobile.card.error.CardErrorHandlerUi#getCardErrorHandler()
     */
    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return CardErrorUIWrapper.getInstance();
    }

    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(CreateLoginActivity.this, REFERER);
        }
    }

    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return mainErrorMessageLabelTwo;
    }

    @Override
    public List<EditText> getInputFields() {
        // TODO Auto-generated method stub
        final List<EditText> inputFields = new ArrayList<EditText>();
        inputFields.add(idField);
        inputFields.add(passField);
        return inputFields;
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return this;
    }

}
