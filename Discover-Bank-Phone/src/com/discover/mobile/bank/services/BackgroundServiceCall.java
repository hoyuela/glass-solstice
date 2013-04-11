package com.discover.mobile.bank.services;

/**
 * Interface to be consumed by Service Call handlers to determined whether it should show an indication
 * of progress via the UI or not. 
 * 
 * @author henryoyuela
 *
 */
public interface BackgroundServiceCall {
	/**
	 * Method used to set the boolean flag owned by the implementation that specifies
	 * whether progress indication should be shown in the UI.
	 * 
	 * @param value True to run service call silently in background, false otherwise.
	 */
	public void setIsBackgroundCall(boolean value);
	
	/**
	 * Method used to determine whether some indication should displayed in the UI to show
	 * progress.
	 * 
	 * @return Returns true if running in background, false otherwise.
	 */
	public boolean isBackgroundCall();
}
