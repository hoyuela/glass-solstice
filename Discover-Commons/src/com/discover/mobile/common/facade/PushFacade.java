/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;



import com.discover.mobile.common.BaseActivity;

/**
 * A facade for handling customer service, which is a shared component, accessible via the nav bar
 * 
 * @author ekaram
 *
 */
public interface PushFacade {

	/**
	 * Do a GET request to the server to check to see if this vendor id is
	 * registered to this user.
	 * @author jon thornton
	 */
	public void getXtifyRegistrationStatus(BaseActivity callingActivity);
	
	/**
	 * Starts the Xtify SDK using the correct app key and the correct Google Project ID specific to the environment
	 * @param context - application context
	 */
	public void startXtifySDK(BaseActivity callingActivity);
}
