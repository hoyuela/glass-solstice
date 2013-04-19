/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.facade.LoginServiceFacade;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.AccountDetails;

/**
 * The implementation class for the card navigation facade
 * 
 * @author CTS
 * 
 */
public class LoginServiceFacadeImpl implements LoginServiceFacade,
        CardEventListener, CardErrorHandlerUi {
    private Context context;
    
    /**
     *  Authenticates the Username and Password
     *  @param callingActivity The Activity reference from where the service is being called
     *  @param username The Username which needs to be authenticated
     *  @param password The password which entered by user for Authentication 
     */
    @Override
    public void login(final LoginActivityInterface callingActivity,
            final String username, final String password) {
        final WSRequest request = new WSRequest();
        final String authString = NetworkUtility.getAuthorizationString(
                username, password);
        context = callingActivity.getContext();

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("Authorization", authString);
        headers.put("X-Override-UID", "true");

        final String url = NetworkUtility.getWebServiceUrl(context,
                R.string.login_url);
        request.setUrl(url);
        request.setHeaderValues(headers);

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new AccountDetails(), "Discover", "Authenticating...", this);
        serviceCall.execute(request);
    }

    /**
     *  Error call back function, gets call while getting any kind Error/exception occurred during service call.
     *  @param data Object of CardErrorBean which contains error details. 
     */
    @Override
    public void OnError(final Object data) {
        // TODO Auto-generated method stub
        CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler((CardErrorHandlerUi)this);
        cardErrorResHandler.handleCardError((CardErrorBean) data);
    }

    /**
     *  Success call back function, gets call with the received data once service call finished successfully
     *  @param data DTO containing the details received from service. 
     */
    @Override
    public void onSuccess(final Object data) {
        Globals.setLoggedIn(true);
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj.getCookieManagerInstance();
        sessionCookieManagerObj.setCookieValues();

        final LoginActivityInterface callingActivity = (LoginActivityInterface) context;

        callingActivity.updateAccountInformation(AccountType.CARD_ACCOUNT);

        CardSessionContext.getCurrentSessionDetails()
                .setNotCurrentUserRegisteredForPush(false);
        CardSessionContext.getCurrentSessionDetails().setAccountDetails(
                (AccountDetails) data);

        cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), (AccountDetails)data);
        final Intent confirmationScreen = new Intent(context,
                CardNavigationRootActivity.class);
        TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

        context.startActivity(confirmationScreen);
        
        // Close current activity
        if(context instanceof Activity)
            ((Activity)context).finish();
    }
    
    /**
     *  returns the TextView object to show in line errors
     */
    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return ((com.discover.mobile.common.error.ErrorHandlerUi)context).getErrorLabel();
        //return null;
    }

    /**
     *  returns the List of all EditText present on screen 
     */
    @Override
    public List<EditText> getInputFields() {
        // TODO Auto-generated method stub
        return ((com.discover.mobile.common.error.ErrorHandlerUi)context).getInputFields();
    }
    
    /**
     *  returns the context
     */
    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return context;
    }

    /**
     *  returns the CardErrHandler object to show error.
     */
    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return CardErrorUIWrapper.getInstance();
    }

    /* (non-Javadoc)
     * @see com.discover.mobile.card.error.CardErrorHandlerUi#showCustomAlert(android.app.AlertDialog)
     */
    @Override
    public void showCustomAlert(AlertDialog alert) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.discover.mobile.card.error.CardErrorHandlerUi#showOneButtonAlert(int, int, int)
     */
    @Override
    public void showOneButtonAlert(int title, int content, int buttonText) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.discover.mobile.card.error.CardErrorHandlerUi#showDynamicOneButtonAlert(int, java.lang.String, int)
     */
    @Override
    public void showDynamicOneButtonAlert(int title, String content,
            int buttonText) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.discover.mobile.card.error.CardErrorHandlerUi#setLastError(int)
     */
    @Override
    public void setLastError(int errorCode) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.discover.mobile.card.error.CardErrorHandlerUi#getLastError()
     */
    @Override
    public int getLastError() {
        // TODO Auto-generated method stub
        return 0;
    }

}
