/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;
import android.content.Context;

import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.ui.CardInfoForToggle;


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

	/**
	 * Returns the url for preauth
	 * @return
	 */
	public String getPreAuthUrl();

	/**
	 * Returns the card base url for preauth
	 * @return
	 */
	public String getPreAuthBaseUrl();

	/** 
	 * Initializes the PhoneGap framework code
	 */
	public void initPhoneGap();

	/**
	 * Returns the card information used to populate the Account Toggle widget.
	 */
	public CardInfoForToggle getCardInfoForToggle(Context context);

}
