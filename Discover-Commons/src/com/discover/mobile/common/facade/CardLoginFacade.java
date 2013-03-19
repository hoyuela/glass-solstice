/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

/**
 * A facade to support common shared logout code
 * 
 * @author ssmith
 * 
 */
public interface CardLoginFacade {

	/**
	 * Submits login call with payload
	 * 
	 * @param callingActivity
	 * @param tokenValue
	 * @param hashedTokenValue
	 */
	public void loginWithPayload(LoginActivityInterface callingActivity,
			String tokenValue, String hashedTokenValue);

	/**
	 * Submits the login call
	 * 
	 * @param callingActivity
	 */
	public void login(LoginActivityInterface callingActivity, String username,
			String password);

}
