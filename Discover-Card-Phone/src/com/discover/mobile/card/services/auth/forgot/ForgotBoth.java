/**
 * 
 */
package com.discover.mobile.card.services.auth.forgot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;

import com.discover.mobile.card.R;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author 228218
 *
 */
public class ForgotBoth {
    private Context context;    
    private CardEventListener listener;
    private AccountInformationDetails accountInformationDetails;
    /**
     * Constructor
     * 
     * @param context
     * @param listener
     *            CardEventListener
     * @param accountInformationDetails
     *            AccountInformationDetails
     */
    public ForgotBoth(Context context, CardEventListener listener,
            AccountInformationDetails accountInformationDetails) {
        super();
        this.context = context;
        this.listener = listener;
        this.accountInformationDetails = accountInformationDetails;
    }
    
    /**
     * This method make a service call for sending account details to server.
     * 
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public void sendRequest() throws JsonGenerationException,
            JsonMappingException, IOException {
        WSRequest request = new WSRequest();

        // Setting the headers available for the service
        HashMap<String, String> headers = request.getHeaderValues();
        headers.put("X-SEC-Token", "");
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.forgotBoth_url);

        request.setUrl(url);
        request.setHeaderValues(headers);
        request.setMethodtype("POST");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        JacksonObjectMapperHolder.getMapper().writeValue(baos,
                accountInformationDetails);

        request.setInput(baos.toByteArray());

        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
                "Discover", "Loading...", listener);
        serviceCall.execute(request);
    }
}
