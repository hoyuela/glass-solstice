/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.PushConstant;
import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.auth.strong.StrongAuthListener;
import com.discover.mobile.card.auth.strong.StrongAuthUtil;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorCallbackListener;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.error.CardErrorUtil;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.card.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.card.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.auth.BankPayload;
import com.discover.mobile.card.services.auth.SSOAuthenticate;
import com.discover.mobile.card.services.push.GetPushData;
import com.discover.mobile.card.services.push.GetPushRegistration;
import com.discover.mobile.card.services.push.PostPushRegistration;
import com.discover.mobile.card.whatsnew.WhatsNewActivity;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.facade.CardLoginFacade;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.xtify.sdk.api.XtifySDK;

/**
 * The impl class for the card nav facade
 * 
 * @author ekaram
 * 
 */
public class CardLoginFacadeImpl implements CardLoginFacade, CardEventListener,
        CardErrorHandlerUi {
    private Context context;
    private CardEventListener SSOCardEventListener;
    private StrongAuthListener authListener;
    private final String LOG_TAG = CardLoginFacadeImpl.class.getSimpleName();
    private String bankPayloadText;
    private String userId;
    private boolean uidIsAccountNumber = false;
    private StrongAuthListener listener;
    private boolean showToggleFlag = false;
    private final int SA_LOCKED = 1402;
    private final int NOT_ENROLLED = 1401;
    private final int SSN_NOT_MATCHED = 1111;
    private final int BANK_SSN_NOT_MATCHED = 1112;
    private final int SSO_SSN_MATCHED = 1106;
    private final int SSO_ERROR_FLAG = 1102;
    private final String NOT_ENROLLED_MSG = "NOTENROLLED";
    private final String SA_LOCKED_MSG = "LOCKOUT";
    private String vendorId;

    private WSRequest request;

    @Override
    public void login(final LoginActivityInterface callingActivity,
            final String username, final String password)
    {
        String authString = NetworkUtility.getAuthorizationString(username, password);
        loginHelper(callingActivity, authString, username, password);
    }
    
    /**
     * Authenticates the user using the passcode based authorization scheme and credentials.
     */
    @Override
    public void loginWithPasscode(LoginActivityInterface callingActivity, String deviceToken,
			String passcode){
        String authString = NetworkUtility.getPasscodeAuthorizationString(deviceToken, passcode);
        loginHelper(callingActivity, authString, deviceToken, passcode);
    }
    
    
    /**
     * Authenticates using the given authString.
     * @param callingActivity
     * @param authString
     */
    private void loginHelper(final LoginActivityInterface callingActivity,
            final String authString, String username, String password) {
        request = new WSRequest();
        context = callingActivity.getContext();

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("Authorization", authString);
        showToggleFlag = false;//DEFECT 97099

        final String url = NetworkUtility.getWebServiceUrl(context,
                R.string.login_url);
        request.setUrl(url);
        request.setHeaderValues(headers);
        //TODO sgoff0 - why is this needed?
        request.setUsername(username);
        request.setPassword(password);

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new AccountDetails(), "Discover", null, this);
        Utils.isSpinnerAllowed = true;
        Utils.showSpinner(context, "Discover", "Loading...");
        serviceCall.execute(request);

        listener = new StrongAuthListener() {

            @Override
            public void onStrongAuthSucess(Object data) {
                doCardNormalFlow();
            }

            @Override
            public void onStrongAuthError(Object data) {
                // TODO Auto-generated method stub
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);

            }

            @Override
            public void onStrongAuthCardLock(Object data) {
                // TODO Auto-generated method stub
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            	
            }

            @Override
            public void onStrongAuthSkipped(Object data) 
            {
            	//getAcHome();
                getAcHomewithtoggle();
            }

            @Override
            public void onStrongAuthNotEnrolled(Object data) {
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }
        };
    }
    

    @Override
    public void loginWithPayload(final LoginActivityInterface callingActivity,
            final String tokenValue, final String hashedTokenValue) {
        context = callingActivity.getContext();
        Utils.log(LOG_TAG, " tokenValue " + tokenValue + " hashedTokenValue "
                + hashedTokenValue);

        // Listen SSO service call
        SSOCardEventListener = new CardEventListener() {
            @Override
            public void onSuccess(Object data) {
                // Get the bankpayload and return to bank via facade
                BankPayload bankPayload = (BankPayload) data;
                Utils.log(LOG_TAG, "---payload-- " + bankPayload.payload);
                FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(
                        bankPayload.payload);
            }

            @Override
            public void OnError(Object data) {
                CardErrorBean cardErrorBean = (CardErrorBean) data;
                CardShareDataStore cardShareDataStore = CardShareDataStore
                        .getInstance(context);
                String cache = (String) cardShareDataStore
                        .getValueOfAppCache("WWW-Authenticate");

                Utils.log(LOG_TAG, "--cache--" + cache + " cardErrorBean "
                        + cardErrorBean.getErrorCode());

                // If error code is 401 and cache contains challenge
                // then show strong auth question
                if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                        && cache != null && cache.contains("challenge")) {
                    cardShareDataStore.deleteCacheObject("WWW-Authenticate");

                    // Check if it's required strong authentication. Skip check
                    // for SA
                    StrongAuthHandler authHandler = new StrongAuthHandler(
                            callingActivity.getContext(), authListener, true);
                    authHandler.strongAuth();
                } else if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                        && cache != null && cache.contains("skipped")) {
                    authListener.onStrongAuthSkipped(cardErrorBean);
                } else if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_FORBIDDEN)
                        && cardErrorBean.getErrorCode()
                                .contains("" + SA_LOCKED)
                        & cardErrorBean.getErrorCode().contains(
                                "" + SA_LOCKED_MSG)) {
                    authListener.onStrongAuthCardLock(cardErrorBean);
                } else if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_FORBIDDEN)
                        && cardErrorBean.getErrorCode().contains(
                                "" + NOT_ENROLLED)
                        && cardErrorBean.getErrorCode().contains(
                                "" + NOT_ENROLLED_MSG)) {
                    Utils.log(LOG_TAG, "--NOt Enrolled --");
                    authListener.onStrongAuthNotEnrolled(cardErrorBean);
                }
                // If SSN not Matched
                else if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_FORBIDDEN)
                        && cardErrorBean.getErrorCode().contains(
                                "" + SSN_NOT_MATCHED)) {
                    // SSN does not matched. Show SSN not match model
                    getErrorMatchModelForPayload(false, false, false,
                            cardErrorBean);
                }

                else if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_FORBIDDEN)
                        && (cardErrorBean.getErrorCode().contains(
                                "" + SSO_ERROR_FLAG) || cardErrorBean
                                .getErrorCode().contains("" + SSO_SSN_MATCHED))) {
                    boolean isSSODLinkable = cardErrorBean.getIsSSODelinkable();
                    boolean isSSOUser = cardErrorBean.getIsSSOUser();
                    boolean isSSNMatch = cardErrorBean.getIsSSNMatched();

                    Utils.log(LOG_TAG, "isSSODLinkable " + isSSODLinkable
                            + " isSSOUser " + isSSOUser + " isSSNMatch "
                            + isSSNMatch);

                    // Get error model based on error flags
                    getErrorMatchModelForPayload(isSSOUser, isSSNMatch,
                            isSSODLinkable, cardErrorBean);
                }
                else if (cardErrorBean.getErrorCode().contains(
                        "" + HttpURLConnection.HTTP_FORBIDDEN)
                        && (cardErrorBean.getErrorCode().contains(
                                "" + BANK_SSN_NOT_MATCHED))) 
                {
                    CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            CardLoginFacadeImpl.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data,new CardErrorCallbackListener() {
                        
                        @Override
                        public void onButton2Pressed() {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void onButton1Pressed() {
                            // TODO Auto-generated method stub
                            getAcHomewithtoggle();
                        }
                    });
                    
                }

                else {
                    CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            CardLoginFacadeImpl.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);
                }
            }
        };

        authListener = new StrongAuthListener() {

            @Override
            public void onStrongAuthSucess(Object data) {
                // Calling service for SSO authentication
                getSSOAuthenticationWithoutToken();
            }

            @Override
            public void onStrongAuthError(Object data) {
                // TODO Auto-generated method stub
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }

            @Override
            public void onStrongAuthCardLock(Object data) {
                // TODO Auto-generated method stub
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }

            @Override
            public void onStrongAuthSkipped(Object data) {
            	final Activity activity = DiscoverActivityManager.getActiveActivity();
            	final ModalDefaultTopView top = new ModalDefaultTopView(activity, null);
            	final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(activity, null);
            	final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity, top, bottom);
            	top.setTitle(R.string.E_SA_SKIPPED_TITLE);
            	top.setContent(R.string.E_SA_SKIPPED_CONTENT);
            	top.showErrorIcon(true);
            	top.hideFeedbackView();
            	bottom.setButtonText(R.string.ok);
            	bottom.getButton().setOnClickListener(new OnClickListener(){
            		@Override
            		public void onClick(final View v){
            		    getAcHomewithtoggle();
            			//getAcHome();
            			modal.dismiss();
            		}
            	});
               if(activity instanceof BaseFragmentActivity){
            	   ((BaseFragmentActivity) activity).showCustomAlert(modal);
               }else if(activity instanceof BaseActivity){
            	   ((BaseActivity) activity).showCustomAlert(modal);
               }
            }

            @Override
            public void onStrongAuthNotEnrolled(Object data) {

                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data,
                        new CardErrorCallbackListener() {

                            @Override
                            public void onButton2Pressed() {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onButton1Pressed() {
                                // Go to Big Browser
                                Utils.hideSpinner();
                                Intent browserIntent = new Intent(
                                        Intent.ACTION_VIEW,
                                        //Uri.parse("https://www.discover.com"));
                                        Uri.parse("https://www.discovercard.com/cardmembersvcs/loginlogout/app/ac_main?ICMPGN=MBL_WEB_LP_FTR_FULL_SITE_TXT"));
                                context.startActivity(browserIntent);
                            }
                        });

            }
        };

        SSOAuthenticate authenticate = new SSOAuthenticate(
                callingActivity.getContext(), SSOCardEventListener);
        authenticate.sendRequest(tokenValue, hashedTokenValue);
    }

    @Override
    public void toggleLoginToBank(final Context context) {
        this.context = context;
        CardEventListener cardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(Object data) {
                // Set Payload data
                // Get the bankpayload and return to bank via facade

                BankPayload bankPayload = (BankPayload) data;
                bankPayloadText = bankPayload.payload;
                Utils.log("Payload from Bank.....", "" + bankPayloadText);

                if (bankPayloadText == null) {
                    Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);
                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            CardLoginFacadeImpl.this);
                    CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener() {
                        @Override
                        public void onButton1Pressed() {
                            // Register Button click flow - Register step-1
                            final Intent registrationActivity = new Intent(
                                    context,
                                    RegistrationAccountInformationActivity.class);
                            context.startActivity(registrationActivity);
                        }

                        @Override
                        public void onButton2Pressed() {
                            // Handled automatically
                        }
                    };
                    CardErrorUtil cardErrUtil = new CardErrorUtil(context);
                    final String errorMessage = cardErrUtil
                            .getMessageforErrorCode("40311021");
                    final String errorTitle = cardErrUtil
                            .getTitleforErrorCode("1401_LOCKOUT");
                    CardErrorBean cardErrorBean = new CardErrorBean(errorTitle,
                            errorMessage, "40311023", false, "101");
                    cardErrorResHandler.handleCardError(cardErrorBean,
                            errorClickCallback);

                } else {
                    FacadeFactory.getBankLoginFacade()
                            .authorizeWithBankPayload(bankPayloadText);
                }
            }

            @Override
            public void OnError(Object data) {
                // SSN NOT MATCH HANDLED
                Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);

            }
        };

        // Get payload from server
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
        AccountDetails accountDetails = (AccountDetails) cardShareDataStoreObj
                .getValueOfAppCache(context.getString(R.string.account_details));
        if (accountDetails.isSSNMatched) {
            getBankPayloadFromServer(cardEventListener);
        } else {
            Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);
            CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    CardLoginFacadeImpl.this);
            CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
            CardErrorBean bean = new CardErrorBean(cardErrorUtil
                    .getTitleforErrorCode("4031102_SSN_NOT_MATCH"),
                    cardErrorUtil
                            .getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
                    "4031102", false, "0");
            cardErrorResHandler.handleCardError(bean);
        }

    }

    /**
     * This methods make a GET call and get payload data from server
     * 
     * @param listener
     */
    public void getBankPayloadFromServer(CardEventListener listener) {

        WSRequest request = new WSRequest();
        HashMap<String, String> headers = request.getHeaderValues();
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.sso_authenticate_bank_payload);

        request.setUrl(url);
        request.setHeaderValues(headers);

        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new BankPayload(), "Discover", "Loading...", listener);
        serviceCall.execute(request);

    }

    @Override
    public void toggleToCard(final Context context) {

        Utils.updateAccountDetails(context, new CardEventListener() {

            @Override
            public void onSuccess(Object data) {
                // TODO Auto-generated method stub
                Globals.setLoggedIn(true);
                final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                        .getInstance(context);
                final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                        .getCookieManagerInstance();
                sessionCookieManagerObj.setCookieValues();

                CardSessionContext.getCurrentSessionDetails()
                        .setNotCurrentUserRegisteredForPush(false);
                CardSessionContext.getCurrentSessionDetails()
                        .setAccountDetails((AccountDetails) data);

                cardShareDataStoreObj.addToAppCache(
                        context.getString(R.string.account_details),
                        (AccountDetails) data);
                final Intent confirmationScreen = new Intent(context,
                        CardNavigationRootActivity.class);
                TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

                Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);

                showToggleFlag = true;
                confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
                context.startActivity(confirmationScreen);

                // Close current activity
                if (context instanceof Activity)
                    ((Activity) context).finish();

            }

            @Override
            public void OnError(Object data) {
                // TODO Auto-generated method stub
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);

            }
        }, "Discover", "Loading......");
    }

    @Override
    public void OnError(final Object data) {
        CardErrorBean bean = (CardErrorBean) data;
        boolean ssoUser = false;
        boolean delinkable = false;
        final boolean isSSNMatched = bean.getIsSSNMatched();
        final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                (CardErrorHandlerUi) this);
        String statusCode = bean.getErrorCode();
        Utils.log("status code", "statusCode---" + statusCode);
        delinkable = bean.getIsSSODelinkable();
        ssoUser = bean.getIsSSOUser();

        CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(context);
        String cache = (String) cardShareDataStore
                .getValueOfAppCache("WWW-Authenticate");
        if (statusCode.equalsIgnoreCase("4031102")
                || statusCode.equalsIgnoreCase("4031106")) {
            if (ssoUser && !delinkable) // A/L/U status
            {
                if (isSSNMatched) { // Bank call for Auth
                    if (null != request)
                        FacadeFactory.getBankLoginFacade().authDueToALUStatus(
                                request.getUsername(), request.getPassword());
                } else {
                    // Show SSN not matched modal

                    CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
                    CardErrorBean beanError = new CardErrorBean(
                            cardErrorUtil
                                    .getTitleforErrorCode("4031102_SSN_NOT_MATCH"),
                            cardErrorUtil
                                    .getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
                            "4031102", false, "0");
                    cardErrorResHandler.handleCardError(beanError);
                }

            } else if (ssoUser && delinkable)// ZB status
            {
                CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener() {
                    @Override
                    public void onButton1Pressed() {
                    	Utils.hideSpinner();
                        // Register Button click flow - Big browser link
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                //Uri.parse("https://www.discover.com"));
                                Uri.parse("https://www.discovercard.com/cardmembersvcs/loginlogout/app/ac_main?ICMPGN=MBL_WEB_LP_FTR_FULL_SITE_TXT"));
                        context.startActivity(browserIntent);
                    }

                    @Override
                    public void onButton2Pressed() {
                        // TODO Cancel Button click flow
                    }
                };
                //DEFECT
                CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
				CardErrorBean beanError = new CardErrorBean(
						cardErrorUtil
								.getTitleforErrorCode("4031102_SSO_DELINK"),
						cardErrorUtil
								.getMessageforErrorCode("4031102_SSO_DELINK"),
						"4031102", false, "101");
				cardErrorResHandler.handleCardError(beanError,
						errorClickCallback);
				//DEFECT

            } else {
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }
        }

        // If error code is 401 and cache contains challenge
        // then show strong auth question
        else if (bean.getErrorCode().contains(
                "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                && cache != null && cache.contains("challenge")) {
            cardShareDataStore.deleteCacheObject("WWW-Authenticate");

            // Check if it's required strong authentication. Skip check for SA
            StrongAuthHandler authHandler = new StrongAuthHandler(context,
                    listener, true);
            authHandler.strongAuth();
        } else if (bean.getErrorCode().contains(
                "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                && cache != null && cache.contains("skipped")) {
            listener.onStrongAuthSkipped(bean);
            /*       13.4 Changes Start*/
        }else if (bean.getErrorCode().contains(
                "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                && cache != null && cache.contains("createuser")) {

            StrongAuthUtil strongAuthUtil = new StrongAuthUtil(context);
            strongAuthUtil.createUser(CardLoginFacadeImpl.this);
            /*       13.4 Changes End*/
        } else if (bean.getErrorCode().contains(
                "" + HttpURLConnection.HTTP_FORBIDDEN)
                && bean.getErrorCode().contains("" + SA_LOCKED)
                & bean.getErrorCode().contains("" + SA_LOCKED_MSG)) {
            listener.onStrongAuthCardLock(bean);
        } else if (bean.getErrorCode().contains(
                "" + HttpURLConnection.HTTP_FORBIDDEN)
                && bean.getErrorCode().contains("" + NOT_ENROLLED)
                && bean.getErrorCode().contains("" + NOT_ENROLLED_MSG)) {
            listener.onStrongAuthNotEnrolled(bean);
        } else {
            cardErrorResHandler.handleCardError((CardErrorBean) data);
        }
    }

    @Override
    public void onSuccess(Object data) {
        Globals.setLoggedIn(true);
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        sessionCookieManagerObj.setCookieValues();
        final LoginActivityInterface callingActivity = (LoginActivityInterface) context;
        callingActivity.updateAccountInformation(AccountType.CARD_ACCOUNT);
        CardSessionContext.getCurrentSessionDetails()
                .setNotCurrentUserRegisteredForPush(false);
        CardSessionContext.getCurrentSessionDetails().setAccountDetails(
                (AccountDetails) data);
        cardShareDataStoreObj.addToAppCache(
                context.getString(R.string.account_details), data);

        boolean isSSOUserVar = ((AccountDetails) data).isSSOUser;

        uidIsAccountNumber = Utils.validateUserforSSO(userId);
        if (uidIsAccountNumber && isSSOUserVar) {// Cannot Login with Account no
                                                 // modal

            final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    (CardErrorHandlerUi) this);
            CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener() {
                @Override
                public void onButton1Pressed() {
                    // Register Button click flow - Register step-1
                    final Intent registrationActivity = new Intent(context,
                            RegistrationAccountInformationActivity.class);
                    context.startActivity(registrationActivity);
                }

                @Override
                public void onButton2Pressed() {
                    // Handled automatically
                }
            };
            CardErrorUtil cardErrUtil = new CardErrorUtil(context);
            final String errorMessage = cardErrUtil
                    .getMessageforErrorCode("4031102_SSO_AccountNo");
            final String errorTitle = cardErrUtil
                    .getTitleforErrorCode("1401_LOCKOUT");
            CardErrorBean cardErrorBean = new CardErrorBean(errorTitle,
                    errorMessage, "40311023", false, "101");
            cardErrorResHandler.handleCardError(cardErrorBean,
                    errorClickCallback);

        } else if (!uidIsAccountNumber && isSSOUserVar) { // Strong Auth flow
            CardSessionContext.getCurrentSessionDetails()
                    .setNotCurrentUserRegisteredForPush(false);
            CardSessionContext.getCurrentSessionDetails().setAccountDetails(
                    (AccountDetails) data);
            cardShareDataStoreObj.addToAppCache(
                    context.getString(R.string.account_details),
                    (AccountDetails) data);
            if (shouldShowSSOToggle(data)) {
                showToggleFlag = true;

                // Strong auth need. Done skip checking with server if SA
                // required or not.
//                StrongAuthHandler authHandler = new StrongAuthHandler(context,
//                        listener, false);
//                authHandler.strongAuth();
                doCardNormalFlow();
            }
        } else {
            doCardNormalFlow();
        }

    }

    private String getVID() {

        SharedPreferences pushSharedPrefs = context.getSharedPreferences(
                PushConstant.pref.PUSH_SHARED, // TODO: Push
                Context.MODE_PRIVATE);
        String vid = XtifySDK.getXidKey(context.getApplicationContext()); // pushSharedPrefs.getString(PushConstant.pref.PUSH_XID,"0");

        Utils.log(LOG_TAG, "give me vid -- ? " + vid);
        if (vid != null && !vid.equalsIgnoreCase("")) {
            if (!pushSharedPrefs.getBoolean(
                    PushConstant.pref.PUSH_GCM_MIGRATION, false)) {
                Editor editor = pushSharedPrefs.edit();
                editor.putBoolean(PushConstant.pref.PUSH_GCM_MIGRATION, true);
                editor.commit();
            }
        }
        return vid;
    }

    /**
     * This method will decide whether we need to show SSO toggle button or not
     * 
     * @param acHome
     * @return
     */
    public boolean shouldShowSSOToggle(Object acHome) {
        boolean isSSOUserVar = ((AccountDetails) acHome).isSSOUser;
        String payLoadSSOTextVar = ((AccountDetails) acHome).payLoadSSOText;
        if (isSSOUserVar && payLoadSSOTextVar != null)
            return true;
        else
            return false;
    }

    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return ((com.discover.mobile.common.error.ErrorHandlerUi) context)
                .getErrorLabel();

    }

    @Override
    public List<EditText> getInputFields() {
        // TODO Auto-generated method stub
        return ((com.discover.mobile.common.error.ErrorHandlerUi) context)
                .getInputFields();

    }

    @Override
    public void showCustomAlert(AlertDialog alert) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showOneButtonAlert(int title, int content, int buttonText) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showDynamicOneButtonAlert(int title, String content,
            int buttonText) {
        // TODO Auto-generated method stub

    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return context;
    }

    @Override
    public void setLastError(int errorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getLastError() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return CardErrorUIWrapper.getInstance();

    }

    /**
     * Based on Flag, this method will show error models and nevigate to AC Home
     * 
     * @param isSSOUser
     * @param isSSNMatch
     * @param isSSODLinkable
     * @param cardErrBean
     */
    public void getErrorMatchModelForPayload(boolean isSSOUser,
            final boolean isSSNMatch, boolean isSSODLinkable,
            final CardErrorBean cardErrBean) {
        // ALU status
        if (isSSOUser && !isSSODLinkable) {
            // If SSN is matched redirect to bank
            if (isSSNMatch) {
                FacadeFactory.getBankLoginFacade().authDueToALUStatus();
            }

            // If SSN is not matched then display error and go
            // to login page
            else {
                // SSN not match model
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
                CardErrorBean bean = new CardErrorBean(
                        cardErrorUtil
                                .getTitleforErrorCode("4031102_SSN_NOT_MATCH"),
                        cardErrorUtil
                                .getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
                        cardErrBean.getErrorCode(), false, cardErrBean
                                .getNeedHelpFooter());
                cardErrorResHandler.handleCardError(bean);
            }
        }

        // ZB status
        else if (isSSOUser && isSSODLinkable) {
            // Show alert for ZB status
            cardErrBean.setFooterStatus("101");
            CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    CardLoginFacadeImpl.this);
            cardErrorResHandler.handleCardError(cardErrBean,
                    new CardErrorCallbackListener() {

                        @Override
                        public void onButton2Pressed() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onButton1Pressed() {
                            // Big Broweser
                        	Utils.hideSpinner();
                            Intent browserIntent = new Intent(
                                    Intent.ACTION_VIEW, 
                                   // Uri.parse("https://www.discover.com"));
                                    Uri.parse("https://www.discovercard.com/cardmembersvcs/loginlogout/app/ac_main?ICMPGN=MBL_WEB_LP_FTR_FULL_SITE_TXT"));
                            
                            
                            context.startActivity(browserIntent);
                        }
                    });
        }

        // SSN not matched
        else if (!isSSNMatch) {
            // Show SSN Error model
            CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    CardLoginFacadeImpl.this);
            CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
            CardErrorBean bean = new CardErrorBean(cardErrorUtil
                    .getTitleforErrorCode("4031102_SSN_NOT_MATCH"),
                    cardErrorUtil
                            .getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
                    cardErrBean.getErrorCode(), false, cardErrBean
                            .getNeedHelpFooter());
            cardErrorResHandler.handleCardError(bean,
                    new CardErrorCallbackListener() {

                        @Override
                        public void onButton2Pressed() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onButton1Pressed() {

                            // Go to AC HOME
                            getAcHome();
                        }
                    });
        }
    }

    /**
     * This will call SSO Authentication service without sending Authentication
     * header
     */
    public void getSSOAuthenticationWithoutToken() {
        CardEventListener cardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(Object data) {

                // Get the bankpayload and return to bank via facade
                BankPayload bankPayload = (BankPayload) data;
                Utils.log(LOG_TAG, "---payload-- " + bankPayload.payload);
                FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(
                        bankPayload.payload);
            }

            @Override
            public void OnError(Object data) {
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        CardLoginFacadeImpl.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }
        };

        SSOAuthenticate authenticate = new SSOAuthenticate(context,
                cardEventListener);
        authenticate.sendRequest(null, null);
    }

