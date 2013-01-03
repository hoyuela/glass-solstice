package com.discover.mobile.common;

import com.google.common.base.Strings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class holding the keys to the values put in shared preferences. Each value is either an application level or 
 * user level preference. Application and user level preferences are stored in separate files. Application should
 * call setCurrentUser() in order to generate user level preferences file. SharedPrefenceManagerWrapper will always 
 * use application level preferences file to store values by default, if current user is not specified by application.
 * Setting current user allows to save preferences for multiple users.
 * 
 * @author jthornton, hoyuela
 *
 */
public final class SharedPreferencesWrapper {
	
	/**String representing the file name*/
	private static final String FILE_NAME = "com.discover.mobile.prefs";
			
	/**Key to whether or not the shared preferences should be shown*/
	public static final String SHOW_LOGIN_MODAL = "showLoginModal";
	
	/**Key for a user ID*/
	public static final String USER_ID = "userId";
	
	/**Key for if we need to be saving a user's ID*/
	public static final String REMEMBER_USER_ID = "rememberId";
	
	/**Key for whether or not the status bar should be shown */
	public static final String STATUS_BAR_VISIBILITY = "statusBarVisibility";
	
	/**Category code to identify an application level preference**/
	public static final int APP_LEVEL_PREF = 0;
	
	/**Category code to identify an user level preference**/
	public static final int USER_LEVEL_PREF = 1;
	
	/**Contains an identifier for the current user logged in. Value is used to construct user preferences file name.**/
	private static String currentUser;
	
	/**
	 * Constructor that will prevent this class from being instantiated
	 */
	private SharedPreferencesWrapper() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}

	/**
	 * Returns the preference level (User or Application) for the key specified. If the key is not known
	 * then application level is returned.
	 * 
	 * @param key Key to a value stored in SharedPreferences
	 * 
	 * @return APP_LEVEL_PREF or USER_LEVEL_PREF
	 */
	public static int getPreferenceLevel(String key) {
		int level = APP_LEVEL_PREF;
		
		// Only check if it is user level preference otherwise 
		// return as an application level preference
		if ( !Strings.isNullOrEmpty(key) && key.equals(SHOW_LOGIN_MODAL) ) {
			level = USER_LEVEL_PREF;
		}
		
		return level;
	}
	
	/**
	 * Returns the name of the preference file used to store the key specified. If the key is unknown then it is stored
	 * in application level preference file
	 * 
	 * @return Name of the SharedPreference file
	 */
	private static String getPreferenceFile(String key) {
		StringBuilder prefFile = new StringBuilder();
		
		//Set file name for currentUser if the currentUser is set and preference level of key is user level
		if( !Strings.isNullOrEmpty(currentUser) && getPreferenceLevel(key) == USER_LEVEL_PREF ) {
			prefFile.append(currentUser);
			prefFile.append(".");
		}
		
		prefFile.append(FILE_NAME);
	
		return prefFile.toString();
	}
	
	/**
	 * Sets the name of the current user logged into the application. To be called by application after successfully logging
	 * in. If not set by application, then all values are stored in the application level preference file.
	 * 
	 * @param user Name of the current user logged into the application.
	 */
	public static void setCurrentUser(String user) {
		currentUser = user;
	}
	
		
    /**
     * Save a boolean value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public static void saveToSharedPrefs(final Context context, final String key, final boolean value){
    	final SharedPreferences settings = context.getSharedPreferences(getPreferenceFile(key), Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit(); 
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default boolean value 
     */
    public static boolean getValueFromSharedPrefs(final Context context, final String key, final boolean defaultValue){
    	final SharedPreferences settings = context.getSharedPreferences(getPreferenceFile(key), Context.MODE_PRIVATE);
    	return settings.getBoolean(key, defaultValue);
    }
    
    /**
     * Save a string value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public static void saveToSharedPrefs(final Context context, final String key, final String value){
    	final SharedPreferences settings = context.getSharedPreferences(getPreferenceFile(key), Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit(); 
    }
    
    /**
     * Get a boolean value to the shared preferences
     * @param key - key of the value to get
     * @param defaultValue - default string value 
     */
    public static String getValueFromSharedPrefs(final Context context, final String key, final String defaultValue){
    	final SharedPreferences settings = context.getSharedPreferences(getPreferenceFile(key), Context.MODE_PRIVATE);
    	return settings.getString(key, defaultValue);
    }
    
}
