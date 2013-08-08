/**
 * 
 */
package com.discover.mobile.card.services.push;

import java.io.Serializable;

/**
 * POJO Bean class for PushData Web service call
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class GetPushData implements Serializable {

    private static final long serialVersionUID = 6509965830307419413L;
    public String resultCode;
    public String resultMsg;
    public String remindersEnrollResultsVO;
}
