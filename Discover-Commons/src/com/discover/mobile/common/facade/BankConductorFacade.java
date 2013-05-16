/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import com.discover.mobile.common.framework.Conductor;


/**
 * A facade for assisting with general card activities 
 * 
 * @author ekaram
 *
 */
public interface BankConductorFacade {

	
	
	/**
	 * returns the card conductor framework object
	 * 
	 * @return
	 */
	public Conductor getBankConductor();
}
