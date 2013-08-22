package com.discover.mobile.card.common.sessiontimer;

import android.content.Context;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;

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
        Utils.log(TAG, "inside doInBackground()......");

        final WSRequest request = new WSRequest();
        Utils.isSpinnerAllowed = false;
        final String url = NetworkUtility.getWebServiceUrl(mContext,
                R.string.resettimer_url);

        request.setUrl(url);
        request.setMethodtype("POST");

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(mContext, null,
                null, null, this);
        serviceCall.execute(request);
    }

    @Override
    public void OnError(final Object data) {
        // TODO Auto-generated method stub
        Utils.log(TAG, "Error occured while async update service call");
    }

    @Override
    public void onSuccess(final Object data) {
        // TODO Auto-generated method stub
        Utils.log(TAG, "successfully occured async update service call");
    }

}