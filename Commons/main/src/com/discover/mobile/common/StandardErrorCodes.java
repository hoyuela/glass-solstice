package com.discover.mobile.common;

public final class StandardErrorCodes {

	public static final int MAINTENANCE_MODE_1 = 1006;
	public static final int MAINTENANCE_MODE_2 = 1007;
	public static final int AUTH_BAD_ACCOUNT_STATUS = 1102;
	public static final int BAD_ACCOUNT_STATUS = 1907;
	public static final int MAX_LOGIN_ATTEMPTS = 1910;
	public static final int INVALID_EXTERNAL_STATUS = 1911;
	public static final int ONLINE_STATUS_PROHIBITED = 1912;
	public static final int INVALID_ONLINE_STATUS = 1913;
	public static final int REWARDS_OUTAGE = 1915;
	public static final int FAILED_SECURITY = 1916;
	public static final int ACCOUNT_NOT_REGISTERED = 1918;
	
	private StandardErrorCodes() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}
