package com.discover.mobile.common.ui.widgets;

/**
 * Interface used to notify other UI components or Widgets when an event occurs within a ValidatedInputField object.
 * 
 * @author henryoyuela
 *
 */
public interface ValidatedInputFieldListener {
	/**
	 * Used to notify other UI component when an error occurs.
	 */
	public void onValidationError();
}
