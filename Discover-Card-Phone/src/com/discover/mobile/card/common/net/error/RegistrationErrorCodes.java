package com.discover.mobile.card.common.net.error;

public class RegistrationErrorCodes {

   
    public static final int NOT_PRIMARY_CARDHOLDER = 4001914;
    public static final int REG_AUTHENTICATION_PROBLEM = 5001906;
    public static final int REG_AUTHENTICATION_PROBLEM_SECOND = 5031906;
    public static final int BAD_ACCOUNT_STATUS = 4001907;
    public static final int PASS_RESET_FAIL = 5001908;
    public static final int CANT_GET_ACCT_INFO = 5001909;
    public static final int MAX_LOGIN_ATTEMPTS = 4001910;
    public static final int INVALID_EXTERNAL_STATUS = 4001911;
    public static final int ONLINE_STATUS_PROHIBITED = 4001912;
    public static final int INVALID_ONLINE_STATUS = 4001913;
    public static final int REWARDS_OUTAGE = 4001915;
    public static final int FAILED_SECURITY = 4001916;   
    public static final int FINAL_LOGIN_ATTEMPT = 4001917;
    public static final int ACCOUNT_NOT_REGISTERED = 4001918;
    public static final int ID_AND_PASS_EQUAL = 4001919;
    public static final int ID_AND_SSN_EQUAL = 4001920;
    public static final int ID_ALREADY_TAKEN = 4001921;
    public static final int ACCT_NUMBER_REASSIGNMENT = 4031922;
    public static final int ID_IS_EMPTY = 4001923;
    public static final int ID_INVALID = 4001924;
    public static final int PASS_EMPTY = 4001925;
    public static final int PASS_INVALID = 4001926;
    
    private RegistrationErrorCodes() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
