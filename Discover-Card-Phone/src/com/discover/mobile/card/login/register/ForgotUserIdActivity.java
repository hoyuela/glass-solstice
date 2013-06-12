package com.discover.mobile.card.login.register;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.base.Strings;

import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.facade.FacadeFactory;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorCallbackListener;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.ui.CardNotLoggedInCommonActivity;
import com.discover.mobile.card.common.uiwidget.NonEmptyEditText;
import com.discover.mobile.card.common.uiwidget.UsernameOrAccountNumberEditText;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;

/**
 * This class handles the forgot user ID flow. If a user successfully completes
 * this page they will be logged into the application and presented with a
 * dialog that shows them their user ID, email, and last 4 digits of their
 * account number.
 * 
 * @author scottseward
 * 
 */
public class ForgotUserIdActivity extends CardNotLoggedInCommonActivity
        implements CardEventListener, OnClickListener {


    private static final String REFERER = "forgot-uid-pg";
    private static final String MAIN_ERROR_LABEL_TEXT_KEY = "a";
    private static final String SHOULD_UPDATE_PASS_APPEARANCE = "b";
    private static final String SHOULD_UPDATE_ACCT_NBR_APPEARANCE = "c";
    private static final String MAIN_ERROR_LABEL_VISIBILITY_KEY = "d";
    private static final String PASS_FIELD_TEXT_KEY = "e";
    private static final String CARD_FIELD_TEXT_KEY = "f";

    private static final String MODAL_IS_SHOWING_KEY = "n";
    private static final String MODAL_BODY_KEY = "o";
    private static final String MODAL_TITLE_KEY = "p";
    private static final String MODAL_CLOSES_ACTIVITY_KEY = "q";

    private int modalTitleText;
    private int modalBodyText;
    private boolean modalClosesActivity = false;

    private RegistrationConfirmationDetails confirmationDetails;

    // BUTTONS
    private Button submitButton;

    // ERROR LABELS
    private TextView mainErrLabel;
    private TextView idErrLabel;
    private TextView passErrLabel;

    // TEXT LABELS
    private TextView cancelLabel;
    private TextView helpNumber;
    private TextView provideFeedback;
    //Defect id 95853
    private TextView privacy_terms ;
    //Defect id 95853

    // INPUT FIELDS
    private UsernameOrAccountNumberEditText cardNumField;
    private NonEmptyEditText passField;

    // SCROLL VIEW
    private ScrollView mainScrollView;

    protected final Activity currentContext = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_forgot_id);

        loadAllViews();
        setupInputFields();

        TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);
        //Defect id 95853
        privacy_terms= (TextView)findViewById(R.id.privacy_terms);
        privacy_terms.setOnClickListener(this);
        //Defect id 95853
        provideFeedback.setOnClickListener(this);

        setOnClickActions();
        attachErrorLabels();

        restoreState(savedInstanceState);
    }

    /**
     * Restore the state of input fields and error states if needed.
     * 
     * @param savedInstanceState
     */
    public void restoreState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mainErrLabel.setText(savedInstanceState
                    .getString(MAIN_ERROR_LABEL_TEXT_KEY));
            mainErrLabel.setVisibility(savedInstanceState
                    .getInt(MAIN_ERROR_LABEL_VISIBILITY_KEY));
            if (savedInstanceState.getBoolean(SHOULD_UPDATE_PASS_APPEARANCE)) {
                passField.updateAppearanceForInput();
            }

            if (savedInstanceState
                    .getBoolean(SHOULD_UPDATE_ACCT_NBR_APPEARANCE)) {
                cardNumField.updateAppearanceForInput();
            }

            final String cardText = savedInstanceState
                    .getString(CARD_FIELD_TEXT_KEY);
            final String passText = savedInstanceState
                    .getString(PASS_FIELD_TEXT_KEY);

            if (!Strings.isNullOrEmpty(passText)) {
                passField.setText(passText);
            }

            if (!Strings.isNullOrEmpty(cardText)) {
                cardNumField.setText(cardText);
            }

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
     * Save the state of the error label on the screen so that upon rotation
     * change, we can restore them.
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putString(MAIN_ERROR_LABEL_TEXT_KEY, mainErrLabel.getText()
                .toString());
        outState.putInt(MAIN_ERROR_LABEL_VISIBILITY_KEY,
                mainErrLabel.getVisibility());

        if (passErrLabel.getVisibility() != View.GONE) {
            outState.putBoolean(SHOULD_UPDATE_PASS_APPEARANCE, true);
        }

        if (idErrLabel.getVisibility() != View.GONE) {
            outState.putBoolean(SHOULD_UPDATE_ACCT_NBR_APPEARANCE, true);
        }

        outState.putString(PASS_FIELD_TEXT_KEY, passField.getText().toString());
        outState.putString(CARD_FIELD_TEXT_KEY, cardNumField.getText()
                .toString());

        outState.putBoolean(MODAL_IS_SHOWING_KEY, modalIsPresent);
        outState.putInt(MODAL_TITLE_KEY, modalTitleText);
        outState.putInt(MODAL_BODY_KEY, modalBodyText);
        outState.putBoolean(MODAL_CLOSES_ACTIVITY_KEY, modalClosesActivity);

        super.onSaveInstanceState(outState);
    }

    /**
     * Get the views that we need from the layout and assign them to local
     * references.
     */
    private void loadAllViews() {
        submitButton = (Button) findViewById(R.id.forgot_id_submit_button);

        mainErrLabel = (TextView) findViewById(R.id.forgot_id_submission_error_label);
        idErrLabel = (TextView) findViewById(R.id.forgot_id_id_error_label);
        passErrLabel = (TextView) findViewById(R.id.forgot_id_pass_error_label);

        cancelLabel = (TextView) findViewById(R.id.account_info_cancel_label);
        helpNumber = (TextView) findViewById(R.id.help_number_label);

        cardNumField = (UsernameOrAccountNumberEditText) findViewById(R.id.forgot_id_id_field);
        passField = (NonEmptyEditText) findViewById(R.id.forgot_id_password_field);

        mainScrollView = (ScrollView) findViewById(R.id.main_scroll);
    }

    /**
     * Attach error labels to input fields.
     */
    private void attachErrorLabels() {
        passField.attachErrorLabel(passErrLabel);
        cardNumField.attachErrorLabel(idErrLabel);
    }

    /**
     * Set the card field to accept an account number.
     * 
     * Set the input fields to be able to control the enabled state of the
     * submit button. If both pass and card are valid, the continue button gets
     * enabled.
     * 
     */
    private void setupInputFields() {
        cardNumField.setFieldAccountNumber();
    }

    /**
     * Assign click listeners to buttons and phone number.
     */
    private void setOnClickActions() {
        final String helpNumberString = helpNumber.getText().toString();
        final Context currentContext = this;
        helpNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dialNumber(helpNumberString, currentContext);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                checkInputsAndSubmit();
            }
        });

        cancelLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                goBack();
            }
        });
    }

    /**
     * When the hardware back button is pressed, call goBack().
     */
    @Override
    public void onBackPressed() {
        goBack();
    }

    /**
	 * 
	 */
    private void checkInputsAndSubmit() {
        cardNumField.updateAppearanceForInput();

        if (passField.getText().toString().length() < 5) {
            passField.setErrors();
        } else {
            passField.updateAppearanceForInput();
            Utils.setViewGone(mainErrLabel);

            if (cardNumField.isValid() && passField.isValid()) {
                doForgotUserIdCall();
            } else {
                mainScrollView.smoothScrollTo(0, 0);
                displayOnMainErrorLabel(getString(R.string.login_error));
            }

        }

    }

    /**
     * Submit the form info to the server and handle success or error.
     */
    private void doForgotUserIdCall() {
        // final ProgressDialog progress = ProgressDialog.show(this, "Discover",
        // "Loading...", true);

        // Lock orientation while request is being processed
    	//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // if(Utils.checkNetworkConnection(this))
        {

            // new ForgotUserIdPassword().execute();
            final String[] data = new String[2];
            data[0] = cardNumField.getText().toString().replace(" ", "");
            data[1] = passField.getText().toString();

            // Cts:Commented code was to check global cache functionality.
            /*
             * if(globalCache.getData() != null) {
             * RegistrationConfirmationDetails
             * cachedData=(RegistrationConfirmationDetails
             * )globalCache.getData().get(data[0]); if(cachedData!=null) {
             * getDataFromAsync(cachedData); } else
             */
            {
                // new ForgotUserIDAsyncTask(this).execute(data);
                callForgotUserID(data);
            }
            // }

        }
    }

    /**
     * This method calls the genralised AsyncTask class WsAsyncTask passing in
     * the DataHolder seralizable class which will be used byJackson to convert
     * Json into PoJO objects
     * 
     * @param data
     *            username and password passed as a string array.
     */
    private void callForgotUserID(final String[] data) {
        final WSRequest request = new WSRequest();
        final String authString = NetworkUtility.getAuthorizationString(
                data[0], data[1]);

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("Authorization", authString);
        headers.put("X-Override-UID", "true");

        final String url = NetworkUtility.getWebServiceUrl(this,
                R.string.forgotUserID_url);

        request.setUrl(url);
        request.setHeaderValues(headers);

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(this,
                new RegistrationConfirmationDetails(), "Discover",
                "Authenticating...", this);
        serviceCall.execute(request);
    }

    private void displayOnMainErrorLabel(final String text) {
        mainErrLabel.setText(text);
        Utils.setViewVisible(mainErrLabel);
    }

    /**
     * This method submits the users information to the Card server for
     * verification.
     * 
     * The AsyncCallback handles the success and failure of the call and is
     * responsible for handling and presenting error messages to the user.
     * 
     */
    private void getAccountDetails(final RegistrationConfirmationDetails user) {

        confirmationDetails = user;
        final CardEventListener cardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(final Object data) {
                // TODO Auto-generated method stub
                Globals.setLoggedIn(true);
                final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                        .getInstance(ForgotUserIdActivity.this);
                final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                        .getCookieManagerInstance();
                sessionCookieManagerObj.setCookieValues();
                cardShareDataStoreObj.addToAppCache(ForgotUserIdActivity.this
                        .getString(R.string.account_details), data);
                navigateToConfirmationScreenWithResponseData(confirmationDetails);
                finish();
            }

            @Override
            public void OnError(final Object data) {
                // TODO Auto-generated method stub
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        ForgotUserIdActivity.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);

            }
        };
        Utils.updateAccountDetails(currentContext, cardEventListener,
                "Discover", "Loading...");

    }

    /**
     * Start the next activity after this one is complete.
     * 
     * @param responseData
     */
    private void navigateToConfirmationScreenWithResponseData(
            final RegistrationConfirmationDetails responseData) {
        final Intent confirmationScreen = new Intent(this,
                CardNavigationRootActivity.class);
        confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
        confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
        confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4,
                responseData.acctLast4);

        confirmationScreen.putExtra(IntentExtraKey.SCREEN_TYPE,
                IntentExtraKey.SCREEN_FOROGT_USER);
        TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_CONFIRMATION);

        this.startActivity(confirmationScreen);
    }

    private void displayModal(final int titleText, final int bodyText,
            final boolean finishActivityOnClose) {
        modalBodyText = bodyText;
        modalTitleText = titleText;
        modalClosesActivity = finishActivityOnClose;

        showErrorModal(titleText, bodyText, finishActivityOnClose);
    }

    @Override
    public void goBack() {
        // Defect id 97237
        final Intent forgotCredentialsActivity = new Intent(this,
                ForgotCredentialsActivity.class);
        startActivity(forgotCredentialsActivity);
        // Defect id 97237
        finish();
    }

    @Override
    public TextView getErrorLabel() {
        return mainErrLabel;
    }

    @Override
    public List<EditText> getInputFields() {
        return null;
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.common.NotLoggedInRoboActivity#getErrorHandler()
     */

    public void getDataFromAsync(final RegistrationConfirmationDetails user) {
        CardShareDataStore.getInstance(this).addToAppCache(
                cardNumField.getText().toString().replace(" ", ""), user);
        confirmationDetails = user;
        getAccountDetails(confirmationDetails);

    }

    @Override
    public void onSuccess(final Object data) {
        getDataFromAsync((RegistrationConfirmationDetails) data);
    }

    @Override
    public void OnError(final Object data) {

        // Changed for handling SSO USERs
        final CardErrorBean cardErrBean = (CardErrorBean) data;
        if (cardErrBean.getIsSSOUser()) {
            cardErrBean.setFooterStatus("101");
            final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    this);
            cardErrorResHandler.handleCardError(cardErrBean,
                    new CardErrorCallbackListener() {

                        @Override
                        public void onButton2Pressed() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onButton1Pressed() {
                            // Calling Registration Activity
                            final Intent registrationActivity = new Intent(
                                    ForgotUserIdActivity.this,
                                    RegistrationAccountInformationActivity.class);
                            startActivity(registrationActivity);
                            finish();

                        }
                    });

        } else {

            final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    this);
            cardErrorResHandler.handleCardError((CardErrorBean) data);
        }

    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        return CardErrorUIWrapper.getInstance();
    }

    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(ForgotUserIdActivity.this,
                    REFERER);
            //Defect id 95853
        }else if(v.getId() == R.id.privacy_terms)
        {
            FacadeFactory.getBankFacade().navToCardPrivacyTerms();
        }
        //Defect id 95853
    }

}
