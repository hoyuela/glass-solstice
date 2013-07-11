package com.discover.mobile.bank.ui.widgets;

import android.content.Context;

public interface CustomProgressDialog {
	void startProgressDialog(boolean isProgressDialogCancelable, Context context);
	
	void stopProgressDialog();
	
	/**returns a boolean to tell bank navigation root and base fragment
	 * activity whether or not a custom or default progress dialog
	 * needs to be shown
	 */
	boolean useCustomDialog();
	
	/*
	 * sets boolean value to determine whether a custom modal
	 * needs to be shown or not.
	 */
	void setShowCustomDialog(boolean show);
}
