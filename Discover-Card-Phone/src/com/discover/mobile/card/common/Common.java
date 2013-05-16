package com.discover.mobile.card.common;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * This class will hold common methods which can be used in the application
 * 
 * @author yb
 * 
 */
public class Common {
    long lastRestCallTime;

    public void keepSessionAlive() {
        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // fire KeepSessionAlive if last rest call is 30 seconds ago
        long now = cal1.getTimeInMillis();

        if ((now - lastRestCallTime) / 1000 >= 30) {
            // Call ResetServerTimeOut
        }

    }
}
