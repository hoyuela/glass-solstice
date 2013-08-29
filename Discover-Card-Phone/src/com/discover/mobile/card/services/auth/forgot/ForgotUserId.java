package com.discover.mobile.card.services.auth.forgot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorCallbackListener;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUtil;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.auth.strong.StrongAuthEnterInfoActivity;
import com.discover.mobile.card.auth.strong.StrongAuthUtil;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.login.register.ForgotUserIdActivity;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class makes a service call for Forgot Password feature
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class ForgotUserId {

    private Context context;

    /**
     * Constructor
     * 
     * @param context
     * @param listener
     *            CardEventListener
     * @param accountInformationDetails
     *            AccountInformationDetails
     */
    public ForgotUserId(final Context context) {
        this.context = context;
    }

    /**
     * This method make a service call for sending account details to server.
     * 
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    
    CardEventListener forgotCardEventListener = new CardEventListener() {
        
        @Override
        public void onSuccess(Object data) {
            if(context instanceof ForgotUserIdActivity){
                ((ForgotUserIdActivity) context).getDataFromAsync((RegistrationConfirmationDetails)(data));
            }else if(context instanceof StrongAuthEnterInfoActivity){
                ((StrongAuthEnterInfoActivity) context).successModal((RegistrationConfirmationDetails)(data));
            }
        }
        
        @Override
        public void OnError(Object data) {
         // Changed for handling SSO USERs
            final CardErrorBean cardErrBean = (CardErrorBean) data;
            // 13.4 Defect 105407 Fix Start
            CardShareDataStore cardShareDataStore = CardShareDataStore
                    .getInstance(context);
            String cache = (String) cardShareDataStore
                    .getValueOfAppCache("WWW-Authenticate");
            // 13.4 Defect 105407 Fix End
            if (cardErrBean.getIsSSOUser()) {
                cardErrBean.setFooterStatus("101");
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        (CardErrorHandlerUi) context);
                cardErrorResHandler.handleCardError(cardErrBean,
                        new CardErrorCallbackListener() {

                            @Override
                            public void onButton2Pressed() {

                            }

                            @Override
                            public void onButton1Pressed() {
                                // Calling Registration Activity
                                final Intent registrationActivity = new Intent(
                                        context,
                                        RegistrationAccountInformationActivity.class);
                                context.startActivity(registrationActivity);
                                ((Activity) context).finish();

                            }
                        });
            } else if (cardErrBean.getErrorCode().contains(
                    "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                    && cache != null && cache.contains("createuser")) {
                
                  StrongAuthUtil strongAuthUtil = new  StrongAuthUtil(context);
                 strongAuthUtil.createUser((CardErrorHandlerUi) context);
            } else {

                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler((CardErrorHandlerUi) context);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }

        }
    };
    
    public void sendRequest(final String[] data ) throws JsonGenerationException,
            JsonMappingException, IOException {
        final WSRequest request = new WSRequest();
        final String authString = NetworkUtility.getAuthorizationString(
                data[0], data[1]);

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("Authorization", authString);
        headers.put("X-Override-UID", "true");

        final String url = NetworkUtility.getWebServiceUrl(context,
                R.string.forgotUserID_url);

        request.setUrl(url);
        request.setHeaderValues(headers);

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new RegistrationConfirmationDetails(), "Discover",
                "Loading...", forgotCardEventListener);
        serviceCall.execute(request);
    }
}
