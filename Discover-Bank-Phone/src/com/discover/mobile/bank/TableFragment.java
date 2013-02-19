package com.discover.mobile.bank;

/**
 * Interface that any fragment with a table should implement so that the it 
 * can go to the details screen. 
 * @author jthornton
 *
 */
public interface TableFragment {

	/**
	 * Let the table fragment know that it needs to send itself to the details
	 * screen with the index that was clicked.
	 * @param index - index that was clicked
	 */
	void sendToDetails(final int index);
}