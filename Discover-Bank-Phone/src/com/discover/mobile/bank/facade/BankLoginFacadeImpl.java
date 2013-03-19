/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.facade.BankLoginFacade;

/**
 * @author ekaram
 * 
 */
public class BankLoginFacadeImpl implements BankLoginFacade {

	@Override
	public void authorizeWithBankPayload(String payload) {
		BankConductor.authWithBankPayload(payload);
	}

	@Override
	public void authDueToALUStatus() {
		BankConductor.authDueToALUStatus();
	}

}
