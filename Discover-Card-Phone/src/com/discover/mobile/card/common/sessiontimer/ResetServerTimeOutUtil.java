package com.discover.mobile.card.common.sessiontimer;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;

/*
 * This class will reset the server timer.
 * 
 */
public final class ResetServerTimeOutUtil implements CardEventListener {

    private final Context mContext;

    public ResetServerTimeOutUtil(final Context context) {
        mContext = context;
    }

    private static String TAG = "ResetServerTimeOut";

    /**
     * It will reset the timer on server by calling dummy web service call.
     */
    public void resetServerTimeOut() {
        Log.d(TAG, "inside doInBackground()......");

        final WSRequest request = new WSRequest();

        // Setting the headers available for the service
        /*final HashMap<String, String> headers = request.getHeaderValues();
        
               
        headers.put("X-Client-Platform", Utils.getStringResource(mContext,
                R.string.xClientPlatform));
        headers.put("X-Application-Version", Utils.getStringResource(mContext,
                R.string.xApplicationVersion));
        headers.put("Content-Type", "application/json");
        
        final CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(mContext);
        final SessionCookieManager sessionCookieManager = cardShareDataStore
                .getCookieManagerInstance();
        
        String token = sessionCookieManager.getSecToken();
        Log.d(TAG, "token11:"+token);
        headers.put("X-SEC-Token", token);
       */

        final String url = NetworkUtility.getWebServiceUrl(mContext,
                R.string.resettimer_url);

        request.setUrl(url);
        //request.setHeaderValues(headers);
       request.setMethodtype("POST");

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(mContext, null,
                null, null, this);
        serviceCall.execute(request);
    }

    @Override
    public void OnError(Object data) {
        // TODO Auto-generated method stub
        Log.d(TAG, "Error occured while async update service call");
    }

    @Override
    public void onSuccess(Object data) {
        // TODO Auto-generated method stub
        Log.d(TAG, "successfully occured async update service call");
    }

    /*
     * private boolean checkNetworkConnected() { boolean isNetworkConnected =
     * false; if (!NetworkUtility.isConnected(mContext)) {
     * 
     * isNetworkConnected = false; } else { isNetworkConnected = true; } return
     * isNetworkConnected;
     * 
     * }
     */
}