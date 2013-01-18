package com.discover.mobile.common.auth.bank;

public class BankErrorCodes {

	//HTTP 503
	public static final String ERROR_MAINTENANCE_PLANNED = "Maintenance.Planned";
	//HTTP 503
	public static final String ERROR_MAINTENANCE_UNPLANNED = "Maintenance.Unplanned";
	//HTTP 401
	public static final String ERROR_INVALID_LOGIN = "Auth.Login.Fail";
	//HTTP 401
	public static final String ERROR_LAST_ATTEMPT_LOGIN = "Auth.Login.LastAttempt";
	//HTTP 401
	public static final String ERROR_LOGIN_LOCKED = "Auth.Login.Locked";
	//HTTP 401
	public static final String ERROR_INVALID_STRONG_AUTH = "Auth.SA.Fail";
	//HTTP 401
	public static final String ERROR_LAST_ATTEMPT_STRONG_AUTH = "Auth.SA.LastAttempt";
	//HTTP 403
	public static final String ERROR_LOCKED_STRONG_AUTH = "Auth.SA.Locked";
	
	private BankErrorCodes() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
