/**
 * 
 */
package com.discover.mobile.card.services.push;

import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.services.auth.BankPayload;

/**
 * @author 328073
 *
 */
public class GetPushRegistration
{
    private final Context context;
    private final CardEventListener listener;

    /**
     * Constructor
     * 
     */
    public GetPushRegistration(Context context, CardEventListener listner) {
        this.context = context;
        this.listener = listner;
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
                R.string.get_push_registration)+"?vid="+vendroId;
        
        request.setUrl(url);
        /*  String input = "vid="+vendroId;
        request.setInput(input.getBytes());*/
        Utils.isSpinnerShow =false;
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new GetPushData(), "Discover", "Authenticating...", listener);
        serviceCall.execute(request);
    }
}
