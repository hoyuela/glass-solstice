package com.discover.mobile.card.login.register;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * This class provides common server calls to the group of classes during
 * registration or forgot credentials that are responsible for logging the user
 * in during the final step.
 * 
 * @author scottseward
 * 
 */
public class ForgotOrRegisterFinalStep extends NotLoggedInRoboActivity
        implements CardErrorHandlerUi, CardEventListener { // DEFECT 96936
    /**
     * The details object that the server will return upon successful flow
     * through forgot or register.
     */
    protected RegistrationConfirmationDetails confirmationDetails;
    protected final Activity currentContext = this;
    protected String screenType;
    protected boolean isAccountUnlock() {
    	return IntentExtraKey.SCREEN_ACCOUNT_UNLOCK.equals(screenType);
    }
    
    protected boolean isForgotBoth() {
    	return IntentExtraKey.SCREEN_FORGOT_BOTH.equals(screenType);
    }

    protected boolean isRegistration() {
    	return IntentExtraKey.SCREEN_REGISTRATION.equals(screenType);
    }

    protected boolean isForgotPassword() {
    	return IntentExtraKey.SCREEN_FORGOT_PASS.equals(screenType);
    }

    // DEFECT 96936
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.log("PageTimeOutUtil.getInstance", "in side CreateLoginActivity");
        PageTimeOutUtil.getInstance(this).startPageTimer();

    }

    // DEFECT 96936
    /**
     * This method submits the users information to the Card server for
     * verification.
     * 
     * The AsyncCallback handles the success and failure of the call and is
     * responsible for handling and presenting error messages to the user.
     * 
     */
    protected void retrieveAccountDetailsFromServer(
            final RegistrationConfirmationDetails registrationConfirmationDetails) {
        confirmationDetails = registrationConfirmationDetails;
        final CardEventListener cardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(final Object data) {
                Globals.setLoggedIn(true);
                final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                        .getInstance(ForgotOrRegisterFinalStep.this);
                final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                        .getCookieManagerInstance();
                sessionCookieManagerObj.setCookieValues();
                cardShareDataStoreObj.addToAppCache(
                        ForgotOrRegisterFinalStep.this
                                .getString(R.string.account_details), data);
                Utils.log("retrieveAccountDetailsFromServer", "clear timer");
                PageTimeOutUtil.getInstance(ForgotOrRegisterFinalStep.this).destroyTimer();  //DEFECT 96936 

                navigateToConfirmationScreenWithResponseData(confirmationDetails);
                finish();
            }

            @Override
            public void OnError(final Object data) {
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        ForgotOrRegisterFinalStep.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }
        };
        Utils.updateAccountDetails(currentContext, cardEventListener,
                "Discover", "Loading...");
    }

    /**
     * If the server call succeeds then we navigate the user to the account home
     * page with a confirmation dialog presented.
     * 
     * @param responseData
     */
    protected void navigateToConfirmationScreenWithResponseData(
            final RegistrationConfirmationDetails responseData) {
    	if (isAccountUnlock()) {
    		TrackingHelper.trackCardPage(AnalyticsPage.ACCOUNT_UNLOCK_CONFIRMATION, null);
    	}
        final Intent confirmationAndLoginScreen = new Intent(this, CardNavigationRootActivity.class);
        confirmationAndLoginScreen.putExtra(IntentExtraKey.UID, responseData.userId);
        confirmationAndLoginScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
        confirmationAndLoginScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);
        confirmationAndLoginScreen.putExtra(IntentExtraKey.SCREEN_TYPE, screenType);
        confirmationAndLoginScreen.putExtra(IntentExtraKey.IS_ACCOUNT_UNLOCK, isAccountUnlock());
        this.startActivity(confirmationAndLoginScreen);
        finish();
    }

    /**
     * Make the help number at the bottom of the screen clickable and when
     * clicked, dial its number.
     */
    protected void setupHelpNumber() {
        final TextView helpText = (TextView) findViewById(R.id.help_number_label);
        helpText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                CommonUtils.dialNumber(helpText.getText().toString(),
                        currentContext);
            }
        });
    }

    /**
     * Sends a user to a modal 'lockout' screen. This terminates the
     * registration process.
     * 
     * @param screenType
     */
    protected void sendToErrorPage(final ScreenType screenType) {
        finish();
    }

    /**
     * Close this activity and start the forgot credentials activity.
     * 
     * @param v
     */
    public void cancel(final View v) {
        goBack();
    }

    @Override
    public void goBack() {
        idealTimeoutLogout();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.common.NotLoggedInRoboActivity#getErrorHandler()
     */
    @Override
    public ErrorHandler getErrorHandler() {
        return CardErrorHandler.getInstance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.discover.mobile.card.error.CardErrorHandlerUi#getCardErrorHandler()
     */
    @Override
    public CardErrHandler getCardErrorHandler() {

        return CardErrorUIWrapper.getInstance();
    }

    @Override
    public TextView getErrorLabel() {

        return null;
    }

    @Override
    public List<EditText> getInputFields() {

        return null;
    }

    // DEFECT 96936
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
        PageTimeOutUtil.getInstance(this).destroyTimer();
        navigateToPreviousActivity();
    }

    @Override
    public void onSuccess(final Object data) {
        PageTimeOutUtil.getInstance(this).destroyTimer();
        navigateToPreviousActivity();
    }
    
    public void navigateToPreviousActivity()
    {
    	Intent lastScreen = null;
        if (isForgotBoth() || isForgotPassword()) {
            lastScreen = new Intent(this, ForgotCredentialsActivity.class);
            startActivity(lastScreen);
        } else if (isAccountUnlock()) {
        	//do nothing special
        } else {
            // FacadeFactory.getLoginFacade().navToLogin(this);
            final Bundle bundle = new Bundle();
            bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE,
                    true);
            bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
            FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);

        }
        finish();
    }
    // DEFECT 96936

}