private int convertStringToInt(String str) {
		return Integer.parseInt(str.replace(".", ""));
	}
    /**
     * Get account information from server and go to AC Home
     */
    public void getAcHomewithtoggle() {
        // Go to AC Home
    	  final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                  .getInstance(context);
          final AccountDetails cardHomedata = (AccountDetails) cardShareDataStoreObj
                  .getValueOfAppCache(context
                          .getString(R.string.account_details));

         if (cardHomedata != null) 
         {
        	  final Intent confirmationScreen = new Intent(context,
                      CardNavigationRootActivity.class);
              TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
              showToggleFlag = true;
              confirmationScreen.putExtra("showToggleFlag", showToggleFlag);

              context.startActivity(confirmationScreen);

              // Close current activity
              if (context instanceof Activity)
                  ((Activity) context).finish();
         } 
         else 
         {
        	  
	        Utils.updateAccountDetails(context, new CardEventListener() {
	
	            @Override
	            public void onSuccess(Object data) {
	                // TODO Auto-generated method stub
	                Globals.setLoggedIn(true);
	                final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
	                        .getInstance(context);
	                final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
	                        .getCookieManagerInstance();
	                sessionCookieManagerObj.setCookieValues();
	
	                final LoginActivityInterface callingActivity = (LoginActivityInterface) context;
	
	                callingActivity
	                        .updateAccountInformation(AccountType.CARD_ACCOUNT);
	
	                CardSessionContext.getCurrentSessionDetails()
	                        .setNotCurrentUserRegisteredForPush(false);
	                CardSessionContext.getCurrentSessionDetails()
	                        .setAccountDetails((AccountDetails) data);
	
	                cardShareDataStoreObj.addToAppCache(
	                        context.getString(R.string.account_details),
	                        (AccountDetails) data);
	                final Intent confirmationScreen = new Intent(context,
	                        CardNavigationRootActivity.class);
	                TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
	                showToggleFlag = true;
	                confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
	                context.startActivity(confirmationScreen);
	
	                // Close current activity
	                if (context instanceof Activity)
	                    ((Activity) context).finish();
	
	            }
	
	            @Override
	            public void OnError(Object data) {
	                // TODO Auto-generated method stub
	                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
	                        CardLoginFacadeImpl.this);
	                cardErrorResHandler.handleCardError((CardErrorBean) data);
	
	            }
	        }, "Discover", "Loading......");
         }
    }
    
    
    /**
     * Get account information from server and go to AC Home
     */
    public void getAcHome() {
        // Go to AC Home
          final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                  .getInstance(context);
          final AccountDetails cardHomedata = (AccountDetails) cardShareDataStoreObj
                  .getValueOfAppCache(context
                          .getString(R.string.account_details));

         if (cardHomedata != null) 
         {
              final Intent confirmationScreen = new Intent(context,
                      CardNavigationRootActivity.class);
              TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

              context.startActivity(confirmationScreen);

              // Close current activity
              if (context instanceof Activity)
                  ((Activity) context).finish();
         } 
         else 
         {
              
            Utils.updateAccountDetails(context, new CardEventListener() {
    
                @Override
                public void onSuccess(Object data) {
                    // TODO Auto-generated method stub
                    Globals.setLoggedIn(true);
                    final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                            .getInstance(context);
                    final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                            .getCookieManagerInstance();
                    sessionCookieManagerObj.setCookieValues();
    
                    final LoginActivityInterface callingActivity = (LoginActivityInterface) context;
    
                    callingActivity
                            .updateAccountInformation(AccountType.CARD_ACCOUNT);
    
                    CardSessionContext.getCurrentSessionDetails()
                            .setNotCurrentUserRegisteredForPush(false);
                    CardSessionContext.getCurrentSessionDetails()
                            .setAccountDetails((AccountDetails) data);
    
                    cardShareDataStoreObj.addToAppCache(
                            context.getString(R.string.account_details),
                            (AccountDetails) data);
                    final Intent confirmationScreen = new Intent(context,
                            CardNavigationRootActivity.class);
                    TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
                    context.startActivity(confirmationScreen);
    
                    // Close current activity
                    if (context instanceof Activity)
                        ((Activity) context).finish();
    
                }
    
                @Override
                public void OnError(Object data) {
                    // TODO Auto-generated method stub
                    CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            CardLoginFacadeImpl.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);
    
                }
            }, "Discover", "Loading......");
         }
    }

    /**
     * This funcaiton will check if vid is present for push or not based on that
     * it will re direct to AC Home page.
     */
    public void doCardNormalFlow() {
        // Card normal flow
/* 13.3 Changes */
		SharedPreferences pushSharedPrefs = context.getSharedPreferences(
				PushConstant.pref.PUSH_SHARED, // TODO: Push
				Context.MODE_PRIVATE);
		final Editor editor = pushSharedPrefs.edit();
		SharedPreferences whatsNewSharedPrefs = context.getSharedPreferences(
				context.getString(R.string.whats_new_sharedpref), // TODO: Push
				Context.MODE_PRIVATE);
		final Editor whatsNewEditor = whatsNewSharedPrefs.edit();
		String appVersion = whatsNewSharedPrefs.getString(
				context.getString(R.string.appVer), null);

		vendorId = getVID();

		int currentAppVersion = convertStringToInt(context
				.getString(R.string.xApplicationVersion));
		Utils.log("APPVERSION CURRENT", "VERSION---" + currentAppVersion);
		Intent intent;
		if (null == appVersion
				|| (null != appVersion && convertStringToInt(appVersion) < currentAppVersion)) {
			whatsNewEditor.putString(context.getString(R.string.appVer),
					context.getString(R.string.xApplicationVersion));
			whatsNewEditor.commit();
			intent = new Intent(context, WhatsNewActivity.class);
		} else {
			intent = new Intent(context, CardNavigationRootActivity.class);

		}
		/* 13.3 Changes */
		final Intent confirmationScreen = intent;
        if (vendorId != null && !vendorId.equalsIgnoreCase(""))
        {
            // Registration
            GetPushRegistration pushRegistration = new GetPushRegistration(context, new CardEventListener() 
            {
	            @Override
	            public void onSuccess(Object data) 
	            {
	                // TODO Auto-generated method stub
	                GetPushData getPushData = (GetPushData) data;
	                Utils.log(LOG_TAG, "---Push status -- "+ getPushData.resultCode);                            
	                if (getPushData.resultCode.equalsIgnoreCase("F")) 
	                {
	                	//Register user with server for push notification
						//If this fails, we dont need to show error.
						try
						{
							registerPush();
						} 
						catch (JsonGenerationException e) 
						{
							e.printStackTrace();
						}
						catch (JsonMappingException e) 
						{
							e.printStackTrace();
						}
						catch (IOException e) 
						{
							e.printStackTrace();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						
	                    confirmationScreen.putExtra(PushConstant.extras.PUSH_GET_CALL_STATUS,true);
	                    editor.putBoolean(PushConstant.pref.PUSH_OTHER_USER_STATUS,false);
	                } 
	                else if (getPushData.resultCode.equalsIgnoreCase("o")) 
	                {
	                    // Setting other user flag to true so that JQM
	                    // can have this flag.
	                    String errorMsgForPush = context.getString(R.string.E_Push_Other_Account);
	                    confirmationScreen.putExtra(PushConstant.extras.PUSH_ERROR_AC_HOME,errorMsgForPush);
	                    confirmationScreen.putExtra(PushConstant.extras.PUSH_GET_CALL_STATUS,false);
	                    editor.putBoolean(PushConstant.pref.PUSH_OTHER_USER_STATUS,true);
	                } 
	                else
	                {
	                    confirmationScreen.putExtra(PushConstant.extras.PUSH_GET_CALL_STATUS,false);
	                    editor.putBoolean(PushConstant.pref.PUSH_OTHER_USER_STATUS,false);
	                }
	                editor.commit();
	                confirmationScreen.putExtra("showToggleFlag",showToggleFlag);
	                TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
	                context.startActivity(confirmationScreen);
	                if (context instanceof Activity)
	                    ((Activity) context).finish();
	            }
	
	            @Override
	            public void OnError(Object data) {
	                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
	                cardErrorResHandler.handleCardError((CardErrorBean) data);
	            }
	        });

            editor.putString(PushConstant.pref.PUSH_XID,
                    XtifySDK.getXidKey(context.getApplicationContext()));
            Utils.log(LOG_TAG, "--1--" + vendorId);
            Utils.log(LOG_TAG,"--2--"+ XtifySDK.getXidKey(context.getApplicationContext()));
            pushRegistration.sendRequest(XtifySDK.getXidKey(context.getApplicationContext()));
        } else if (vendorId == null || vendorId.equalsIgnoreCase("")) {
			/* 13.3 Changes */
			// final Intent confirmationScreen = new Intent(context,
			// CardNavigationRootActivity.class);
			/* 13.3 Changes */
            confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
            TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
            context.startActivity(confirmationScreen);
            if (context instanceof Activity)
                ((Activity) context).finish();
        } else {
			/* 13.3 Changes */
			// final Intent confirmationScreen = new Intent(context,
			// CardNavigationRootActivity.class);
			/* 13.3 Changes */
            confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
            TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
            context.startActivity(confirmationScreen);
            if (context instanceof Activity)
                ((Activity) context).finish();
        }

    }
    
    /**
	 * This method will send sever XID with acceptance status so register user 
	 * with server 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 */
	public void registerPush() throws JsonGenerationException, JsonMappingException, IOException, Exception
	{
		PostPushRegistration postPushRegistration = new PostPushRegistration(context, new CardEventListener()
		{
			
			@Override
			public void onSuccess(Object data)
			{
				GetPushData data2 = (GetPushData) data;
				Utils.log(LOG_TAG, "--Response Data -- "+data2.resultCode);
			}
			
			@Override
			public void OnError(Object data)
			{
				//Do nothing
			}
		});
		
		//Sending server to acceptance status with the XID
		postPushRegistration.sendRequest(XtifySDK.getXidKey(context.getApplicationContext()), "Y");
	}
}