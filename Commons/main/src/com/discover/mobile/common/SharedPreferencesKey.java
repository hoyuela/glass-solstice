package com.discover.mobile.common;

/**
 * Class holding the keys to the values put in shared preferences
 * 
 * @author jthornton
 *
 */
public final class SharedPreferencesKey {
	
	/**String representing the file name*/
	public static final String FILE_NAME = "com.discover.mobile.prefs";
	
	/**Key to whether or not the shared preferences should be shown*/
	public static final String SHOW_LOGIN_MODAL = "showLoginModal";

	/**
	 * Constructor that will prevent this class from being instantiated
	 */
	private SharedPreferencesKey() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
