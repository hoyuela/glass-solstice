package com.discover.mobile.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.base.Strings;

/**
 * Class holding the keys to the values put in shared preferences. Each value is either an application level or 
 * user level preference. Application should call setCurrentUser() prior to using this class in order to be able to
 * store values accurately. Globals will always use application level to store values by default, 
 * if current user is not specified by application. Setting current user will allow to save preferences for multiple users.
 * 
 * Class uses a dotted notation to store values into persistent storage. A value can either be Application Level,
 * Account Level, or User Level. Application level will only use the key name to store the value. Account Level will 
 * use the current account.key name to store the value. Finally, user level will store a value using 
 * current account.current user.key name. The level will be determined automatically by the class whenever any of the values
 * are stored into persistent storage.
 * 
 * Activities that use this class should use loadPreferences() in its onResume and savePreferences in its onPause().
 * 
 * In order to add a new value do the following:
 * 1. Define a KEY using public static final string <KEY NAME>
 * 2. Add a data member to hold the value in volatile memory
 * 3. Update getPreferenceLevel() if necessary
 * 4. Update loadPreferences() and savePreferences()
 * 5. Update loadUserPreferences if a user preference
 * 6. Update setToDefaults()
 * 
 * @author jthornton, hoyuela
 *
 */
public final class Globals {
	/**Defines tag used for logging**/
	private static final String TAG = Globals.class.getSimpleName();

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

	/**Key used to determine the mode the application is in Card or Bank*/
	public static final String CURRENT_ACCOUNT = "currentAccount";

	/**Contains an identifier for the current user logged in. Value is used to construct user preferences file name.
	 * Stored in Persistent Storage as currentAccount.USER_ID**/
	private static String currentUser;

	/**Contains an identifier for the current account being used by the user. Values can either be CARD_ACCOUNT or 
	 * BANK_ACCOUNT. Stored in Persistent Storage using key name CURRENT_ACCOUNT**/
	private static AccountType currentAccount;

	/**Contains a boolean flag that specifies whether user id should be remembered. Stored in Persistent Storage using
	 * key name currentAccount.REMEMBER_USER_ID
	 */
	private static boolean rememberId;
	/**Contains a boolean flag that specifies whether status bar should be displayed. Stored in Persistent Storage using
	 * key name currentAccount.statusBarVisibility
	 */
	private static boolean statusBarVisibility; 
	/**Contains a boolean flag that specifies whether log out modal should be displayed when a user signs out. Stored in
	 * Persistent Storage using key name currentAccount.currentUser.SHOW_LOGIN_MODAL.
	 */
	private static boolean showLoginModal;

	/**
	 * Last four digits of the card account that will be displayed in the toggle view for sso
	 */
	private static String cardLastFour;

	/**
	 * Name of the card account that will be displayed in the toggle view for sso
	 */
	private static String cardName;

	/**Used to determine whether the user is logged in or not**/
	private static boolean isLoggedIn;

	private static long oldTouchTimeinMillis;

	//Initialize static members at start-up of application
	static {
		setToDefaults();
	}

