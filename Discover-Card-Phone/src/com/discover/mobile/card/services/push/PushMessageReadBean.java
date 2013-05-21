/**
 * 
 */
package com.discover.mobile.card.services.push;

import java.io.Serializable;
import java.util.List;

/**
 * @author 328073
 * 
 */
public class PushMessageReadBean implements Serializable {

    /** Unique identifier */
    private static final long serialVersionUID = 8183770766319973175L;

    /** Static string for marking an item read */
    public static final String MARK_READ = "markRead";

    /** Action attribute for the JSon obect */
    public String action;

    /** Ids to mark read */
    public List<String> reqId;
}
