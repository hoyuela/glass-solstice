package com.discover.mobile.card.statement;

import android.app.Activity;
import android.util.Log;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.passcode.model.json.Status;

/**
 * 
 * GetHealthCheck calls health check URL from statement activity's Image load
 * error condition
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class GetHealthCheck {

    private static String TAG = "GetHealthCheck";

    private String url;
    private Activity activity;

    public GetHealthCheck(final Activity activity, final String baseUrl) {
        this.activity = activity;
        url = baseUrl + "/healthCheck";
    }

    public void loadDataFromNetwork(final CardEventListener cel) {
        Log.v(TAG, "Performing health check");
        WSRequest request = new WSRequest();
        request.setUrl(url);
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(activity,
                new Status(), null, null, cel);
        serviceCall.execute(request);
    }

}
