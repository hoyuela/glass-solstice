/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;

import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;


/**
 * A facade for assisting with general card activities 
 * 
 * @author ekaram
 *
 */
public interface CardFacade {

	/**
	 * Navigates to the register activity
	 * @param callingActivity
	 */
	public void navToRegister(BaseActivity callingActivity);
	
	/**
	 * Navigates to the forgot activity
	 * @param callingActivity
	 */
	public void navToForgot(BaseActivity callingActivity);
	
	/**
	 * Launches the card home fragment
	 */
	public void navToHomeFragment(Activity callingActivity);
	
	/**
	 * Returns the card error handler for use by calling activity classes
	 * @return
	 */
	public ErrorHandler getCardErrorHandler();
}
