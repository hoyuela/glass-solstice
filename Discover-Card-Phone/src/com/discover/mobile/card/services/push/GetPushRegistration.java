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
 * GetPushRegistration performs call to web server Push Registration web service
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class GetPushRegistration {
    private final Context context;
    private final CardEventListener listener;

    /**
     * Constructor
     * 
     */
    public GetPushRegistration(final Context context,
            final CardEventListener listner) {
        this.context = context;
        listener = listner;
    }

    /**
     * This method prepares header/request and send data to server
     * 
     * @param tokenValue
     * @param hashedTokenValue
     */
    public void sendRequest(final String vendroId) {

        WSRequest request = new WSRequest();
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.get_push_registration) + "?vid=" + vendroId;

        request.setUrl(url);
        /*
         * String input = "vid="+vendroId; request.setInput(input.getBytes());
         */
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new GetPushData(), "Discover", null, listener);
        serviceCall.execute(request);
    }
}
