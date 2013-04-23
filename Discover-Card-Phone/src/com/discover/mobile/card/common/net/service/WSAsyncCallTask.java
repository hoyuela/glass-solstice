package com.discover.mobile.card.common.net.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorUtil;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class uses AsyncTask from Android API to call services in background and
 * returns the result by calling OnError() of ErrorListener or onSuccess() of
 * SuccessListener.
 * 
 * @author Hemang Kakadia
 */

public class WSAsyncCallTask extends AsyncTask<WSRequest, Integer, Object> {

    /**
     * Context
     */
    private final Context context;

    /**
     * Holder class
     */
    private Serializable dataHolder;
    /**
     * Prpgress Dialog
     */
    protected ProgressDialog fetchingProgressDialog;
    /**
     * Strings for ProgressDialog Title and ProgressDialog Messsage.
     */
    private final String strProgressBarTitle, strProgressBarMsg;

    private CardEventListener callBackListner = null;

    /**
     * Constructor for WSAsyncCallTask
     * 
     * @param context
     *            Activity Context
     * @param dataHolder
     *            class for holding the Java objects parsed by Jackson
     * @param strProgressBarTitle
     *            Title for Progress Bar
     * @param strProgressBarMsg
     *            Message for Progress Bar
     */
    public WSAsyncCallTask(final Context context,
            final Serializable dataHolder, final String strProgressBarTitle,
            final String strProgressBarMsg,
            final CardEventListener callBackListner) {
        this.context = context;
        this.dataHolder = dataHolder;
        this.strProgressBarTitle = strProgressBarTitle;
        this.strProgressBarMsg = strProgressBarMsg;
        this.callBackListner = callBackListner;

    }

    /**
     * 
     * This Async Task method is called just before thread execution and just
     * shows Progress Dialog
     * 
     * @see onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        // ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // int prevOrientation = ((Activity)context).getRequestedOrientation();
        if (((Activity) context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((Activity) context)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (((Activity) context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ((Activity) context)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            ((Activity) context)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

       /* fetchingProgressDialog = Utils.getProgressDialog((Activity)context,
                strProgressBarTitle, strProgressBarMsg);*/
        //In place of getProgressDialog showSpinner has been used 
        
        try
        {
            Utils.showSpinner(context,strProgressBarTitle , strProgressBarMsg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       
        // );
    }

    /**
     * This AsyncTask method performs tasks in Background thread. Here we are
     * invoking the service call through WSProxy class and getting the response
     * as the object of WSResponse class which is holding actual response and
     * response Code.CardError Details class is used to fetch the correspondng
     * error msg from error_code_mapping.xml for the passed response code.In
     * case of error we are passing this Error code and ErrorMessage in
     * CardErrorBean Holder class else we are parsing the response.
     * 
     * @see doInBackground
     */
    @Override
    protected Object doInBackground(final WSRequest... params) {
        CardErrorBean cardErrBean = null;
        WSProxy wsProxy = new WSProxy();

        WSResponse response = null;
        InputStream in = null;
        try {
            response = wsProxy.invoke(context, params[0]);
            Map<String, List<String>> headers = response.getHeaders();
            CardShareDataStore cardShareDataStore = CardShareDataStore
                    .getInstance(context);
            
            if (headers != null) {
                final Set<String> keys = headers.keySet();
                for (final String key : keys) {
                    String strongAuthSvcs = null;
                    String compareKey = "STRONGAUTHSVCS";
                    String compareSecTokenKey = "sectoken";
                    Log.i("Headers========>", "" + key + " " + headers.get(key));

                    // Adding values to application cache for Strong
                    // Authentication
                    if (key != null && key.equalsIgnoreCase("WWW-Authenticate")) {
                        
                        cardShareDataStore.addToAppCache("WWW-Authenticate",
                                headers.get(key).get(0));
                    }
                    
  
                }

            }
            in = response.getInputStream();

            if (null == in) {
                CardErrorUtil cardErrUtil = new CardErrorUtil(context);

                final String ErrorMessage = cardErrUtil
                        .getMessageforErrorCode("100");// for network error
                // message
                final String errorTitle = cardErrUtil
                        .getTitleforErrorCode("100");
                cardErrBean = new CardErrorBean(errorTitle, ErrorMessage,
                        "100", false, "0");
            } else {
                // response.setResponseCode(503);
                int statusCode = response.getResponseCode();
                Log.d("doInBackground", "statusCode: before check" + statusCode);
                if (statusCode < 200 || statusCode > 299) {
                    Log.d("doInBackground", "statusCode:" + statusCode);
                    CardErrorUtil cardErrUtil = new CardErrorUtil(context);

                    cardErrBean = cardErrUtil
                            .handleCardErrorforResponse(response);
                } else {

                    if (null != dataHolder) {
                        parseResponse(in);
                    }
                }
            }

        } catch (final Exception e) {

            cardErrBean = new CardErrorBean(e.toString(), true);

            e.printStackTrace();
        } finally {
            try {
                if (null != response && null != in) {
                    in.close();
                    in = null;
                    response = null;
                }
                if (wsProxy != null) {
                    wsProxy.dispose();
                    wsProxy = null;
                }
            } catch (final IOException e) {
                cardErrBean = new CardErrorBean(e.toString(), true);
            }
        }

        if (null != cardErrBean) {
            return cardErrBean;
        }
        return dataHolder;
    }

    /**
     * This method is called after the thread execution is over .Progress Dialog
     * is dismissed and calls are made to respective error and success handlers
     * implemented in Activity.
     * 
     * @see OnPostExecute
     */
    @Override
    protected void onPostExecute(final Object result) {
        super.onPostExecute(result);
       // fetchingProgressDialog.dismiss();
      //In place of dismiss() hideSpinner() has been used 
        
        try
        {
            Utils.hideSpinner();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
       

        ((Activity) context)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        if (null != result && result.getClass() == CardErrorBean.class) {
            callBackListner.OnError(result);
        } else {
            // Log.d("WSCall", "Caller:" + context.getClass().getName());
            callBackListner.onSuccess(result);
        }
    }

    /**
     * This method is used for parsing the input stream through jackson and
     * dataholder class is holding the parsed response.
     * 
     * @param in
     *            Inputstream containing the json data.
     */
    private void parseResponse(InputStream in) {
        try {

            if (dataHolder.getClass() == AccountDetails.class) {
                final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                        .getInstance(context);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                String strJSON = sb.toString();
                cardShareDataStoreObj.addToAppCache(
                        context.getString(R.string.account_details_for_js),
                        strJSON);
                dataHolder = JacksonObjectMapperHolder.getMapper().readValue(
                        strJSON, dataHolder.getClass());
            } else {
                dataHolder = JacksonObjectMapperHolder.getMapper().readValue(
                        in, dataHolder.getClass());
            }
        } catch (final JsonParseException e) {
            e.printStackTrace();
        } catch (final JsonMappingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
