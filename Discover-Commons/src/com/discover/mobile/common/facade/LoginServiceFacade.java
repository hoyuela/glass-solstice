/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;


/**
 * A delegate for login to 
 * 
 * @author ekaram
 *
 */
public interface LoginServiceFacade {
	
	/**
	 * Submits the login call 
	 * @param callingActivity
	 */
	public void login(LoginActivityInterface callingActivity, String username, String password);
	
}