	/**
	 * Constructor that will prevent this class from being instantiated
	 */
	private Globals() {
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
	public static int getPreferenceLevel(final String key) {
		int level = PreferenceLevel.APP_LEVEL_PREF.getValue();

		//Verify key name is not not null or empty
		if( !Strings.isNullOrEmpty(key) ) {
			// Check if key provided is an account level preference
			if( !key.equals(CURRENT_ACCOUNT) ) {
				level = PreferenceLevel.ACCOUNT_LEVEL_PREF.getValue();
			}

			// Check if key provided is a user level preference  
			if ( key.equals(SHOW_LOGIN_MODAL) ) {
				level |= PreferenceLevel.USER_LEVEL_PREF.getValue();
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Key name provided is null");
			}
		}


		return level;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private static String getStoredKeyName(final String key) {
		final StringBuilder keyName = new StringBuilder();

		//Check if key provided is an account level preference
		if( (getPreferenceLevel(key) & PreferenceLevel.ACCOUNT_LEVEL_PREF.getValue()) == PreferenceLevel.ACCOUNT_LEVEL_PREF.getValue() ) {
			keyName.append(currentAccount);
			keyName.append(".");
		}

		//Set file name for currentUser if the currentUser is set and preference level of key is user level
		if( !Strings.isNullOrEmpty(currentUser) &&
				(getPreferenceLevel(key) & PreferenceLevel.USER_LEVEL_PREF.getValue()) == PreferenceLevel.USER_LEVEL_PREF.getValue() ) {
			keyName.append(currentUser);
			keyName.append(".");
		}

		keyName.append(key);

		return keyName.toString();
	}

	/**
	 * Loads all user, account, and application level preferences from persistent storage. Activities
	 * should call this function in its onResume. User level settings are only loaded if the user
	 * is logged on.
	 * 
	 * @param context Current activity that is loading the preferences.
	 */
	public static void loadPreferences(final Context context) {
		//Load all application level specific preferences
		final SharedPreferences settings = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

		currentAccount = AccountType.values()[settings.getInt(getStoredKeyName(CURRENT_ACCOUNT), AccountType.CARD_ACCOUNT.ordinal())];
		rememberId = settings.getBoolean(getStoredKeyName(REMEMBER_USER_ID), rememberId);
		statusBarVisibility = settings.getBoolean(getStoredKeyName(STATUS_BAR_VISIBILITY), statusBarVisibility);

		//Check whether logged in or not
		if( !isLoggedIn ) {
			//Only Load remembered user if not logged in
			currentUser = (rememberId)?settings.getString(getStoredKeyName(USER_ID), ""):currentUser;

		}  else {
			//Load user level settings only if logged in
			showLoginModal = settings.getBoolean(getStoredKeyName(SHOW_LOGIN_MODAL), true);
		}

		if( Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG,"loadPreferences");
			Log.v(TAG, "isLoggedIn=" +isLoggedIn);
			Log.v(TAG, "Key=" +getStoredKeyName(CURRENT_ACCOUNT) +" Value=" +currentAccount);
			Log.v(TAG, "Key=" +getStoredKeyName(USER_ID) +" Value=" +currentUser);
			Log.v(TAG, "Key=" +getStoredKeyName(REMEMBER_USER_ID) +" Value=" +rememberId);
			Log.v(TAG, "Key=" +getStoredKeyName(STATUS_BAR_VISIBILITY) +" Value=" +statusBarVisibility);
			Log.v(TAG, "Key=" +getStoredKeyName(SHOW_LOGIN_MODAL) +" Value=" +showLoginModal);
		}
	}

	/**
	 * Loads all user, account and application level preferences from persistent storage based on account type. 
	 * 
	 * @param context Current activity that is loading the application preferences.
	 * @param account CARD_ACCOUNT or BANK_ACCOUNT
	 * 
	 */
	public static void loadPreferences(final Context context, final AccountType account) {

		//Load all application level specific preferences
		final SharedPreferences settings = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

		currentAccount = account;
		rememberId = settings.getBoolean(getStoredKeyName(REMEMBER_USER_ID), rememberId);
		statusBarVisibility = settings.getBoolean(getStoredKeyName(STATUS_BAR_VISIBILITY), statusBarVisibility);

		//Check whether logged in or not
		if( !isLoggedIn ) {
			//Only Load remembered user if not logged in
			currentUser = (rememberId)?settings.getString(getStoredKeyName(USER_ID), ""):currentUser;

		} else {
			//Load user level settings only if logged in
			showLoginModal = settings.getBoolean(getStoredKeyName(SHOW_LOGIN_MODAL), true);
		}

		if( Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG,"loadPreferences");
			Log.v(TAG, "isLoggedIn=" +isLoggedIn);
			Log.v(TAG, "Key=" +getStoredKeyName(CURRENT_ACCOUNT) +" Value=" +currentAccount);
			Log.v(TAG, "Key=" +getStoredKeyName(USER_ID) +" Value=" +currentUser);
			Log.v(TAG, "Key=" +getStoredKeyName(REMEMBER_USER_ID) +" Value=" +rememberId);
			Log.v(TAG, "Key=" +getStoredKeyName(STATUS_BAR_VISIBILITY) +" Value=" +statusBarVisibility);
			Log.v(TAG, "Key=" +getStoredKeyName(SHOW_LOGIN_MODAL) +" Value=" +showLoginModal);
		}
	}

