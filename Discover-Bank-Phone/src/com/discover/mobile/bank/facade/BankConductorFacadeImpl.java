/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.common.facade.BankConductorFacade;
import com.discover.mobile.common.framework.Conductor;



/**
 * @author ekaram
 *
 */
public class BankConductorFacadeImpl implements BankConductorFacade{

	
	/**
	 * returns the card conductor framework object
	 * 
	 * @return
	 */
	public Conductor getBankConductor(){
		return BankConductor.getInstance();
	}
	
}
