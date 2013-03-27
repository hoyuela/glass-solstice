package com.discover.mobile.card.common.net.error;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


/**
 * 
 * ©2013 Discover Bank
 *
 * Main error util class which parse the error response received from 
 * the service response object and prepare  CardErrorBean class  for error title and message
 *
 * @author CTS
 *
 * @version 1.0
 */


final public class CardErrorUtil {
    private Context context;
   
    private String headerMsg = null;
    private String userIdToken=null;
    
    
    private final Resources mResource;

    private static String GENERAL_ERROR_MSG_TAG = "E_0";
    private static String GENERAL_ERROR_TITLE_TAG = "E_T_0";
    
   /**
    * constructor    * @param context
    * @param response
    */
    
    public CardErrorUtil(Context context)
    {
        this.context=context;
        mResource = context.getResources();
        
    }
    
    /**
     * @param response
     * This method will handle the error based on error response code
     * @return
     */
    public CardErrorBean handleCardErrorforResponse(WSResponse response)
    {
        Log.d("handleCardErrorforResponse", "RespCode:"+response.getResponseCode());
        switch(response.getResponseCode())
        {
        case CardErrorResponseHandler.INCORRECT_USERID_PASSWORD: //401:Invalid user id password
        case CardErrorResponseHandler.USER_ACCOUNT_LOCKED:       //403:  a/c locked
           
            return getCardErrorBeanwithResponseStatus(response,false);
        case CardErrorResponseHandler.SERVICE_UNDER_MAINTENANCE: //503: maintenance error
          
            getHeaderValue(response,"Location");  
            if(null!=headerMsg)
            {
            return  getCardErrorBeanwithResponseStatus(response,true);
            }
            else
            {
            	 return  getCardErrorBeanwithResponseStatus(response,false);
            }
            
            default: //for other error
                return getCardErrorBeanwithoutResponseStatus(response);
        }
        
               
        
       
        
        
    }
    
    /**
     * This method is used to prepare  CardErrorBean class object for error title and message from service response
     * 
     * @param response
     * @param isHeaderMsg for response header will be parse or not
     * @return CardErrorBean with error title and message
     */
    
    private CardErrorBean getCardErrorBeanwithResponseStatus(WSResponse response,boolean isHeaderMsg)
    {
        
        CardErrorBean cardErrBean = null;
        String errorMessage = null;
        String errorTitle = null;
        String strResbody = null;
        
      /* String body="{\"status\": \"1402\","+
               "\"message\": \"Strongauth status invalid\","+
               "\"data\": ["+
                   "{\"status\": \"LOCKOUT\""+
                   "}"+
               "]"+
           "}";

        
       Log.d("body", " body:"+ body);
       InputStream is = new ByteArrayInputStream(body.getBytes());*/
       
       //response.setInputStream(is);
        
       
       
    try {
        strResbody = fromStream(response.getInputStream());
        if(strResbody!=null)
        Log.d("fromStream", "inputstring"+ strResbody+"len:"+strResbody.length());
        InputStream is = new ByteArrayInputStream(strResbody.getBytes());
        
        response.setInputStream(is);
        
    } catch (IOException e1) {
        
        e1.printStackTrace();
    }
        
   
      
       if(response.getInputStream()!=null)   
       {
           
           String errorCode ;
           
          
           
           try {
               if(strResbody.length()!=0)
               {
                   CardErrorResponse responseBean=  parseResponse(response.getInputStream());
                   
                  errorCode = getErrorCodewithResponse(response,responseBean);
                   
                  
                   
                   errorTitle = getTitleforErrorCode(errorCode);
                  // errorCode = Integer.toString(response.getResponseCode());
               }
               else
               {
                   errorCode = Integer.toString(response.getResponseCode());
                  
                   
                   errorTitle = getTitleforErrorCode(errorCode);  
                   
               }
               
               
                
               if(isHeaderMsg)
               {
                   cardErrBean = new CardErrorBean(errorTitle, headerMsg,
                           errorCode, false);
               }
               else
               {
                   errorMessage = getMessageforErrorCode(errorCode);
                   cardErrBean = new CardErrorBean(errorTitle, errorMessage,
                           errorCode, false);
               }
               
             
               
               
          
       } catch (final JsonParseException e) {
           cardErrBean = new CardErrorBean(e.toString(),true);
           
          
       } catch (final JsonMappingException e) {
           cardErrBean = new CardErrorBean(e.toString(),true);
           
       } catch (final IOException e) {
           cardErrBean = new CardErrorBean(e.toString(),true);
          
       }
           
       
        
        
       }
       else
       {
           response.setResponseCode(100); //setting no network error
         cardErrBean =getCardErrorBeanwithoutResponseStatus(response);   
       }
       return cardErrBean;
       
       
        
    }
    
