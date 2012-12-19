package com.discover.mobile.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class holding the keys to the values put in shared preferences
 * 
 * @author jthornton
 *
 */
public final class SharedPreferencesWrapper {
	
	/**String representing the file name*/
	public static final String FILE_NAME = "com.discover.mobile.prefs";
	
	/**Key to whether or not the shared preferences should be shown*/
	public static final String SHOW_LOGIN_MODAL = "showLoginModal";
	
	/**Key for a user ID*/
	public static final String USER_ID = "userId";
	
	/**Key for if we need to be saving a user's ID*/
	public static final String REMEMBER_USER_ID = "rememberId";

	/**
	 * Constructor that will prevent this class from being instantiated
	 */
	private SharedPreferencesWrapper() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}

    /**
     * Save a boolean value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public static void saveToSharedPrefs(final Context context, final String key, final boolean value){
    	final SharedPreferences settings = context.getSharedPreferences(SharedPreferencesWrapper.FILE_NAME, Context.MODE_PRIVATE);
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
    	final SharedPreferences settings = context.getSharedPreferences(SharedPreferencesWrapper.FILE_NAME, Context.MODE_PRIVATE);
    	return settings.getBoolean(key, defaultValue);
    }
    
    /**
     * Save a string value to the shared preferences
     * @param key - key of the value to store
     * @param value - boolean value 
     */
    public static void saveToSharedPrefs(final Context context, final String key, final String value){
    	final SharedPreferences settings = context.getSharedPreferences(SharedPreferencesWrapper.FILE_NAME, Context.MODE_PRIVATE);
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
    	final SharedPreferences settings = context.getSharedPreferences(SharedPreferencesWrapper.FILE_NAME, Context.MODE_PRIVATE);
    	return settings.getString(key, defaultValue);
    }
    
}
