package com.discover.mobile.bank.ui;

/*
 *  This interface is used to disable an entire view.  The purpose is to 
 *  keep user clicks from interrupting currently active service calls.
 *  For example, when a user clicks an element that starts a service call,
 *  there may be a slight delay between the click and the progress dialog being shown.
 *  This can allow the user to click on another element before the progress dialog is shown,
 *  which will cause an interrupt to service call or create a poor user experience. Before the 
 *  service call is initiated, disableInput() should be called and then enableInput() should
 *  be called in the service class completion listener
 */
public interface InputEnablerListener {
	/*
	 * This method should be used to reset all onlick listeners.
	 */
	void enableInput();
	/*
	 * This method should be used to remove all onclick listeners
	 */
	void disableInput();
}
