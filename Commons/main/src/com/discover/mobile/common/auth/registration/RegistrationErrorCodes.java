package com.discover.mobile.common.auth.registration;

public final class RegistrationErrorCodes {
	
	public static final int LOCKED_OUT_ACCOUNT = 1402;
	public static final int SAMS_CLUB_MEMBER = 1905;
	public static final int REG_AUTHENTICATION_PROBLEM = 1906;
	public static final int PASS_RESET_FAIL = 1908;
	public static final int CANT_GET_ACCT_INFO = 1909;
	public static final int CANT_REGISTER_NON_AUTH_BUYER = 1914;
	public static final int FINAL_LOGIN_ATTEMPT = 1917;
	public static final int ID_AND_PASS_EQUAL = 1919;
	public static final int ID_AND_SSN_EQUAL = 1920;
	public static final int ID_ALREADY_TAKEN = 1921;
	public static final int ACCT_NUMBER_REASSIGNMENT = 1922;
	public static final int ID_IS_EMPTY = 1923;
	public static final int ID_INVALID = 1924;
	public static final int PASS_EMPTY = 1925;
	public static final int PASS_INVALID = 1926;
	
	private RegistrationErrorCodes() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}
