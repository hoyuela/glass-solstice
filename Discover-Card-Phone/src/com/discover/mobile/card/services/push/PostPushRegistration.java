/**
 * 
 */
package com.discover.mobile.card.services.push;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * PostPushRegistration performs Push registration with server
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class PostPushRegistration {

    private final Context context;
    private final CardEventListener listener;

    /**
     * Constructor
     * 
     */
    public PostPushRegistration(final Context context,
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
     * @throws Exception
     */
    public void sendRequest(final String venderId, final String regStatus)
            throws JsonGenerationException, JsonMappingException, IOException,
            Exception {

        WSRequest request = new WSRequest();
        String url = NetworkUtility.getWebServiceUrl(context,
                R.string.get_push_registration);

        request.setUrl(url);
        PostPushRegistrationBean postPushRegistrationBean = new PostPushRegistrationBean();
        postPushRegistrationBean.deviceOS = "Android_"
                + context.getString(R.string.xApplicationVersion);

        postPushRegistrationBean.osVersion = android.os.Build.VERSION.RELEASE;
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        postPushRegistrationBean.deviceID = manager.getDeviceId();
        postPushRegistrationBean.vid = venderId;
        postPushRegistrationBean.regStatus = regStatus;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JacksonObjectMapperHolder.getMapper().writeValue(byteArrayOutputStream,
                postPushRegistrationBean);
        request.setInput(byteArrayOutputStream.toByteArray());
        request.setMethodtype("POST");
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new GetPushData(), "Discover", null, listener);
        serviceCall.execute(request);
    }

}
