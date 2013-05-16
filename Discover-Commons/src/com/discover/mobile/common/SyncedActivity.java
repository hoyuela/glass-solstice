package com.discover.mobile.common;

/**
 * Interface to be implemented by an Activity to allow threads other than main UI thread to synchronize 
 * with the Activity resume's state.
 * 
 * @author henryoyuela
 *
 */
public interface SyncedActivity {
	public static final int INFINITE = -1;
	
	
	/**
	 * The implementation of this method signature will return true if the activity is in its resumed 
	 * state and in the foreground, false otherwise. The class implementing this method will need to 
	 * ensure to manage a boolean flag that is set to true when onResume is called on the Activity and 
	 * set to false when onPause is called.
	 * 
	 * @return True if the activity is in its resumed state and in the foreground, false otherwise.
	 */
	public boolean isReady();
			
	/**
	 * 	This method will block indefinitely till an activity has resumed. If the activity is 
	 *  already resumed then it will return immediately.
	 *  
	 *  Returns True if activity resumed, falsed otherwise
	 */
	public boolean waitForResume(final int millis);
}
