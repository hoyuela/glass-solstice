package com.discover.mobile.common.net.error.bank;

/**
 * Class contains the error codes that can be found in an error response to a Bank NetworkServiceCall<>
 * 
 * @author henryoyuela
 *
 */
public class BankErrorCodes {

	/**
	 * Error Code used to describe a Planned System Maintenance error response with HTTP Status Code 503
	 */
	public static final String ERROR_MAINTENANCE_PLANNED = "Maintenance.Planned";
	/**
	 * Error Code used to describe a Unplanned System Maintenance error response with HTTP Status Code 503
	 */
	public static final String ERROR_MAINTENANCE_UNPLANNED = "Maintenance.Unplanned";
	/**
	 * Error Code used to describe an Invalid Login error response with HTTP Status Code 401
	 */
	public static final String ERROR_INVALID_LOGIN = "Auth.Login.Failed";
	/**
	 * Error Code used to describe a Last Attempt Invalid Login error response with HTTP Status Code 401
	 */
	public static final String ERROR_LAST_ATTEMPT_LOGIN = "Auth.Login.LastAttempt";
	/**
	 * Error Code used to describe a Login Locked Out error response with HTTP Status Code 403
	 */
	public static final String ERROR_LOGIN_LOCKED = "Auth.Login.Locked";
	/**
	 * Error Code used to describe an Invalid Strong Auth error response with HTTP Status Code 401
	 */
	public static final String ERROR_INVALID_STRONG_AUTH = "Auth.SA.Failed";
	/**
	 * Error Code used to describe a Last Attempt to a Strong Auth error response with HTTP Status Code 401
	 */
	public static final String ERROR_LAST_ATTEMPT_STRONG_AUTH = "Auth.SA.LastAttempt";
	/**
	 * Error Code used to describe a Strong Auth Locked Out error response with HTTP Status Code 403
	 */
	public static final String ERROR_LOCKED_STRONG_AUTH = "Auth.SA.Locked";
	
	/**
	 * Error Code used to describe a Fraud user error response with HTTP Status Code 403
	 */
	public static final String ERROR_FRAUD_USER = "Auth.Login.BadStatus";
	
	/**
	 * Error Code used to describe a No Accounts Found error response with HTTP Status Code 403
	 */
	public static final String ERROR_NO_ACCOUNTS_FOUND = "Auth.Login.NoAccountsFound";
	
	/**
	 * Error Code used to describe a Check Duplicate error with HTTP Status code 422.
	 */
	public static final String ERROR_CHECK_DUPLICATE = "Deposits.Deposit.DuplicateDeposit";
	/**
	 * Error Code used to describe a Check Duplicate error with HTTP Status code 422.
	 */
	public static final String ERROR_CHECK_DUPLICATE_EX = "Deposits.Deposit.ErrorDuplicate";
	/**
	 * Default constructor is not to be used
	 */
	private BankErrorCodes() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
