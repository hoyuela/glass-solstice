/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;


import android.content.Context;

/**
 * A facade to support common shared login code
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
	
	
	/**
	 * Starts the process of retrieving a Bank payload and authorizing the user
	 * against Bank services. The Bank authentication process, upon success,
	 * will start the user at their account landing view.
	 */
	public void toggleLoginToBank(Context context);

	/**
	 * Toggles the user to the Card side of the application which was previous
	 * authenticated.
	 * 
	 * @param context
	 *            a context from the AccountToggleView, if needed.
	 */
	public void toggleToCard(Context context);
	
    /**
     * Authenticates the user using the passcode based authorization scheme and credentials.
     * @param calling activity - activity calling this method
     * @param deviceToken 
     * @param passcode
     */
    public void loginWithPasscode(LoginActivityInterface callingActivity, String deviceToken,
			String passcode);
}
