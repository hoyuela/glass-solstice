/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;
import android.content.Context;

import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.nav.NavigationRootActivity;
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
	public void navToRegister(BaseFragmentActivity callingActivity);
	
	public boolean fastcheckTokenExists(NavigationRootActivity callingActivity);
	
	//Defect id 97126
	   /**
     * Navigates to the provide feedback 
     * @param callingActivity
     */
    public void navToProvideFeedback(Activity callingActivity);
  //Defect id 97126
	/**
	 * Navigates to the forgot activity
	 * @param callingActivity
	 */
	public void navToForgot(BaseFragmentActivity callingActivity);

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
	
	 //16/Aug/2013---Observation Fixed
	   /**
     * Navigates to the privacy terms 
     * @param callingActivity
     */
	public void navToPrivacyTerms(Context context);

}
