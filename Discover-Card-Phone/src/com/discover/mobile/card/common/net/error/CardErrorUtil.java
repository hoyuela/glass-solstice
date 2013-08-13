package com.discover.mobile.card.common.net.error;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSResponse;
import com.discover.mobile.card.common.utils.Utils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * �2013 Discover Bank
 * 
 * Main error util class which parse the error response received from the
 * service response object and prepare CardErrorBean class for error title and
 * message
 * 
 * @author CTS
 * 
 * @version 1.0
 */

final public class CardErrorUtil {
    private final Context context;

    private String headerMsg = null;
    private String userIdToken = null;

    private final Resources mResource;

    private static String GENERAL_ERROR_MSG_TAG = "E_0";
    private static String GENERAL_ERROR_TITLE_TAG = "E_T_0";

    /**
     * constructor * @param context
     * 
     * @param response
     */

    public CardErrorUtil(final Context context) {
        this.context = context;
        mResource = context.getResources();

    }

    /**
     * @param response
     *            This method will handle the error based on error response code
     * @return
     */
    public CardErrorBean handleCardErrorforResponse(final WSResponse response) {
        Utils.log("handleCardErrorforResponse",
                "RespCode:" + response.getResponseCode());
        switch (response.getResponseCode()) {
        case 409:
        case CardErrorResponseHandler.INCORRECT_USERID_PASSWORD: // 401:Invalid user id  password
        case CardErrorResponseHandler.INVALID_INPUT: // 500 http status code
        case CardErrorResponseHandler.INLINE_ERROR: // 400: a/c locked
        case CardErrorResponseHandler.USER_ACCOUNT_LOCKED: // 403: a/c locked
            final CardErrorBean cardErrBean1 = getCardErrorBeanwithResponseStatus(response, false);
            if (null == cardErrBean1.getErrorCode()) {
                cardErrBean1.setErrorCode("" + response.getResponseCode());
            }
            return cardErrBean1;
        case CardErrorResponseHandler.SERVICE_UNDER_MAINTENANCE: // 503: maintenance error
            final CardErrorBean cardErrBean2 = getCardErrorBeanwithResponseStatus(response, true);
            if (null == cardErrBean2.getErrorCode()) {
                cardErrBean2.setErrorCode("" + response.getResponseCode());
            }
            return cardErrBean2;
        default: // for other error
            final CardErrorBean cardErrBean3 = getCardErrorBeanwithoutResponseStatus(response);
            if (null == cardErrBean3.getErrorCode()) {
                cardErrBean3.setErrorCode("" + response.getResponseCode());
            }
            return cardErrBean3;
        }

    }

    /**
     * This method is used to prepare CardErrorBean class object for error title
     * and message from service response
     * 
     * @param response
     * @param isHeaderMsg
     *            for response header will be parse or not
     * @return CardErrorBean with error title and message
     */

