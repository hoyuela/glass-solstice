package com.discover.mobile.card.common.net.error;

/**
 * 
 * ©2013 Discover Bank
 * 
 * bean class to hold error details
 * 
 * @author CTS
 * 
 * @version 1.0
 */

public final class CardErrorBean {
    private String errorCode = null;
    private String errorMessage = null;
    private String errorTitle = null;
    private boolean isAppError = false;

    // private static CardErrorBean errorHolder;

    /**
     * Constructor
     * 
     * @param errTitle
     * @param errMessage
     * @param errCode
     * @param isAppError
     */
    public CardErrorBean(final String errTitle, final String errMessage,
            final String errCode, final boolean isAppError) {

        errorCode = errCode;
        errorMessage = errMessage;
        errorTitle = errTitle;
        this.isAppError = isAppError;
    }

    /**
     * Constructor
     * 
     * @param errMessage
     * @param isAppError
     */
    public CardErrorBean(final String errMessage, final boolean isAppError) {

        errorMessage = errMessage;
        this.isAppError = isAppError;

    }

    /**
     * Get error code
     * 
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get error message
     * 
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get error title
     * 
     * @return error title
     */
    public String getErrorTitle() {
        return errorTitle;
    }

    /**
     * Get app error
     * 
     * @return true if app error found
     */
    public boolean isAppError() {
        return isAppError;
    }

}
