package com.discover.mobile.common;

import android.app.Activity;

/**
 * ©2013 Discover Bank
 * 
 * TODO: Class description
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class ActivityUtil {

    private static ActivityUtil m_ActivityUtil = null;

    private final String TAG = "ActivityUtil";

    private Activity m_Activity = null;

    private ActivityUtil() {

    }

    public static ActivityUtil getInstance() {
        if (null == m_ActivityUtil) {
            m_ActivityUtil = new ActivityUtil();
        }

        return m_ActivityUtil;
    }

    public void setCurrentActivity(Activity activity) {
        m_Activity = activity;
    }

    public Activity getCurrentActivity() {
        return m_Activity;
    }

}
