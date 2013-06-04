package com.discover.mobile.common.ui.modals;

import android.widget.Button;

/**
* Interface to bind to the bottom views with two buttons that will be placed in an alert modal
* 
* @deprecated use a SimpleTwoButtonModal instead - these modals wrap the current method calls at the moment.
* 
* @author jthornton
*
*/
@Deprecated
public interface ModalBottomTwoButtonView{

	/**
	 * Get the ok or done button
	 * @return the ok or done button
	 */
	public Button getOkButton();
	
	/**
	 * Get the cancel button
	 * @return the cancel button
	 */
	public Button getCancelButton();
	
	/**
	 * Set the text in the ok button
	 * @param resource - resource id to be shown
	 */
	public void setOkButtonText(final int resource);
	
	/**
	 * Set the text in the cancel button
	 * @param resource - resource id to be shown
	 */
	public void setCancelButtonText(final int resource);
	
}

