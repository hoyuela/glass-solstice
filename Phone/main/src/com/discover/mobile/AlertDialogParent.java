package com.discover.mobile;

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
	public AlertDialog getDialog();
	/**
	 * Starts a progress dialog over the Activity implementing this method signature
	 * and sets it as the active modal.
	 */
	public void startProgressDialog(); 
	/**
	 * 
	 * @param dialog Reference to the Dialog to be set as the active modal on the activity
	 */
	public void setDialog(final AlertDialog dialog);
	/**
	 * Closes the active modal being displayed over the Activity implementing this method signature.
	 */
	public void closeDialog();

}
