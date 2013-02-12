package com.discover.mobile.bank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.security.EnhancedAccountSecurityActivity;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;

/**
 * Utility class to centralize the navigation to and from screens in the application.
 * 
 * @author henryoyuela
 *
 */
public class BankNavigator {
	public static final String TAG = BankNavigator.class.getSimpleName();

	/**
	 * This constructor is not supported and throws an UnsupportedOperationException when called.
	 * 
	 * @throws UnsupportedOperationException Every time this method is invoked.
	 */
	private BankNavigator() {
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
				Log.d(TAG, "Application is already in Home Page view");
			}
		}
	}

	/**
	 * Navigates application to EnhancedAccountSecurityActivity which is used for strong authentication
	 * for both CARD and BANK accounts. If EnhancedAccountSecurityActivity is already open, then it will
	 * just update the question displayed to the user.
	 * 
	 * @param activity Reference to Activity from where the application will navigate to Strong Auth Page
	 * @param question Question to ask the user for Strong Authentication
	 * @param id Question ID, which will be sent to the server with answer to the question
	 * @param errorMessage Set to null if no error message needs to be displayed, otherwise set to the error
	 * 						message to display on the StrongAuthPage.
	 * 
	 */
	public static void navigateToStrongAuth(final Activity activity, final BankStrongAuthDetails details, final String errorMessage) {
		if( activity.getClass() != EnhancedAccountSecurityActivity.class ) {
			final Intent strongAuth = new Intent(activity, EnhancedAccountSecurityActivity.class);

			strongAuth.putExtra(IntentExtraKey.IS_CARD_ACCOUNT, false);
			strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_DETAILS, details);
			activity.startActivityForResult(strongAuth, 0);
		} else {
			final EnhancedAccountSecurityActivity strongAuthPage = (EnhancedAccountSecurityActivity)activity;

			if( errorMessage != null ) {
				BankErrorHandler.getInstance().showErrorsOnScreen(strongAuthPage, errorMessage);
			}

			strongAuthPage.updateQuestion(details);
		}
	}

	public static void navigateToNoAccounts() {
		((AlertDialogParent)DiscoverActivityManager.getActiveActivity()).closeDialog();
		//TODO: Remove this code once implemented. This is only for QA testing purposes only
		final CharSequence text = "No Accounts Page Under Development";
		final int duration = Toast.LENGTH_SHORT;

		final Toast toast = Toast.makeText(DiscoverActivityManager.getActiveActivity(), text, duration);
		toast.show();

	}

	public static void navigateToPayBillStepTwo(final Bundle extras){

	}

}
