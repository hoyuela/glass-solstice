package com.discover.mobile.push.register;

/**
 * This is an interface for any class that is going to register the vendor id with Discvoer's server.
 * So far used in the push what now screen as well as the enroll in push screen.
 * 
 * @author jthornton
 *
 */
public interface PushRegistrationUI {

	/**
	 * Method defined so that the listener knows where to go after it is complete.  Will be used if the user
	 * is trying to enroll in alerts.
	 */
	void changeToAcceptScreen();
	
	/**
	 * Method defined so that the listener knows where to go after it is complete.  Will be used if the user
	 * is trying to NOT enroll in alerts.
	 */
	void changeToDeclineScreen();
}
