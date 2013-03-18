/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankLoginServices;
import com.discover.mobile.common.facade.BankLoginFacade;


/**
 * @author ekaram
 *
 */
public class BankLoginFacadeImpl implements BankLoginFacade{

	@Override
	public void authorizeWithBankPayload(String payload) {
		BankLoginServices.authWithBankPayload(payload);
	}

	
	@Override
	public void authDueToALUStatus() {
		BankLoginServices.authDueToALUStatus();
	}
	
}
