package com.discover.mobile.bank;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.account.AccountActivityFragment;
import com.discover.mobile.bank.account.AccountActivityViewPager;
import com.discover.mobile.bank.account.ScheduledTransactionsViewPager;
import com.discover.mobile.bank.account.BankOpenAccountFragment;
import com.discover.mobile.bank.account.ScheduledTransactionsViewPager;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.paybills.BankPayTerms;
import com.discover.mobile.bank.paybills.BankPayeeNotEligibleFragment;
import com.discover.mobile.bank.paybills.BankSelectPayee;
import com.discover.mobile.bank.paybills.SchedulePaymentFragment;
import com.discover.mobile.bank.security.EnhancedAccountSecurityActivity;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

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


	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills landing page.
	 */
	public static void navigateToPayBillsLanding(){
		final BankPayeeNotEligibleFragment fragment = new BankPayeeNotEligibleFragment();
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}
	/**
	 * Navigates the application to the Open Accounts Page, which is displayed when a Bank user does not have any accounts.
	 * 
	 */
	public static void navigateToOpenAccount() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();

		if( null != activity  ) {
			activity.makeFragmentVisible(new BankOpenAccountFragment());
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to load Open Account Page");
			}
		}
	}

	/**
	 * Navigates a user to the browser using the URL specified. Prior to moving the user to the 
	 * browser, a modal will be displayed to warn the user that they are navigating away from the application.
	 * 
	 * @param url String that holds the url to be used when opening the browser.
	 */
	public static void navigateToBrowser(final String url) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();

		// Create a one button modal to notify the user that they are leaving the application
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
				R.string.bank_open_browser_title, 
				R.string.bank_open_browser_text, 
				false, 
				R.string.bank_need_help_number_text, 
				R.string.continue_text);

		//Set the dismiss listener that will navigate the user to the browser	
		modal.setOnDismissListener(new OnDismissListener() {
	        @Override
	        public void onDismiss(final DialogInterface arg0) {
	        	final Intent i = new Intent(Intent.ACTION_VIEW);
	    		i.setData(Uri.parse(url));
	    		activity.startActivity(i);
	        }
	    });

		activity.showCustomAlert(modal);
	}

	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills terms and conditions page.
	 */
	public static void navigateToPayBillsTerms(final Bundle extras){
		final BankPayTerms fragment = new BankPayTerms();
		fragment.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills select payee page.
	 */
	public static void naviagteToSelectPayee(final Bundle extras){
		((AlertDialogParent)DiscoverActivityManager.getActiveActivity()).closeDialog();
		final BankSelectPayee fragment = new BankSelectPayee();
		fragment.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills step two page.
	 */
	public static void navigateToPayBillStepTwo(final BaseFragmentActivity activity, final Bundle extras){
		final SchedulePaymentFragment fragment = new SchedulePaymentFragment();
		fragment.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	/**
	 * Navigate to the activity detail view pager screen
	 * @param bundle - bundle to pass into the screen
	 */
	public static void navigateToActivityDetailScreen(final Bundle bundle){
		final AccountActivityViewPager fragment =  new AccountActivityViewPager();
		fragment.setArguments(bundle);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	/**
	 * Navigate to the activity detail table screen
	 * @param bundle - bundle to pass into the screen
	 */
	public static void navigateToAccountActivityPage(final Bundle bundle, final boolean isGoingBack){
		final BankNavigationRootActivity activity =
				(BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();
		((AlertDialogParent)activity).closeDialog();
		if(activity.isDynamicDataFragment() && !isGoingBack){
			activity.addDataToDynamicDataFragment(bundle);
		}else{
			final AccountActivityFragment fragment =  new AccountActivityFragment();
			fragment.setArguments(bundle);
			((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
		}
	}
	
	/**
	 * Calls the NavigateToAcountActivityPage with false as the default parameter for isGoingBack.
	 * So that we could add support for going back to the method without breaking the calls that are already in use
	 * elsewhere.
	 * @param bundle
	 */
	public static void navigateToAccountActivityPage(final Bundle bundle){
		navigateToAccountActivityPage(bundle, false);
	}

}
