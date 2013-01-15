package com.discover.mobile.common;

/**
 * Used to specify at what level a preference should be store using Globals class. 
 * 
 * @author henryoyuela
 *
 */
public enum PreferenceLevel {

	/** Category code to identify an application level preference **/
	APP_LEVEL_PREF(1),

	/** Category code to identify an user level preference **/
	USER_LEVEL_PREF(2),

	/** Category code to identify an account level preference **/
	ACCOUNT_LEVEL_PREF(4),

	/** Category used to identify all levels **/
	ALL_LEVEL_PREF( APP_LEVEL_PREF.getValue() | (int)USER_LEVEL_PREF.getValue() | (int)ACCOUNT_LEVEL_PREF.getValue());

	/** Holds the value used to identify a Preference Level within the enum**/
	private int value;

	/**
	 * Used to assign a specific value to a Preference level 
	 * 
	 * @param value
	 */
	private PreferenceLevel(int value) {
		this.value = value;
	}

	/**
	 * @return Get the actual int value of the Preference Level
	 */
	public int getValue() {
		return value;
	}
}
