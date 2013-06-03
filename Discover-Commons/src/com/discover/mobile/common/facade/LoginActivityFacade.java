/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.ScreenType;

/**
 * A delegate for login to 
 * 
 * @author ekaram
 *
 */
public interface LoginActivityFacade {
	/**
	 * Navigates to the Login Activity
	 * @param context
	 */
	public void navToLogin(Context context);
	/**
	 * Navigates to the lockout screen 
	 * 
	 * @param context
	 * @param screenType
	 */
	public void navToLockoutScreen(Context context, ScreenType screenType);
	/**
	 * Returns the login activity class for consumption directly
	 * 
	 * @return
	 */
	public Class getLoginActivityClass();
	/**
	 * Returns a new login activity for consumption directly
	 * @return
	 */
	public BaseFragmentActivity getLoginActivity();
	/**
	 * Allows a navigation direct to login, sporting a message in a bundle
	 * @param currentActivity
	 * @param bundle
	 */
	public void navToLoginWithMessage(Activity currentActivity, Bundle bundle);
	
}
