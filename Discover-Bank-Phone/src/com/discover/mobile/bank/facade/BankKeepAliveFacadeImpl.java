package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.common.facade.BankKeepAliveFacade;

/**
 * Facade implementation for services involved with Bank Keep Alive.
 */
public class BankKeepAliveFacadeImpl implements BankKeepAliveFacade {

	@Override
	public void refreshBankSession() {
		BankServiceCallFactory.createRefreshSessionCall().submit();		
	}
}
