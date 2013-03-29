package com.discover.mobile.card.common.sessiontimer;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;

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
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("X-Override-UID", "true");

        final String url = NetworkUtility.getWebServiceUrl(mContext,
                R.string.resettimer_url);

        request.setUrl(url);
        request.setHeaderValues(headers);

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