    /**
     * concatenate and returns the error code from the parse CardErrorResponse class
     * @param response
     * @param responseBean
     * @return final error code
     */
    private String getErrorCodewithResponse(WSResponse response,CardErrorResponse responseBean)
    {       
        final StringBuilder errCode = new StringBuilder();
       
        String resCode =   Integer.toString(response.getResponseCode()) ;
        errCode.append(resCode);
        errCode.append(responseBean.status);
        
        Log.d("responseBean", " responseBean status:"+ responseBean.status);
        Log.d("responseBean", " responseBean: msg"+ responseBean.message);
       
        if(responseBean.data!=null)
        {
            if(responseBean.data.get(0).saStatus!=null)
            {
                errCode.append("_");
                errCode.append(responseBean.data.get(0).saStatus);
            }
            else if(responseBean.data.get(0).status!=null)
            {
                errCode.append("_");
               errCode.append(responseBean.data.get(0).status);   
            }
            
            else if(responseBean.data.get(0).userid!=null)
            {
            	 errCode.append("_WITHUSERID");
            	 userIdToken="<br/>Your User id is:"+responseBean.data.get(0).userid;
            }
        
        }
        
        Log.d("responseBean", " responseBean int code:"+errCode.toString());
       
        return errCode.toString();
                
       
        
        
    }
    
    /**
     * convert the input stream in to string
     * @param in is InputStream
     * @return converted String
     * @throws IOException
     */
    private static String fromStream(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        return out.toString();
    }
    
    
    
    
    /**
     * prepare  CardErrorBean class  for error title and message based on the response error code
     * @param response
     * @return CardErrorBean with error title and message
     */
    
    private CardErrorBean getCardErrorBeanwithoutResponseStatus(WSResponse response)
    {
        final String statusCode= Integer.toString(response.getResponseCode());
                 
        
        final String errorMessage = getMessageforErrorCode(statusCode);
        final String errorTitle = getTitleforErrorCode(statusCode);
        final CardErrorBean cardErrBean = new CardErrorBean(errorTitle, errorMessage,
                statusCode, false);
        
        return cardErrBean;
        
    }
    /**
     *  parsing the input stream through jackson and CardErrorResponse class is holding the parsed response.
     * @param in Inputstream containing the json data.
     * @return CardErrorResponse class
     */
    private CardErrorResponse parseResponse(final InputStream in)throws JsonParseException,JsonMappingException,IOException{
       
             
        CardErrorResponse carderrRes = JacksonObjectMapperHolder.getMapper().readValue(in,
                CardErrorResponse.class);
       
        return carderrRes;
        
    }
    
    
    /**
     * get header value from the response based on the header tag 
     * @param response
     * @param headerTag e.i. Location
     */
    private void getHeaderValue(WSResponse response,String headerTag)
    {
        
         
        List<String> headerMsgList  =  response.getHeaders().get(headerTag);
       // Log.d("headerMsgList", " headerMsgList.get(0):"+ headerMsgList.get(0));
        //Log.d("headerMsgList", " headerMsgList.get(1):"+ headerMsgList.get(1));
        
        
       /* String headerMsgList  = "UnScheduled://We're sorry. We are currently updating our system and cannot complete your request at this time. "+
                "We apologize for any inconvenience. Please try again later or, for immediate assistance call."+
                "|~|We're sorry. We are currently updating our system and cannot complete your request at this time."+ 
                "We apologize for any inconvenience. Please try again later or, for immediate assistance call 1-800-347-2683.|~|UnScheduled Maintenance";*/
        
        /*String headerMsgList  ="UnScheduled://We're sorry. We are currently updating our system and cannot complete your request at this time. We apologize for any inconvenience. Please try again later or, for immediate assistance call <a href=\"tel:+18003472683\">1-800-347-2683</a>.|~|We're sorry. We are currently updating our system and cannot complete your request at this time. We apologize for any inconvenience. Please try again later or, for immediate assistance call 1-800-347-2683.|~|UnScheduled Maintenance";
         * */
        
       // Log.d("headerMsgList", " headerMsgList.get(0):"+ headerMsgList.get(0));
        if(null!=headerMsgList)
        {
        String []splitMsg = headerMsgList.get(0).split("//");
        
        
        Log.d("headerMsgList", " splitMsg[0].1:"+ splitMsg[0]);
        
        Log.d("headerMsgList", " splitMsg[1].1:"+ splitMsg[1]);
        
        headerMsg = splitMsg[1];
        splitMsg=headerMsg.split("\\|~\\|");
        
        Log.d("headerMsgList", " splitMsg[0].2:"+ splitMsg[0]);
        Log.d("headerMsgList", " splitMsg[1].2:"+ splitMsg[1]);
       // Log.d("headerMsgList", " splitMsg[2].2:"+ splitMsg[2]);
        
        headerMsg= splitMsg[1];
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

        Log.d("get msg in", "getMessageforErrorCode");

        String name = appendErrortag("E_", errorResponseCode);
        try {
            final int resId = mResource.getIdentifier(name, "string",
                    context.getPackageName());
            ErrorMessage = mResource.getString(resId);
            if(null!=userIdToken)
            {
            	ErrorMessage=ErrorMessage.replace("!~~!", userIdToken);
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

        Log.d("get msg out", "ErrorMessage " + ErrorMessage);

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
        Log.d("get msg in", "getMessageforErrorCode");

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

        Log.d("get msg out", "ErrorTitle " + ErrorTitle);

        return ErrorTitle;
    }

    /**
     * Append error tag
     * 
     * @param tag
     * @param errorResponseCode
     * @return error with tag append
     */
    private String appendErrortag(final String tag, final String errorResponseCode) {

        final StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(errorResponseCode);

        return sb.toString();
        

    }
    

}
