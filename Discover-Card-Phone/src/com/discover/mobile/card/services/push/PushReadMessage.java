/**
 * 
 */
package com.discover.mobile.card.services.push;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * PushReadMessage calls Web service for reading newly push messages
 * 
 * @author CTS
 * 
 * @version 1.0
 * 
 */
public class PushReadMessage {

    private final Context context;
    private final CardEventListener listener;

    /**
     * Constructor
     * 
     */
    public PushReadMessage(final Context context,
            final CardEventListener listner) {
        this.context = context;
        listener = listner;
    }

    /**
     * This method prepares header/request and send data to server
     * 
     * @param tokenValue
     * @param hashedTokenValue
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public void sendRequest(final String requestId)
            throws JsonGenerationException, JsonMappingException, IOException {

        WSRequest request = new WSRequest();
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.push_read_msg);

        request.setUrl(url);

        PushMessageReadBean messageReadBean = new PushMessageReadBean();
        messageReadBean.action = messageReadBean.MARK_READ;
        messageReadBean.reqId = new ArrayList<String>();
        messageReadBean.reqId.add(requestId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JacksonObjectMapperHolder.getMapper().writeValue(byteArrayOutputStream,
                messageReadBean);
        request.setInput(byteArrayOutputStream.toByteArray());
        request.setMethodtype("POST");

        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
                "Discover", "Loading...", listener);
        serviceCall.execute(request);
    }

}
