package com.discover.mobile.bank.services.auth;

/**
 * Utility class with a collection of strings that are used for defining the schema used
 * within the Authorization HTTP header in a request or a WWW-Authenticate HTTP header in a response.
 * 
 * @author henryoyuela
 *
 */
public class BankSchema {
	/**
	 * Used for sending a session token to be used as authorization in a request.
	 */
	public static final String BANKBASIC = "BankBasic";
	/**
	 * Denotes that a new token must be obtained from the authorization endpoint
	 */
	public static final String BANKAUTH = "BankAuth";
	/**
	 * Denotes that a Stronger Authentication status of "ALLOW" is required.
	 */
	public static final String BANKSA = "BankSA";
	/**
	 * Denotes that a new bank payload must be obtained from a card endpoint.
	 */
	public static final String CARDAUTH = "CardAuth";
	
	/**
     * This constructor is not supported and throws an UnsupportedOperationException when called.
     * 
     * @throws UnsupportedOperationException Every time this constructor is invoked.
     */
	private BankSchema() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
