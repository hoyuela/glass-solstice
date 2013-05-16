/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * An interface supporting the LoginActivity 
 * @author ekaram
 *
 */
public interface LoginActivityInterface extends ErrorHandlerUi {
	/**
	 * Used to update the globals data stored at login for CARD or BANK and retrieves
	 * user information. Should only be called if logged in otherwise will return false.
	 * 
	 * @param account Specify either Globals.CARD_ACCOUNT or Globals.BANK_ACCOUNT
	 * 
	 * @return Returns true if successful, false otherwise.
	 */
	public boolean updateAccountInformation(final AccountType account);

}
