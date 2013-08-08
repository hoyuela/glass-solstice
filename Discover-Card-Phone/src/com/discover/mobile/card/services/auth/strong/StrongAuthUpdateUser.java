package com.discover.mobile.card.services.auth.strong;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * StrongAuthUpdateUser Updates information to web server via web service call
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthUpdateUser {
    private Context context;

    private CardEventListener listener;

    /**
     * Constructor
     * 
     * @param context
     * @param listener
     *            CardEventListener
     */
    public StrongAuthUpdateUser(final Context context,
            final CardEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Check with server if Strong Authentication is required.
     * 
     */
    public void sendRequest(
            final StrongAuthReviewQueAnsDetails strongAuthReviewQueAnsDetails) {
        WSRequest request = new WSRequest();
        HashMap<String, String> headers = request.getHeaderValues();
        CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
        SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        sessionCookieManagerObj.setCookieValues();

        headers.put("X-Sec-Token", sessionCookieManagerObj.getSecToken());
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.strongAuth_update_user_url);

        request.setUrl(url);
        request.setHeaderValues(headers);
        request.setMethodtype("POST");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            JacksonObjectMapperHolder.getMapper().writeValue(baos,
                    strongAuthReviewQueAnsDetails);
        } catch (JsonGenerationException e) {
        } catch (JsonMappingException e) {
        } catch (IOException e) {
        }

        request.setInput(baos.toByteArray());
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
                "Discover", "Loading...", listener);
        serviceCall.execute(request);
    }
}
