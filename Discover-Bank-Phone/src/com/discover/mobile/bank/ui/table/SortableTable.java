package com.discover.mobile.bank.ui.table;

/**
 * Interface for sortable tables
 * @author jthornton
 *
 */
public interface SortableTable {

	/**
	 * Sort the list based on the first filter
	 * @param descending - true if it should sort in descending
	 */
	void sortOnFilterOne(final boolean descending);

	/**
	 * Sort the list based on the second filter
	 * @param descending - true if it should sort in descending
	 */
	void sortOnFilterTwo(final boolean descending);

	/**
	 * Sort the list based on the third filter
	 * @param descending - true if it should sort in descending
	 */
	void sortOnFilterThree(final boolean descending);
}
