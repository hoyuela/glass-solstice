package com.discover.mobile.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.security.EnhancedAccountSecurityActivity;

/**
 * Utility class to centralize the navigation to and from screens in the application.
 * 
 * @author henryoyuela
 *
 */
public class Navigator {
	public static final String TAG = Navigator.class.getSimpleName();
	
	/**
     * This constructor is not supported and throws an UnsupportedOperationException when called.
     * 
     * @throws UnsupportedOperationException Every time this method is invoked.
     */
	private Navigator() {
		throw new UnsupportedOperationException(
				"This class is non-instantiable"); //$NON-NLS-1$
	}

	/**
	 * Navigates the application to the LoginActivity which is the login page and
	 * closes the referenced activity in the parameter list.
	 * 
	 * @param activity Reference to Activity from where the application will navigate to Login
	 * @param cause Current supported values are IntentExtras SHOW_SUCESSFUL_LOGOUT_MESSAGE and SESSION_EXPIRED
	 */
	public static void navigateToLoginPage(final Activity activity,final String cause) {
		//Send an intent to open login activity if current activity is not login
		if( activity.getClass() != LoginActivity.class ) {
			final Intent intent = new Intent(activity, LoginActivity.class);
			final Bundle bundle = new Bundle();
			bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
			bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
			bundle.putBoolean(cause, true);
			intent.putExtras(bundle);
			activity.startActivity(intent);
			
			//Close current activity
			activity.finish();			
		} else {
			if( IntentExtraKey.SESSION_EXPIRED.equals(cause) ) {
				((LoginActivity)activity).showSessionExpired();
			} else if( IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE.equals(cause)) {
				((LoginActivity)activity).showLogoutSuccessful();
			}
		}	
	}
	
	/**
	 * Navigates the application to the NavigationRootActivity which is the home page and
	 * closes the referenced activity in the parameter list.
	 * 
	 * @param activity Reference to Activity from where it will navigate to home page
	 */
	public static void navigateToHomePage(final Activity activity) {
		if( activity.getClass() != BankNavigationRootActivity.class ) {
			final Intent home = new Intent(activity, BankNavigationRootActivity.class);
			activity.startActivity(home);
			
			//Close current activity
			activity.finish();		
		} else {
			if( Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "Application is already in login view");
			}
		}	
	}
	
	/**
	 * Navigates application to EnhancedAccountSecurityActivity which is used for strong authentication
	 * for both CARD and BANK accounts.
	 * 
	 * @param activity Reference to Activity from where the application will navigate to Strong Auth Page
	 * @param question Question to ask the user for Strong Authentication
	 * @param id Question ID, which will be sent to the server with answer to the question
	 */
	public static void navigateToStrongAuth(final Activity activity,final String question,final String id) {
		final Intent strongAuth = new Intent(activity, EnhancedAccountSecurityActivity.class);
		
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, question);
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, id);
		strongAuth.putExtra(IntentExtraKey.IS_CARD_ACCOUNT, false);
		activity.startActivityForResult(strongAuth, 0);
	}

}
