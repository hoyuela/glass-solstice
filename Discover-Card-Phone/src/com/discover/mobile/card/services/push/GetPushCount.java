/**
 * 
 */
package com.discover.mobile.card.services.push;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;

/**
 * GetPushCount provides new notification counts from server
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class GetPushCount {

    private final Context context;
    private final CardEventListener listener;

    /**
     * Constructor
     * 
     */
    public GetPushCount(final Context context, final CardEventListener listner) {
        this.context = context;
        listener = listner;
    }

    /**
     * This method prepairs header/request and send data to server
     * 
     * @param tokenValue
     * @param hashedTokenValue
     */
    public void sendRequest(final String vendroId) {

        WSRequest request = new WSRequest();
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.get_push_count);

        request.setUrl(url);
        request.setFrequentCaller(true);
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new GetPushCountBean(), "Discover", null, listener);
        serviceCall.execute(request);
    }

}
