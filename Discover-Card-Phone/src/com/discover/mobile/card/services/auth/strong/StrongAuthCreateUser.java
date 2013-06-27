package com.discover.mobile.card.services.auth.strong;

import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.R;

public class StrongAuthCreateUser {

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
    public StrongAuthCreateUser(Context context, CardEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Check with server if Strong Authentication is required.
     * 
     */
    public void sendRequest() {
        WSRequest request = new WSRequest();
        HashMap<String, String> headers = request.getHeaderValues();
        CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
        SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        sessionCookieManagerObj.setCookieValues();

        headers.put("X-Sec-Token", sessionCookieManagerObj.getSecToken());
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.strongAuth_create_user_url);

        request.setUrl(url);
        request.setHeaderValues(headers);
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, new StrongAuthCreateUserDetails(),
                "Discover", "Loading...", listener);
        serviceCall.execute(request);
    }
}
