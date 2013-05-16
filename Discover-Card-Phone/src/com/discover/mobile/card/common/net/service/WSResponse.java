package com.discover.mobile.card.common.net.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This class is web service response model class.
 * 
 * @author Anuja Deshpande
 */

public final class WSResponse {

    /**
     * variable for holding response code
     */
    private int iResponseCode;

    /**
     * variable for holding input stream
     */
    private InputStream inputStream = null;

    /**
     * Variable for holding response headers
     */
    private Map<String, List<String>> mapHeaders;

    /**
     * set the response code
     * 
     * @param responseCode
     *            response code
     */
    public void setResponseCode(final int responseCode) {
        iResponseCode = responseCode;
    }

    /**
     * get the response code
     * 
     * @return response code
     */
    public int getResponseCode() {
        return iResponseCode;
    }

    /**
     * set the input stream
     * 
     * @param inputStream
     *            input stream
     */
    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * get the input stream
     * 
     * @return inout stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * get headers
     * 
     * @return the headers
     */
    public Map<String, List<String>> getHeaders() {
        return mapHeaders;
    }

    /**
     * set headers
     * 
     * @param headers
     *            the headers to set
     */
    public void setHeaders(final Map<String, List<String>> headers) {
        mapHeaders = headers;
    }
}
