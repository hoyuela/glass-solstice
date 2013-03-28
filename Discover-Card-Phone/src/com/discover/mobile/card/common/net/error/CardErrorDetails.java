package com.discover.mobile.card.common.net.error;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

/**
 * This class is used to get specific error message from erro xml for respective
 * error code
 * 
 * @author cts
 * 
 */
public final class CardErrorDetails {

    static int ErrorResponseCode = 0;

    private final Context mContext;
    private final Resources mResource;

    private static String GENERAL_ERROR_MSG_TAG = "E_0";
    private static String GENERAL_ERROR_TITLE_TAG = "E_T_0";
    // public static final int GENERAL_ERROR = 0;

    private static CardErrorDetails errorHandler = null;

    /**
     * Create a instance CardErrorDetails
     * 
     * @param context
     * @return CardErrorDetails instance
     */
    public static CardErrorDetails getErrorHandler(final Context context) {
        if (null == errorHandler) {
            errorHandler = new CardErrorDetails(context);
        }

        return errorHandler;
    }

    /**
     * Constructor
     * 
     * @param context
     */
    private CardErrorDetails(final Context context) {
        mContext = context;
        mResource = mContext.getResources();
    }

    /**
     * Get message for respective response code
     * 
     * @param errorResponseCode
     * @return error message
     */
    public String getMessageforErrorCode(final int errorResponseCode) {

        String ErrorMessage = null;

        Log.d("get msg in", "getMessageforErrorCode");

        String name = appendErrortag("E_", errorResponseCode);
        try {
            final int resId = mResource.getIdentifier(name, "string",
                    mContext.getPackageName());
            ErrorMessage = mResource.getString(resId);
        } catch (final NotFoundException e) {// if the error code not found in
                                             // error
            // xml file
            // then to avoid exception displaying generic
            // error message.
            name = GENERAL_ERROR_MSG_TAG;
            final int resId = mResource.getIdentifier(name, "string",
                    mContext.getPackageName());
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
    public String getTitleforErrorCode(final int errorResponseCode) {

        String ErrorTitle = null;
        Log.d("get msg in", "getMessageforErrorCode");

        String name = appendErrortag("E_T_", errorResponseCode);

        try {
            final int resId = mResource.getIdentifier(name, "string",
                    mContext.getPackageName());
            ErrorTitle = mResource.getString(resId);
        } catch (final NotFoundException e) {// if the error code not found in
                                             // error
            // xml file then to avoid exception
            // displaying generic error message.
            name = GENERAL_ERROR_TITLE_TAG;
            final int resId = mResource.getIdentifier(name, "string",
                    mContext.getPackageName());
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
    private String appendErrortag(final String tag, final int errorResponseCode) {

        final StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(errorResponseCode);

        return sb.toString();

    }
}
