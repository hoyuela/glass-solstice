package com.discover.mobile.common;

import android.app.AlertDialog;

/**
 * An interface that defines method signatures used classes referencing an 
 * Activity class to control it currently active modal. 
 * 
 * @author henryoyuela
 *
 */
public interface AlertDialogParent {
	/**
	 * 
	 * @return Returns a reference to the current dialog being displayed over 
	 * 		   the Activity implementing this method signature.
	 */
	AlertDialog getDialog();
	/**
	 * Starts a progress dialog over the Activity implementing this method signature
	 * and sets it as the active modal.
	 */
	void startProgressDialog(); 
	/**
	 * 
	 * @param dialog Reference to the Dialog to be set as the active modal on the activity
	 */
	void setDialog(final AlertDialog dialog);
	/**
	 * Closes the active modal being displayed over the Activity implementing this method signature.
	 */
	void closeDialog();

}