    private CardErrorBean getCardErrorBeanwithResponseStatus(
            final WSResponse response, final boolean isHeaderMsg) {

        CardErrorBean cardErrBean = null;
        String errorMessage = null;
        String errorTitle = null;
        String footerStatus = null;
        String strResbody = null;

        try {
            strResbody = fromStream(response.getInputStream());
            if (strResbody != null && !strResbody.equalsIgnoreCase("")) {
                Utils.log("fromStream", "inputstring" + strResbody + "len:"
                        + strResbody.length());
                final InputStream is = new ByteArrayInputStream(
                        strResbody.getBytes());

                response.setInputStream(is);
            }

        } catch (final IOException e1) {

            e1.printStackTrace();
        }

        if (response.getInputStream() != null) {
            String errorCode = null;
            CardErrorResponse responseBean = null;
            try {
                if (strResbody.length() != 0) {
                    responseBean = parseResponse(response.getInputStream());

                    // Checking if response contains QID and Question Text for
                    // Strong Authentication
                    String questionId = null;
                    if (null != responseBean.data) {
                        for (int index = 0; index < responseBean.data.size(); index++) {
                            if (responseBean.data.get(index).questionId != null) {
                                questionId = responseBean.data.get(index).questionId;
                                break;
                            }
                        }
                    }

                    // Check if response contains SSO user info
                    boolean isSSOUser = false;

                    if (null != responseBean.data) {
                        for (int index = 0; index < responseBean.data.size(); index++) {
                            if (responseBean.data.get(index).isSSOUser) {
                                isSSOUser = true;
                                break;
                            }
                        }
                    }

                    // If QID and Question text is available retrieve the same
                    if (null != responseBean.data && null != questionId) {
                        return getStrongAuthData(response, responseBean);
                    } else if (null != responseBean.data) {
                        if (isSSOUser) {
                            return getSSOData(response, responseBean);
                        } else {
                            errorCode = getErrorCodewithResponse(response,
                                    responseBean);
                            errorTitle = getTitleforErrorCode(errorCode);
                            footerStatus = getHelpFooterErrorCode(errorCode);
                        }
                    } else {
                        errorCode = getErrorCodewithResponse(response,
                                responseBean);
                        errorTitle = getTitleforErrorCode(errorCode);
                        footerStatus = getHelpFooterErrorCode(errorCode);
                    }

                    if (isHeaderMsg == true && responseBean.status != null) {
                        getHeaderValue(response, "Location");

                        if (null != headerMsg) {
                            cardErrBean = new CardErrorBean(errorTitle,
                                    headerMsg, errorCode, false, footerStatus);
                        } else {
                            errorMessage = getMessageforErrorCode(errorCode);
                            cardErrBean = new CardErrorBean(errorTitle,
                                    errorMessage, errorCode, false,
                                    footerStatus);
                        }
                    } else {
                        errorMessage = getMessageforErrorCode(errorCode);
                        cardErrBean = new CardErrorBean(errorTitle,
                                errorMessage, errorCode, false, footerStatus);
                    }

                } else {
                    errorCode = Integer.toString(response.getResponseCode());
                    footerStatus = getHelpFooterErrorCode(errorCode);
                    errorTitle = getTitleforErrorCode(errorCode);
                    errorMessage = getMessageforErrorCode(errorCode);
                    cardErrBean = new CardErrorBean(errorTitle, errorMessage,
                            errorCode, false, footerStatus);
                }

            } catch (final JsonParseException e) {
            	e.printStackTrace();
            	 cardErrBean = new CardErrorBean(getTitleforErrorCode("0"), getMessageforErrorCode("0"),
                         "0", false, "1");
                //cardErrBean = new CardErrorBean(e.toString(), true);

            } catch (final JsonMappingException e) {
            	e.printStackTrace();
            	 cardErrBean = new CardErrorBean(getTitleforErrorCode("0"), getMessageforErrorCode("0"),
                         "0", false, "1");
                //cardErrBean = new CardErrorBean(e.toString(), true);

            } catch (final IOException e) {
            	e.printStackTrace();
            	 cardErrBean = new CardErrorBean(getTitleforErrorCode("0"), getMessageforErrorCode("0"),
                         "0", false, "1");
               // cardErrBean = new CardErrorBean(e.toString(), true);

            }

        } else {
            response.setResponseCode(100); // setting no network error
            cardErrBean = getCardErrorBeanwithoutResponseStatus(response);
        }
        return cardErrBean;

    }

    /**
     * concatenate and returns the error code from the parse CardErrorResponse
     * class
     * 
     * @param response
     * @param responseBean
     * @return final error code
     */
    private String getErrorCodewithResponse(final WSResponse response,
            final CardErrorResponse responseBean) {
        final StringBuilder errCode = new StringBuilder();

        final String resCode = Integer.toString(response.getResponseCode());
        errCode.append(resCode);
        errCode.append(responseBean.status);

        Utils.log("responseBean", " responseBean status:" + responseBean.status);
        Utils.log("responseBean", " responseBean: msg" + responseBean.message);

        if (responseBean.data != null) {
            if (responseBean.data.get(0).saStatus != null) {
                errCode.append("_");
                errCode.append(responseBean.data.get(0).saStatus);
            } else if (responseBean.data.get(0).status != null) {
                errCode.append("_");
                errCode.append(responseBean.data.get(0).status);
            }

            else if (responseBean.data.get(0).userid != null) {
                errCode.append("_WITHUSERID");
                userIdToken = "<br><br><b>Your User ID is:</b>"
                        + responseBean.data.get(0).userid;
            }

        }

        Utils.log("responseBean",
                " responseBean int code:" + errCode.toString());

        return errCode.toString();

    }

