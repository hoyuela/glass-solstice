package com.discover.mobile.common.facade;

public interface BankKeepAliveFacade {

	/**
	 * Requests a call be made to Bank's session refresh service.
	 */
	public void refreshBankSession();
	
}
