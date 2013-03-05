/**
 * copyright solstice 2013
 */
package com.discover.mobile.bank.framework;

import com.discover.mobile.common.framework.Conductor;
import com.discover.mobile.common.framework.ServiceCallFactory;

/**
 * 
 * @author ekaram
 *
 */
public class BankConductor extends Conductor {
	
	
	/**
	 * 
	 * @param bankServiceCallFactory
	 */
	public BankConductor(ServiceCallFactory bankServiceCallFactory ){
		super(bankServiceCallFactory);
		
	}

	
	/* (non-Javadoc)
	 * @see com.discover.mobile.common.framework.Conductor#lookupCacheRequiredForDestination(java.lang.Class)
	 */
	@Override
	public Class lookupCacheRequiredForDestination(Class c) {
		// FIXME
		return null;
	}

}
