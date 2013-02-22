package com.discover.mobile.common;

import android.app.Activity;

/**
 * Utility class used to keep a reference of the active activity for the application. Base classes such as 
 * BaseActivity, BaseFragmentActivity, and NotLoggedInRoboActivity all call setActiveActivity onResume(). 
 * This class allows to keep a single reference of the activity in focus to be used by all other classes 
 * that may need to interact with
 * the active activity
 * 
 * @author henryoyuela
 *
 */
public class DiscoverActivityManager {
	/**
	 * Reference to the application's active activity set via setActiveActivity()
	 */
	private static Activity mActivity;
	
	/**
     * This constructor is not supported and throws an UnsupportedOperationException when called.
     * 
     * @throws UnsupportedOperationException Every time this constructor is invoked.
     */
	private DiscoverActivityManager() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
	
	/**
	 * 
	 * @return Returns a reference to the active activity set via setActiveActivity
	 */
	public static Activity getActiveActivity() {
		return mActivity;
	}
	
	/** 
	 * @param activity - Set to the current activity for the application
	 */
	public static void setActiveActivity(final Activity activity) {
		mActivity = activity;
	}
}
