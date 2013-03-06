package com.discover.mobile.common;

import java.util.Observable;
import java.util.Observer;

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
public class DiscoverActivityManager extends Observable {
	/**
	 * Reference to the application's active activity set via setActiveActivity()
	 */
	private static Activity mActivity;
	/**
	 * Holds the class type of the previous activity that was the active activity
	 */
	private Class<?>	mPrevActivityClass;
	
	/**
	 * Singleton instance of DiscoverActivityManager
	 */
	private static final DiscoverActivityManager instance = new DiscoverActivityManager();
	
	private DiscoverActivityManager() {	
	}
		
	/**
	 * 
	 * @return Returns a reference to the active activity set via setActiveActivity
	 */
	public static Activity getActiveActivity() {
		return instance.mActivity;
	}
	
	/** 
	 * @param activity - Set to the current activity for the application
	 */
	public static void setActiveActivity(final Activity activity) {
		/**
		 * Store the class type of the current active activity as the previous active activity,
		 * if it is not null and the new active activity is different from current active activity.
		 */
		if( null != instance.mActivity  && activity.getClass() != instance.mActivity.getClass() ) {
			instance.mPrevActivityClass = mActivity.getClass();
		}
		
		instance.mActivity = activity;	
		
		instance.setChanged();
		
		instance.notifyObservers(activity);
	}
	
	/** 
	 * @return Returns the class type of the previous Active Activity
	 */
	public static Class<?> getPreviousActiveActivity() {
		return instance.mPrevActivityClass;
	}
	
	
	/**
	 * Used to clear the previsou active activity class type stored.
	 */
	public static void clearPreviousActiveActivity() {
		instance.mPrevActivityClass = null;
	}
	
	
	/**
	 *  Adds an observer to the set of observers for this object, provided 
	 *  that it is not the same as some observer already in the set. This
	 *  Observer will be called whenever the active Activity is changed
	 *  via this class using the method setActiveActivity().
	 * 
	 * @param o an observer to be added.
	 */
	public static void addListener(final Observer o ) {
		instance.addObserver(o);
	}
	
	/**
	 *  Deletes an observer from the set of observers of this object.
	 * 
	 * @param o an observer to be Remvoed. 
	 */
	public static void removeListener(final Observer o) {
		instance.deleteObserver(o);
	}
}
