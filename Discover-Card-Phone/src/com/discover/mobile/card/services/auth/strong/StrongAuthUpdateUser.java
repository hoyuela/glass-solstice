package com.discover.mobile.card.services.auth.strong;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.R;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class StrongAuthUpdateUser {
    private Context context;
    private final String TAG = StrongAuthCreateUser.class.getSimpleName();
    private CardEventListener listener;

    /**
     * Constructor
     * 
     * @param context
     * @param listener
     *            CardEventListener
     */
    public StrongAuthUpdateUser(Context context, CardEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Check with server if Strong Authentication is required.
     * 
     */
    public void sendRequest(StrongAuthReviewQueAnsDetails strongAuthReviewQueAnsDetails ) {
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
            JacksonObjectMapperHolder.getMapper().writeValue(baos, strongAuthReviewQueAnsDetails);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        request.setInput(baos.toByteArray());
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
                "Discover", "Loading...", listener);
        serviceCall.execute(request);
    }
}
