package com.discover.mobile.card.login.register;

import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.card.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM_SECOND;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.utils.CommonUtils;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.uiwidget.ConfirmationEditText;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.privacyterms.PrivacyTermsLanding;
import com.discover.mobile.card.services.auth.forgot.ForgotPasswordTwoDetails;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * EnterNewPasswordActivit - this activity inherits from
 * AbstractAccountInformationActivity
 * 
 * @author scottseward
 * 
 */
public class EnterNewPasswordActivity extends ForgotOrRegisterFinalStep
        implements CardErrorHandlerUi, OnClickListener,CardEventListener { //DEFECT 96936

    

    private ForgotPasswordTwoDetails passTwoDetails;

    // TEXT LABELS
    private TextView errorMessageLabel;
    private TextView errorLabelOne;
    private TextView errorLabelTwo;
    private TextView provideFeedback;
    //Defect id 95853
    private TextView privacy_terms ;
    //Defect id 95853
    private ImageView errorIcon;

    // INPUT FIELDS
    private CredentialStrengthEditText passOneField;
    private ConfirmationEditText passTwoField;

    // SCROLL VIEW
    private ScrollView mainScrollView;

    private static final String UPDATE_PASS_ONE_STATE = "a";

    private static final String MAIN_ERROR_STRING = "b";
    private static final String MAIN_ERROR_VISIBILITY = "c";
    private static final String MAIN_ICON_VISIBILITY = "d";

    private static final String REFERER = "forgot-password-step2-pg";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_create_password);
        TrackingHelper.trackPageView(AnalyticsPage.FORGOT_PASSWORD_STEP2);
        loadAllViews();
        getPreviousScreenType();
        setupInputFields();
        mergeAccountDetails();
        setupProgressHeader();
        setupHelpNumber();
        Utils.hideSpinner();
        provideFeedback.setOnClickListener(this);
        //Defect id 95853
        privacy_terms.setOnClickListener(this);
        //Defect id 95853
        restoreState(savedInstanceState);
        
       // Utils.log("PageTimeOutUtil.getInstance","in side EnterNewPasswordActivity");
       // PageTimeOutUtil.getInstance(this.getContext()).startPageTimer();
    }

    /**
     * Get a passed boolean to see if the screen that launched this activity was
     * a forgot step. Default to false if it was not provided.
     */
    protected void getPreviousScreenType() {
        isForgotFlow = getIntent().getBooleanExtra(
                IntentExtraKey.SCREEN_FORGOT_BOTH, false);
        isForgotPassword = getIntent().getBooleanExtra(
                IntentExtraKey.SCREEN_FORGOT_PASS, false);
    }

    /**
     * Restore the state of the input fields on the screen upon orientation
     * change.
     * 
     * @param savedInstanceState
     */
    private void restoreState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (!savedInstanceState.getBoolean(UPDATE_PASS_ONE_STATE)) {
                passOneField.updateAppearanceForInput();
            }

            errorMessageLabel.setVisibility(savedInstanceState
                    .getInt(MAIN_ERROR_VISIBILITY));
            errorIcon.setVisibility(savedInstanceState
                    .getInt(MAIN_ICON_VISIBILITY));
            errorMessageLabel.setText(savedInstanceState
                    .getString(MAIN_ERROR_STRING));

        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(UPDATE_PASS_ONE_STATE,
                passOneField.isInDefaultState);

        outState.putString(MAIN_ERROR_STRING, errorMessageLabel.getText()
                .toString());
        outState.putInt(MAIN_ERROR_VISIBILITY,
                errorMessageLabel.getVisibility());
        outState.putInt(MAIN_ICON_VISIBILITY, errorIcon.getVisibility());
    }

    private void setupProgressHeader() {
        final HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
        progress.initChangePasswordHeader(1);
    }

    /**
     * Get all of the view elements from the layout and assign them to local
     * variables that will be used to access them.
     */
    private void loadAllViews() {
        passOneField = (CredentialStrengthEditText) findViewById(R.id.account_info_two_pass_field);
        passTwoField = (ConfirmationEditText) findViewById(R.id.account_info_two_pass_confirm_field);

        errorLabelTwo = (TextView) findViewById(R.id.enter_new_pass_error_two_label);
        errorLabelOne = (TextView) findViewById(R.id.enter_new_pass_error_one_label);
        errorMessageLabel = (TextView) findViewById(R.id.account_info_error_label);
        errorIcon = (ImageView) findViewById(R.id.icon);

        mainScrollView = (ScrollView) findViewById(R.id.main_scroll_view);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);
        //Defect id 95853
        privacy_terms= (TextView)findViewById(R.id.privacy_terms);
        //Defect id 95853

    }

    
    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return errorMessageLabel;
    }
    /**
     * Setup input fields, attach error labels and set the type of input that
     * the fields will receive.
     */
    private void setupInputFields() {
        passOneField.setCredentialType(CredentialStrengthEditText.PASSWORD);
        passTwoField.attachEditTextToMatch(passOneField);
        passOneField.attachErrorLabel(errorLabelOne);
        passTwoField.attachErrorLabel(errorLabelTwo);
    }

    /**
     * Take the details from the first page and merge them into a POJO that will
     * be sent to the server from this page.
     */
    private void mergeAccountDetails() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            passTwoDetails = new ForgotPasswordTwoDetails();
            final AccountInformationDetails passOneDetails = (AccountInformationDetails) getIntent()
                    .getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);
            if (passOneDetails != null) {
                passTwoDetails.userId = passOneDetails.userId;
                passTwoDetails.dateOfBirthDay = passOneDetails.dateOfBirthDay;
                passTwoDetails.dateOfBirthMonth = passOneDetails.dateOfBirthMonth;
                passTwoDetails.dateOfBirthYear = passOneDetails.dateOfBirthYear;
                passTwoDetails.expirationMonth = passOneDetails.expirationMonth;
                passTwoDetails.expirationYear = passOneDetails.expirationYear;
                passTwoDetails.socialSecurityNumber = passOneDetails.socialSecurityNumber;
            }
        }

    }

    /**
     * Take the information provided by the user and send it to the server for
     * serverside validation.
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
                case REG_AUTHENTICATION_PROBLEM:
                    CommonUtils.showLabelWithStringResource(errorMessageLabel,
                            R.string.account_info_bad_input_error_text,
                            currentContext);
                case ID_AND_PASS_EQUAL:

                    CommonUtils
                            .showLabelWithStringResource(
                                    errorMessageLabel,
                                    R.string.account_info_two_id_matches_pass_error_text,
                                    currentContext);
                default:
                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            EnterNewPasswordActivity.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);

                }
            }

        };
        final WSRequest request = new WSRequest();

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("X-SEC-Token", "");
        final String url = NetworkUtility.getWebServiceUrl(this,
                R.string.createpassword_url);

        request.setUrl(url);
        request.setHeaderValues(headers);
        request.setMethodtype("POST");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            JacksonObjectMapperHolder.getMapper().writeValue(baos,
                    passTwoDetails);
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

    /**
     * If all of the information is valid on the page then submit the info to
     * get validated by the server.
     * 
     * @param v
     */
    public void checkInputsThenSubmit(final View v) {
        passOneField.updateAppearanceForInput();
        passTwoField.updateAppearanceForInput();
        CommonUtils.setViewGone(errorMessageLabel);
        CommonUtils.setViewGone(errorIcon);

        // If the info was all valid - submit it to the service call.
        if (passOneField.isValid() && passTwoField.isValid()) {
            final String passOneFieldValue = passOneField.getText().toString();

            passTwoDetails.password = passOneFieldValue;
            passTwoDetails.passwordConfirm = passTwoDetails.password;
            submitFormInfo();
        } else {
            mainScrollView.smoothScrollTo(0, 0);
            if (!passOneField.isValid()) {
                passOneField.setStrengthMeterInvalid();
            }

            CommonUtils.showLabelWithStringResource(errorMessageLabel,
                    R.string.account_info_bad_input_error_text, this);
            CommonUtils.setViewVisible(errorIcon);
        }

    }

   /* //DEFECT 96936
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
        final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
        finish();
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
        //clearNativeCache();
        //clearJQMCache(); // Call this method to clear JQM cache.
       
        PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();
    }*/
    
    
    
    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return this;
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
            Utils.createProvideFeedbackDialog(EnterNewPasswordActivity.this,
                    REFERER);
            //Defect id 95853
        }else if(v.getId() == R.id.privacy_terms)
        {
          //Changes for 13.4 start
//          FacadeFactory.getBankFacade().navToCardPrivacyTerms();
            Intent privacyTerms = new Intent(EnterNewPasswordActivity.this , PrivacyTermsLanding.class);
            startActivity(privacyTerms);
            //Changes for 13.4 end
        }
        //Defect id 95853
    }
}
