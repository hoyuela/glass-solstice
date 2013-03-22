package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.facade.BankKeepAliveFacade;

public class BankKeepAliveFacadeImpl implements BankKeepAliveFacade {

	@Override
	public void refreshBankSession() {
		BankConductor.executeSessionRefreshCall();		
	}
}
