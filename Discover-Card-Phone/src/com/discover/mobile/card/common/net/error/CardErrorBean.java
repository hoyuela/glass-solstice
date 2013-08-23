package com.discover.mobile.card.common.net.error;


/**
 * 
 * �2013 Discover Bank
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
    private String footerStatus = null;
    private String questionId = null;
    private String questionText = null;
    private boolean isSSOUidDLinkable = false;
    private boolean isSSOUser = false;
    private boolean isSSNMatched = false;
    private boolean isTempLocked = false;

    // private static CardErrorBean errorHolder;

    /**
     * Constructor
     * 
     * @param errTitle
     * @param errMessage
     * @param errCode
     * @param isAppError
     */
    public CardErrorBean(final String errorTitle, final String errorMessage,
            final String errorCode, final boolean isAppError,
            final String footerStatus,
            final boolean isTempLocked) {

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorTitle = errorTitle;
        this.isAppError = isAppError;
        this.footerStatus = footerStatus;
        this.isTempLocked = isTempLocked;
    }

    public CardErrorBean(final String errorTitle, final String errorMessage,
            final String errorCode, final boolean isAppError,
            final String footerStatus) {
    	this(errorTitle, errorMessage, errorCode, isAppError, footerStatus, false);
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
        errorCode = "-1";
        footerStatus = "0";
    }

    /**
     * Constructor
     * 
     * @param questionId
     * @param questionText
     */
    public CardErrorBean(final String questionId, final String questionText,
            final String errCode) {

        this.questionId = questionId;
        this.questionText = questionText;
        errorCode = errCode;
        footerStatus = "0";
    }

    /**
     * Constructor SSO
     * 
     * @param isSSOUidDLinkable
     * @param isSSOUser
     */
    public CardErrorBean(final boolean isSSOUidDLinkable,
            final boolean isSSOUser, final boolean isSSNMatched,
            final String errCode, final String errTitle,
            final String errMessage, final String footerStatus) {
        this.isSSOUidDLinkable = isSSOUidDLinkable;
        this.isSSOUser = isSSOUser;
        this.isSSNMatched = isSSNMatched;
        errorCode = errCode;
        errorMessage = errMessage;
        errorTitle = errTitle;
        this.footerStatus = footerStatus;
    }

    /**
     * Set error code
     * 
     * 
     */
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
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

    public String getNeedHelpFooter() {
        return footerStatus;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean getIsSSODelinkable() {
        return isSSOUidDLinkable;
    }

    public boolean getIsSSOUser() {
        return isSSOUser;
    }

    public boolean getIsSSNMatched() {
        return isSSNMatched;

    }

    public void setFooterStatus(final String status) {
        footerStatus = status;
    }

	public boolean isTempLocked() {
		return isTempLocked;
	}

	public void setTempLocked(boolean isTempLocked) {
		this.isTempLocked = isTempLocked;
	}
    
}
