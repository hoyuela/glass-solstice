/**
 * 
 */
package com.discover.mobile;

/**
 * Constant For Push Notification & related Services
 * 
 * @author 328073
 * 
 */
public class PushConstant {
    /**
     * Constant use for passing notification values and status in between
     * activities
     * 
     * @author CTS
     * 
     * @version 1.0
     * 
     */
    public class extras {
        public static final String PUSH_ERROR_AC_HOME = "pushErroMsg";
        public static final String PUSH_GET_CALL_STATUS = "pushGetCallStatus";
    }

    /**
     * Constant use for push Preference
     * 
     * @author CTS
     * 
     * @version 1.0
     * 
     */
    public class pref {
        public static final String PUSH_SHARED = "PUSH_PREF";
        public static final String PUSH_XID = "xid";
        public static final String PUSH_GCM_MIGRATION = "GCMMigration";
        public static final String PUSH_NAVIGATION = "pushNavigationPath";
        public static final String PUSH_COUNT_TIME_STAMP = "pushCountTimeStamp";
        public static final String PUSH_COUNT = "pushCount";
        public static final String PUSH_IS_SESSION_VALID = "pushIsSessionValid";
        public static final String PUSH_REQUEST_ID = "pushRequestId";
        public static final String PUSH_OTHER_USER_STATUS = "pushOtherUserStatus";
        public static final String PUSH_OFFLINE = "pushOffline";
    }

    /***
     * Constant use for miscellaneous conditions in push alerts
     * 
     * @author CTS
     * 
     * @version 1.0
     * 
     */
    public class misc {
        public static final String PUSH_STATMENT_LANDING = "acstmt";
        public static final String PUSH_PAYMENT = "payment";
        public static final String PUSH_PAY_HISTORY = "payhist";
        public static final String PUSH_ACCOUNT_ACTIVITY = "acact";
        public static final String PUSH_CASH = "cbbrem";
        public static final String PUSH_REDEMPTION = "redeemcbb";
        public static final String PUSH_MILES = "redeemmiles";
    }

}