    /**
     * This method returns CardErrorBean with Strong Auth specific data
     * 
     * @param response
     * @param responseBean
     * @return CardErrorBean
     */
    private CardErrorBean getStrongAuthData(final WSResponse response,
            final CardErrorResponse responseBean) {
        String questionId = null;
        String questionText = null;
        final StringBuilder errCode = new StringBuilder();

        final String resCode = Integer.toString(response.getResponseCode());
        errCode.append(resCode);
        if (null != responseBean.status) {
            errCode.append(responseBean.status);
        }

        if (responseBean.data != null) {
            for (int index = 0; index < responseBean.data.size(); index++) {
                if (responseBean.data.get(index).questionId != null) {
                    questionId = responseBean.data.get(index).questionId;
                }
                if (responseBean.data.get(index).questionText != null) {
                    questionText = responseBean.data.get(index).questionText;
                }
            }
        }
        return new CardErrorBean(questionId, questionText, errCode.toString());
    }

    /**
     * convert the input stream in to string
     * 
     * @param in
     *            is InputStream
     * @return converted String
     * @throws IOException
     */
    private static String fromStream(final InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                in));
        final StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        return out.toString();
    }

    /**
     * prepare CardErrorBean class for error title and message based on the
     * response error code
     * 
     * @param response
     * @return CardErrorBean with error title and message
     */

    private CardErrorBean getCardErrorBeanwithoutResponseStatus(
            final WSResponse response) {

        String footerStatus = null;
        final String statusCode = Integer.toString(response.getResponseCode());

        footerStatus = getHelpFooterErrorCode(statusCode);
        final String errorMessage = getMessageforErrorCode(statusCode);
        final String errorTitle = getTitleforErrorCode(statusCode);
        final CardErrorBean cardErrBean = new CardErrorBean(errorTitle,
                errorMessage, statusCode, false, footerStatus);

        return cardErrBean;

    }

    /**
     * parsing the input stream through jackson and CardErrorResponse class is
     * holding the parsed response.
     * 
     * @param in
     *            Inputstream containing the json data.
     * @return CardErrorResponse class
     */
    private CardErrorResponse parseResponse(final InputStream in)
            throws JsonParseException, JsonMappingException, IOException {

        final CardErrorResponse carderrRes = JacksonObjectMapperHolder
                .getMapper().readValue(in, CardErrorResponse.class);

        return carderrRes;

    }

    /**
     * get header value from the response based on the header tag
     * 
     * @param response
     * @param headerTag
     *            e.i. Location
     */
    private void getHeaderValue(final WSResponse response,
            final String headerTag) {

        final List<String> headerMsgList = response.getHeaders().get(headerTag);
        if (null != headerMsgList) {
            String[] splitMsg = headerMsgList.get(0).split("//");

            headerMsg = splitMsg[1];
            splitMsg = headerMsg.split("\\|~\\|");

            headerMsg = splitMsg[1];
        }

    }

    /**
     * Get message for respective response code
     * 
     * @param errorResponseCode
     * @return error message
     */
    public String getMessageforErrorCode(final String errorResponseCode) {

        String ErrorMessage = null;

        Utils.log("get msg in", "getMessageforErrorCode" + errorResponseCode);

        String name = appendErrortag("E_", errorResponseCode);
        try {
            final int resId = mResource.getIdentifier(name, "string",
                    context.getPackageName());
            ErrorMessage = mResource.getString(resId);

            Utils.log("get msg out--", "ErrorMessage " + ErrorMessage);

            if (null != userIdToken) {
                ErrorMessage = ErrorMessage.replace("!~~!", userIdToken);
            }
        } catch (final NotFoundException e) {// if the error code not found in
                                             // error
            // xml file
            // then to avoid exception displaying generic
            // error message.
            name = GENERAL_ERROR_MSG_TAG;
            final int resId = mResource.getIdentifier(name, "string",
                    context.getPackageName());
            ErrorMessage = mResource.getString(resId);

        }

        Utils.log("get msg out", "ErrorMessage " + ErrorMessage);

        return ErrorMessage;
    }

    /**
     * Get the title for respective error code
     * 
     * @param errorResponseCode
     * @return title
     */
    public String getTitleforErrorCode(final String errorResponseCode) {

        String ErrorTitle = null;
        Utils.log("get msg in", "getMessageforErrorCode");

        String name = appendErrortag("E_T_", errorResponseCode);

        try {
            final int resId = mResource.getIdentifier(name, "string",
                    context.getPackageName());
            ErrorTitle = mResource.getString(resId);
        } catch (final NotFoundException e) {// if the error code not found in
                                             // error
            // xml file then to avoid exception
            // displaying generic error message.
            name = GENERAL_ERROR_TITLE_TAG;
            final int resId = mResource.getIdentifier(name, "string",
                    context.getPackageName());
            ErrorTitle = mResource.getString(resId);
        }

        Utils.log("get msg out", "ErrorTitle " + ErrorTitle);

        return ErrorTitle;
    }

    /**
     * get help footer
     * 
     * @param errorResponseCode
     * @return
     */
    public String getHelpFooterErrorCode(final String errorResponseCode) {

        String strFooter = null;
        Utils.log("get msg in", "getMessageforErrorCode" + errorResponseCode);

        final String name = appendErrortag("E_H_", errorResponseCode);
        Utils.log("get msg in", "name" + name);

        try {
            final int resId = mResource.getIdentifier(name, "string",
                    context.getPackageName());
            strFooter = mResource.getString(resId);

        } catch (final NotFoundException e) {// if the error code not found in
                                             // error
            // xml file then to avoid exception
            // displaying generic error message.
            strFooter = "1"; // for help footer

            Utils.log("get msg out", "needHelp e " + strFooter);
        }

        Utils.log("get msg out", "needHelp " + strFooter);

        return strFooter;
    }

    /**
     * Append error tag
     * 
     * @param tag
     * @param errorResponseCode
     * @return error with tag append
     */
    private String appendErrortag(final String tag,
            final String errorResponseCode) {

        final StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(errorResponseCode);

        return sb.toString();

    }

    /**
     * 
     * 
     * @param response
     * @param responseBean
     * @return
     */
    private CardErrorBean getSSOData(final WSResponse response,
            final CardErrorResponse responseBean) {
        boolean isSSOUidDLinkable = false;
        boolean isSSOUser = false;
        boolean isSSNMatch = false;
        String footerStatus = null;
        String errTitle = null;
        String errText = null;
        final StringBuilder errCode = new StringBuilder();

        final String resCode = Integer.toString(response.getResponseCode());
        errCode.append(resCode);
        if (null != responseBean.status) {
            errCode.append(responseBean.status);
        }

        if (responseBean.data != null) {
            for (int index = 0; index < responseBean.data.size(); index++) {
                if (responseBean.data.get(index).isSSOUidDLinkable) {
                    isSSOUidDLinkable = responseBean.data.get(index).isSSOUidDLinkable;
                }
                if (responseBean.data.get(index).isSSOUser) {
                    isSSOUser = responseBean.data.get(index).isSSOUser;
                }
                if (responseBean.data.get(index).isSSNMatched) {
                    isSSNMatch = responseBean.data.get(index).isSSNMatched;
                }
            }
        }

        errTitle = getTitleforErrorCode(errCode.toString());
        errText = getMessageforErrorCode(errCode.toString());

        footerStatus = getHelpFooterErrorCode(errCode.toString());

        Utils.log(CardErrorUtil.class.getSimpleName(), "errCode " + errCode
                + " errTitle " + errTitle + " errText " + errText
                + " footerStatus " + footerStatus);
        return new CardErrorBean(isSSOUidDLinkable, isSSOUser, isSSNMatch,
                errCode.toString(), errTitle, errText, footerStatus);
    }
}
