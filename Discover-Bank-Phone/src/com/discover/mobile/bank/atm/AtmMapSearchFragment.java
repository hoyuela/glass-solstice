/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

/**
 * Interface to bind fragments that do searching
 * @author jthornton
 *
 */
public interface AtmMapSearchFragment {

	/**
	 * Search on the text
	 * @param text - text to search on
	 */
	void performSearch(final String text);

	/**
	 * 
	 * @return a string representing the current address
	 */
	void startCurrentLocationSearch();
}
