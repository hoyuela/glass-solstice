package com.discover.mobile.common.ui.modals;

import android.widget.Button;

/**
 * Interface to bind to the bottom views with one button that will be placed in an alert modal
 * 
 * @deprecated use a SimpleContentModal instead - these modals wrap the current method calls at the moment.
 * 
 * @author jthornton
 *
 */
@Deprecated
public interface ModalBottomOneButtonView {

	/**
	 * Get the button
	 * @return the button in the view
	 */
	public Button getButton();
	
	/**
	 * Set the text in the button
	 * @param resource - resource id to be displayed
	 */
	public void setButtonText(final int resource);
}
