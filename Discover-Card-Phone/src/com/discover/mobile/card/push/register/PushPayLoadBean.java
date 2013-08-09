/**
 * 
 */
package com.discover.mobile.card.push.register;

import java.io.Serializable;

/**
 * POJO Class for Push Notification request
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class PushPayLoadBean implements Serializable {
    private static final long serialVersionUID = 5238426350627339567L;
    public String reqID;
    public String pageCode;
    public String payload;
}
