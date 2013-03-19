/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

/**
 * A facade to support common shared logout code
 * 
 * @author ssmith
 * 
 */
public interface BankLoginFacade {

	/**
	 * Authorizes a bank login using the Bank Payload returned from Card's
	 * Authentication flow.
	 * 
	 * @param payload
	 */
	public void authorizeWithBankPayload(String payload);

	/**
	 * Authorizes a bank login for users that receive a Bad Card Status, A/L/U.
	 */
	public void authDueToALUStatus();

}
