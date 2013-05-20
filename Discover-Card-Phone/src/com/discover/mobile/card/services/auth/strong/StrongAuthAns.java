/**
 * 
 */
package com.discover.mobile.card.services.auth.strong;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthBean;
import com.discover.mobile.card.auth.strong.StrongAuthUtil;

import com.fasterxml.jackson.core.JsonGenerationException;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class submits answer to server for strong authentication
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthAns {

    private final Context context;
    private final CardEventListener listener;

    /**
     * Constructor
     * 
     * @param context
     * @param listener
     *            CardEventListener
     */
    public StrongAuthAns(final Context context, final CardEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * TODO: Method description
     * 
     * @param answer
     * @param strongAuthQuestionId
     * @param selectedIndex
     * @throws NoSuchAlgorithmException
     * @throws JsonGenerationException
     * @throws IOException
     * @throws Exception
     */
    public void sendRequest(final String answer,
            final String strongAuthQuestionId, final int selectedIndex)
            throws NoSuchAlgorithmException, JsonGenerationException,
            IOException, Exception {
        final StrongAuthAnswerDetails answerDetails = new StrongAuthAnswerDetails();
        answerDetails.questionAnswer = answer;
        answerDetails.questionId = strongAuthQuestionId;

        if (selectedIndex == 0) {
            answerDetails.bindDevice = "true";
        } else {
            answerDetails.bindDevice = "false";
        }

        final StrongAuthUtil authUtil = new StrongAuthUtil(context);
        StrongAuthBean authBean = null;
        authBean = authUtil.getStrongAuthData();
        answerDetails.did = authBean.getDeviceId();
        answerDetails.sid = authBean.getSimId();
        answerDetails.oid = authBean.getSubscriberId();

        final WSRequest request = new WSRequest();
        final HashMap<String, String> headers = request.getHeaderValues();

        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        sessionCookieManagerObj.setCookieValues();

        headers.put("X-Sec-Token", sessionCookieManagerObj.getSecToken());

        final String url = NetworkUtility.getWebServiceUrl(context,
                R.string.strongAuth_ans_url);

        request.setUrl(url);
        request.setHeaderValues(headers);

        request.setMethodtype("POST");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        JacksonObjectMapperHolder.getMapper().writeValue(baos, answerDetails);

        request.setInput(baos.toByteArray());

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
                "Discover", "Authenticating...", listener);
        serviceCall.execute(request);
    }
}
