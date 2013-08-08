/**
 * 
 */
package com.discover.mobile.card.services.push;

import java.io.Serializable;

/**
 * POJO Class for PostPushRegistration web service call
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class PostPushRegistrationBean implements Serializable {

    private static final long serialVersionUID = -5911312010908813094L;
    public String vid;
    public String deviceOS;
    public String osVersion;
    public String deviceID;
    public String regStatus;

}
