package com.discover.mobile.bank.error;

/**
 * The following enum is meant only for status codes not supported by HttpUrlConnection. Please use HttpUrlConnection error codes
 * for the ones not found here. 
 * 
 * @author henryoyuela
 *
 */
public enum BankHttpStatusCodes {
	HTTP_UNPROCESSABLE_ENTITY(422);
	
	
	/** Holds the value used to identify the status code**/
	private final int value;

	/**
	 * Used to assign a specific value to a Preference level 
	 * 
	 * @param value
	 */
	private BankHttpStatusCodes(final int value) {
		this.value = value;
	}

	/**
	 * @return Get the actual int value of the Preference Level
	 */
	public int getValue() {
		return value;
	}
}
