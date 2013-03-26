package com.discover.mobile.common.facade;

public interface CardKeepAliveFacade {

	/**
	 * Requests a call be made to Card's session refresh service.
	 */
	public void refreshCardSession();
	
}
