package com.discover.mobile.common;

public final class StandardErrorCodes {

	public static final int FORCED_UPGRADE_REQUIRED = 1002;
	public static final int SCHEDULED_MAINTENANCE = 1006;
	public static final int UNSCHEDULED_MAINTENANCE = 1007;
	public static final int PLANNED_OUTAGE = 1008;
	public static final int EXCEEDED_LOGIN_ATTEMPTS = 1101;
	public static final int AUTH_BAD_ACCOUNT_STATUS = 1102;
	public static final int LAST_ATTEMPT_WARNING = 1103;
	public static final int ACCOUNT_SETUP_PENDING = 1104;
	public static final int ACCOUNT_NUMBER_REREGISTERED = 1105;
	public static final int ACCOUNT_NUMBER_CHANGED = 1106;
	public static final int STRONG_AUTH_NOT_ENROLLED = 1401;
	public static final int NO_DATA_FOUND = 1633;
	public static final int BAD_ACCOUNT_STATUS = 1907;
	public static final int MAX_LOGIN_ATTEMPTS = 1910;
	public static final int INVALID_EXTERNAL_STATUS = 1911;
	public static final int ONLINE_STATUS_PROHIBITED = 1912;
	public static final int INVALID_ONLINE_STATUS = 1913;
	public static final int REWARDS_OUTAGE = 1915;
	public static final int FAILED_SECURITY = 1916;
	public static final int ACCOUNT_NOT_REGISTERED = 1918;
	
	/**
	 * Received when: The authorization credentials are either missing or incorrect.
	 */
	public static final int UNAUTHORIZED = 401;
	
	/**
	 * Received when: If input parameters entered as incorrect or wrong formatted. 
	 */
	public static final int INVALID_FORMAT = 400;
	
	/**
	 * Received when: The request has been understood but has been refused due to incompatible client version. 
	 * 				  Status is available in response body.
	 * 
	 * Received when: The request has been understood but has been refused due to invalid strong auth status. 
	 * 				  Strong Auth Info is available in the response body.
	 */
	public static final int FORBIDDEN = 403;
	
	/**
	 * Received when: The server/service is experiencing issues that require investigation.
	 */
	public static final int INTERNAL_SERVER_ERROR = 500;
	
	/**
	 * Received when: The request has been understood but has been refused. 
	 * 				  The System in is maintenance mode, maintenance window info present in body.
	 */
	public static final int SERVICE_UNAVAILABLE = 503;
	
	private StandardErrorCodes() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}
