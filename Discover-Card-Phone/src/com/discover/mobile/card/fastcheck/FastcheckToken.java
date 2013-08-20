package com.discover.mobile.card.fastcheck;

import java.io.Serializable;

/**
 * 
 * ©2013 Discover Bank
 * 
 * Fastcheck request paramters class
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class FastcheckToken implements Serializable {

    private static final long serialVersionUID = -5560117743257948558L;
    public String deviceToken;
    public boolean getCardImage;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(final String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public FastcheckToken(final String aToken) {
        deviceToken = aToken;
        getCardImage = true;
    }

    public boolean isGetCardImage() {
        return getCardImage;
    }

    public void setGetCardImage(final boolean getCardImage) {
        this.getCardImage = getCardImage;
    }
}
