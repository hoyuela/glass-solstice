/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.common;

import android.app.AlertDialog;


/**
 * Utility class used to keep a reference of the active modal for the application. Base classes such as 
 * BaseActivity, BaseFragmentActivity, and NotLoggedInRoboActivity all call setActiveActivity onResume(). 
 * This class allows to keep a single reference of the modal in focus to be used by all other classes 
 * that may need to interact with
 * the active modal
 * 
 * @author jthornton
 *
 */
public final class DiscoverModalManager {
	/**
	 * Reference to the application's active modal set via setActiveModal()
	 */
	private static AlertDialog mModal;

	/**
	 * Boolean used to let the activity know if a modal needs to be shown on resume
	 */
	private static boolean alertShowing;
	
	private static boolean isProgressDialogCancelable;

	/**
	 * This constructor is not supported and throws an UnsupportedOperationException when called.
	 * 
	 * @throws UnsupportedOperationException Every time this constructor is invoked.
	 */
	private DiscoverModalManager() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}


	/**
	 * 
	 * @return Returns a reference to the active modal set via setActiveModal
	 */
	public static AlertDialog getActiveModal() {
		return mModal;
	}

	/** 
	 * @param modal - Set to the current modal for the application
	 */
	public static void setActiveModal(final AlertDialog modal) {
		mModal = modal;
	}


	/**
	 * @return the alertShowing
	 */
	public static boolean isAlertShowing() {
		return alertShowing;
	}


	/**
	 * @param alertShowing the alertShowing to set
	 */
	public static void setAlertShowing(final boolean alertShowing) {
		DiscoverModalManager.alertShowing = alertShowing;
	}

	/**
	 * Method used to destroy any reference to a modal being held by DiscoverModalManager and
	 * as well as clear the flag indicating that the modal has to be recreated.
	 */
	public static void clearActiveModal() {
		alertShowing = false;
	
		if( hasActiveModal() ) {
			mModal.dismiss();
		}
		
		mModal = null;
	}
	
	/**
	 * Method used to check if there is a Modal being shown to the user.
	 * 
	 * @return True if an active modal is in the foreground, false otherwise.
	 */
	public static boolean hasActiveModal() {
		return (null != DiscoverModalManager.getActiveModal() && DiscoverModalManager.getActiveModal().isShowing());
	}
	
	/**
	 * 
	 * @return returns true if you can cancel the progress dialog
	 */
	public static boolean isProgressDialogCancelable() {
		return isProgressDialogCancelable;
	}
	
	/*
	 * Sets whether you can cancel the progress dialog or not.
	 */
	public static void setProgressDialogCancelable(boolean isProgressDialogCancelable) {
		DiscoverModalManager.isProgressDialogCancelable = isProgressDialogCancelable;
		DiscoverModalManager.getActiveModal().setCancelable(isProgressDialogCancelable);
	}
}