	/**
	 * Loads current user level preferences based on account and only if logged on
	 * 
	 * @param context Current activity that is loading the user preferences.
	 */
	public static boolean loadUserPreferences(final Context context) {
		boolean ret = false;

		//Verify a valid account type is provided
		if( isLoggedIn ) {
			//Load all application level specific preferences
			final SharedPreferences settings = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

			//Load user level settings only if logged in
			showLoginModal = settings.getBoolean(getStoredKeyName(SHOW_LOGIN_MODAL), true);

			if( Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG,"loadUserPreferences");
				Log.v(TAG, "isLoggedIn=" +isLoggedIn);
				Log.v(TAG, "Key=" +getStoredKeyName(CURRENT_ACCOUNT) +" Value=" +currentAccount);
				Log.v(TAG, "Key=" +getStoredKeyName(USER_ID) +" Value=" +currentUser);
				Log.v(TAG, "Key=" +getStoredKeyName(REMEMBER_USER_ID) +" Value=" +rememberId);
				Log.v(TAG, "Key=" +getStoredKeyName(STATUS_BAR_VISIBILITY) +" Value=" +statusBarVisibility);
				Log.v(TAG, "Key=" +getStoredKeyName(SHOW_LOGIN_MODAL) +" Value=" +showLoginModal);
			}

			ret = true;
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Invalid account type provided");
			}
		}

		return ret;
	}

	/**
	 * Stores all user and application level preferences to persistent storage. Activities
	 * should call this function in its onPause. 
	 * 
	 * @param context Current activity that is setting the application account.
	 */
	public static void savePreferences(final Context context) {
		//Store all application level specific preferences
		final SharedPreferences settings = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(getStoredKeyName(STATUS_BAR_VISIBILITY), statusBarVisibility);
		editor.putBoolean(getStoredKeyName(SHOW_LOGIN_MODAL), showLoginModal);

		//Username should only be saved if logged in and user has checked remember me in login screen
		if( isLoggedIn ) {
			editor.putInt(getStoredKeyName(CURRENT_ACCOUNT), currentAccount.ordinal());
			editor.putBoolean(getStoredKeyName(REMEMBER_USER_ID), rememberId);

			if( rememberId) {
				editor.putString(getStoredKeyName(USER_ID),currentUser);
			} else {
				editor.putString(getStoredKeyName(USER_ID),"");
			}
		} else if( !rememberId ) {
			//Clear user name from persistent storage
			editor.putString(getStoredKeyName(USER_ID),"");
		}

		editor.commit();

		if( Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG,"savePreferences");
			Log.v(TAG, "isLoggedIn=" +isLoggedIn);
			Log.v(TAG, "Key=" +getStoredKeyName(CURRENT_ACCOUNT) +" Value=" +settings.getInt(getStoredKeyName(CURRENT_ACCOUNT), AccountType.CARD_ACCOUNT.ordinal()));
			Log.v(TAG, "Key=" +getStoredKeyName(USER_ID) +" Value=" +settings.getString(getStoredKeyName(USER_ID), ""));
			Log.v(TAG, "Key=" +getStoredKeyName(REMEMBER_USER_ID) +" Value=" +settings.getBoolean(getStoredKeyName(REMEMBER_USER_ID), rememberId));
			Log.v(TAG, "Key=" +getStoredKeyName(STATUS_BAR_VISIBILITY) +" Value=" +settings.getBoolean(getStoredKeyName(STATUS_BAR_VISIBILITY), statusBarVisibility));
			Log.v(TAG, "Key=" +getStoredKeyName(SHOW_LOGIN_MODAL) +" Value=" +settings.getBoolean(getStoredKeyName(SHOW_LOGIN_MODAL), true));
		}
	}


	/**
	 * Get the current application t maccount the application is in.
	 * 
	 * @return CARD_ACCOUNT or BANK_ACCOUNT
	 */
	public static AccountType getCurrentAccount() {
		return currentAccount;
	}

	/**
	 * Set the current application account the application is in. Default value is empty.
	 * 
	 * @param value CARD_ACCOUNT or BANK_ACCOUNT
	 */
	public static void setCurrentAccount(final AccountType value) {
		//Validate that an acceptable value is provided
		currentAccount = value;
	}

	/**
	 * Sets the name of the current user logged into the application. To be called by application after successfully logging
	 * in. If not set by application, then all values are stored in the application level preference file.
	 * 
	 * @param user Name of the current user logged into the application.
	 */
	public static void setCurrentUser(final String user) {
		currentUser = user;
	}

	/**
	 * 
	 * @return Returns the current user that is logged into the application. By default it is empty.
	 */
	public static String getCurrentUser() {
		return currentUser;
	}

	/**
	 * @return Returns true if the current user id should be remembered in persistent storage. By default is true.
	 */
	public static boolean isRememberId() {
		return rememberId;
	}

	/**
	 * Sets whether the logged in user should be remembered in persistent storage. By default is false.
	 * 
	 * @param rememberId True to store current user id, otherwise false.
	 */
	public static void setRememberId(final boolean rememberId) {
		Globals.rememberId = rememberId;
	}

	/**
	 * 
	 * @return Returns True if status bar should be visible or not
	 */
	public static boolean isStatusBarVisibility() {
		return statusBarVisibility;
	}

	/**
	 * @param statusBarVisibility Set to true if status bar should be visible, false otherwise
	 */
	public static void setStatusBarVisibility(final boolean statusBarVisibility) {
		Globals.statusBarVisibility = statusBarVisibility;
	}

	/**
	 * 
	 * @return True if Log out modal should be shown when user signs off, false otherwise. Default is true.
	 */
	public static boolean isShowLoginModal() {
		return showLoginModal;
	}

	/**
	 * Used to set whether the confirmation modal dialog is displayed for
	 * the current user at log out.
	 * 
	 * @param showLoginModal True is show next time the user signs in, false otherwise.
	 */
	public static void setShowLoginModal(final boolean showLoginModal) {
		Globals.showLoginModal = showLoginModal;
	}

	/**
	 * 
	 * @return True if current user is logged in, and false otherwise. Default is false.
	 */
	public static boolean isLoggedIn() {
		return isLoggedIn;
	}

	public static long getOldTouchTimeInMillis(){
		return oldTouchTimeinMillis;
	}

	public static void setOldTouchTimeInMillis(final long touch){
		oldTouchTimeinMillis = touch;
	}

	/**
	 * Application should set this to true when the user logs into CARD or BANK successfully.
	 * 
	 * @param isLoggedIn True if user is logged in, false otherwise
	 */
	public static void setLoggedIn(final boolean isLoggedIn) {
		Globals.isLoggedIn = isLoggedIn;
	}

	/**
	 * Clears all global values 
	 */
	public static void setToDefaults() {
		if( Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.e(TAG, "Set Globals to Defaults");
		}

		currentAccount = AccountType.CARD_ACCOUNT;
		currentUser = "";
		rememberId = false;
		statusBarVisibility = true;
		showLoginModal = true;
		isLoggedIn = false;
		cardLastFour = null;
		cardName = null;
	}

	/**
	 * Used to update the globals data stored at login for CARD or BANK and retrieves
	 * user information. Should only be called if logged in otherwise will return false.
	 * 
	 * @param account Specify either Globals.CARD_ACCOUNT or Globals.BANK_ACCOUNT
	 * 
	 * @return Returns true if successful, false otherwise.
	 */
	public static boolean updateAccountInformation(final AccountType account, final Context context, final String userId, final boolean saveUserId) {
		boolean ret = false;

		//Only update account information if logged in
		if( Globals.isLoggedIn() ) {
			//Load preferences
			Globals.loadPreferences(context, account);

			//Set current user for the current session  
			Globals.setCurrentUser(userId);

			//Set the current account selected by the user
			Globals.setCurrentAccount(account);

			//Set remember ID value in globals. This will be used to determine whether
			//Current User is stored in persistent storage by the Globals class
			Globals.setRememberId(saveUserId);

			ret = true;
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.w(TAG, "Unable to update account information.");
			}
		}

		return ret;
	}

	/**
	 * @return the card_last_four
	 */
	public static String getCardLastFour() {
		return cardLastFour;
	}

	/**
	 * @param card_last_four the card_last_four to set
	 */
	public static void setCardLastFour(final String cardLastFour) {
		Globals.cardLastFour = cardLastFour;
	}

	/**
	 * @return the card_name
	 */
	public static String getCardName() {
		return cardName;
	}

	/**
	 * @param card_name the card_name to set
	 */
	public static void setCardName(final String cardName) {
		Globals.cardName = cardName;
	}


}
