package com.discover.mobile.bank.framework;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.AccountActivityViewPager;
import com.discover.mobile.bank.account.BankAccountActivityTable;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.account.BankOpenAccountFragment;
import com.discover.mobile.bank.atm.AtmLocatorActivity;
import com.discover.mobile.bank.atm.AtmMapFragment;
import com.discover.mobile.bank.auth.strong.EnhancedAccountSecurityActivity;
import com.discover.mobile.bank.deposit.BankDepositConfirmFragment;
import com.discover.mobile.bank.deposit.BankDepositForbidden;
import com.discover.mobile.bank.deposit.BankDepositNotEligibleFragment;
import com.discover.mobile.bank.deposit.BankDepositSelectAccount;
import com.discover.mobile.bank.deposit.BankDepositSelectAmount;
import com.discover.mobile.bank.deposit.BankDepositTermsFragment;
import com.discover.mobile.bank.deposit.BankDepositWorkFlowStep;
import com.discover.mobile.bank.deposit.CaptureReviewFragment;
import com.discover.mobile.bank.deposit.CheckDepositErrorFragment;
import com.discover.mobile.bank.deposit.DuplicateCheckErrorFragment;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.help.BankInfoNavigationActivity;
import com.discover.mobile.bank.help.BankPrivacyTermsFragment;
import com.discover.mobile.bank.help.ContactUsType;
import com.discover.mobile.bank.help.CustomerServiceContactsFragment;
import com.discover.mobile.bank.help.FAQDetailFragment;
import com.discover.mobile.bank.help.FAQLandingPageFragment;
import com.discover.mobile.bank.help.LoggedOutFAQActivity;
import com.discover.mobile.bank.help.PrivacyTermsType;
import com.discover.mobile.bank.help.ProvideFeedbackFragment;
import com.discover.mobile.bank.help.TermsLandingPageFragment;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.navigation.BankNavigationHelper;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.paybills.BankPayConfirmFragment;
import com.discover.mobile.bank.paybills.BankPayTerms;
import com.discover.mobile.bank.paybills.BankPayeeNotEligibleFragment;
import com.discover.mobile.bank.paybills.BankSelectPayee;
import com.discover.mobile.bank.paybills.PaymentDetailsViewPager;
import com.discover.mobile.bank.paybills.ReviewPaymentsTable;
import com.discover.mobile.bank.paybills.SchedulePaymentFragment;
import com.discover.mobile.bank.payees.BankAddManagedPayeeFragment;
import com.discover.mobile.bank.payees.BankAddPayeeConfirmFragment;
import com.discover.mobile.bank.payees.BankAddUnmanagedPayeeFragment;
import com.discover.mobile.bank.payees.BankEnterPayeeFragment;
import com.discover.mobile.bank.payees.BankManagePayee;
import com.discover.mobile.bank.payees.BankSearchSelectPayeeFragment;
import com.discover.mobile.bank.payees.PayeeDetailViewPager;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.auth.BankLoginDetails;
import com.discover.mobile.bank.services.auth.BankSSOLoginDetails;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payee.SearchPayeeResultList;
import com.discover.mobile.bank.services.payee.SearchPayeeServiceCall;
import com.discover.mobile.bank.services.payment.GetPaymentsServiceCall;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.bank.transfer.BankTransferConfirmationFragment;
import com.discover.mobile.bank.transfer.BankTransferFrequencyWidget;
import com.discover.mobile.bank.transfer.BankTransferNotEligibleFragment;
import com.discover.mobile.bank.transfer.BankTransferSelectAccount;
import com.discover.mobile.bank.transfer.BankTransferStepOneFragment;
import com.discover.mobile.bank.ui.fragments.BankTextViewFragment;
import com.discover.mobile.bank.ui.fragments.BankUnderDevelopmentFragment;
import com.discover.mobile.bank.util.BankAtmUtil;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.framework.CacheManager;
import com.discover.mobile.common.framework.Conductor;
import com.discover.mobile.common.framework.ServiceCallFactory;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.utils.CommonUtils;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.common.base.Strings;

/**
 * Utility class to centralize the navigation to and from screens in the application.
 *
 * Extends the Conductor to allow for access to the abstract navigation / cache pattern
 *
 * @author henryoyuela
 *
 */
public final class BankConductor  extends Conductor {

	protected static BankConductor instance;

	protected CacheManager cacheMgr = BankUser.instance();

	private static BankLoginDetails loginDetails;


	/**
	 * To utilize the abstract navigate methods from the parent conductor class
	 *
	 * @param pServiceCallFactory
	 */
	private BankConductor(final ServiceCallFactory pServiceCallFactory) {
		super(pServiceCallFactory);

	}

	public static final String TAG = BankConductor.class.getSimpleName();

	/**
	 * A singleton instance
	 * @return
	 */
	public static BankConductor getInstance(){
		if ( instance == null ) {
			instance = new BankConductor(new BankServiceCallFactory());
		}
		return instance;
	}

	/**
	 * Navigates to the given fragment. 1. checks to see if fragment requires
	 * data 2. requests data from cache manager 3. makes service call if cache
	 * data unavailable 4. navigates to class
	 * 
	 * @param fragmentClass
	 *            - the destination class
	 * assumes no payload required for service call, if necessary 
	 * 
	 */
	@Override
	public void launchFragment(final Class<? extends BaseFragment> fragmentClass){
		launchFragment(fragmentClass, null, null);
	}

	/**
	 * Navigates to the given fragment. 
	 * <pre>
	 * 1. checks to see if fragment requires data 
	 * 2. requests data from cache manager 
	 * 3. makes service call if cache data unavailable 
	 * 4. navigates to class
	 * 
	 * @param fragmentClass
	 *            - the destination class
	 * @param payload  - the payload for the service call, if necessary
	 * @param bundle
	 *            - bundle to pass on when navigating.
	 */
	@Override
	public void launchFragment(final Class<? extends BaseFragment> fragmentClass, final Serializable payload, final Bundle bundle) {
		@SuppressWarnings("rawtypes")
		final
		Class cacheObjReq = lookupCacheRequiredForDestination(fragmentClass);
		if (cacheObjReq == null) {
			// no data required, don't perform the service call; just navigate
			navigateToFrament(fragmentClass, bundle);
		} else {
			final Serializable o = (Serializable) BankUser.instance().getObjectFromCache(cacheObjReq);
			if (o == null) {
				// cache is null, let's make the call

				// call payload in the bundle
				@SuppressWarnings("unchecked")
				final NetworkServiceCall<?> call = serviceCallFactory.createServiceCall(cacheObjReq,payload);
				// associate the destination with the call
				destinationMap.put(call.hashCode(), new DestinationDetails(DestinationType.FRAGMENT, fragmentClass, bundle));
				call.submit();
			}else{
				final Bundle updatedBundle = new Bundle();
				if(null != bundle){
					updatedBundle.putAll(bundle);
				}
				updatedBundle.putSerializable(BankExtraKeys.PAYLOAD, o);
				navigateToFrament(fragmentClass, updatedBundle);
			}
		}
	}

	/**
	 * reusable navigate method
	 * 
	 * @param destClass
	 * @param bundle
	 */
	@Override
	protected void navigateToFrament(
			@SuppressWarnings("rawtypes") final Class destClass, final Bundle bundle) {
		Fragment fragment;
		try {
			fragment = (Fragment) destClass.newInstance();
		} catch (final Exception e) {
			throw new RuntimeException(
					"Unable to instantiate to supplied fragment!  Please ensure public no-arg constructor");
		}
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		((BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}


	/**
	 * Navigates the application to the LoginActivity which is the login page and
	 * closes the referenced activity in the parameter list.
	 *
	 * @param activity Reference to Activity from where the application will navigate to Login
	 * @param cause Current supported values are IntentExtras SHOW_SUCESSFUL_LOGOUT_MESSAGE, SHOW_ERROR_MESSAGE and SESSION_EXPIRED
	 * @param message Used to pass in a message to the LoginActivity and display to the user. Used only if cause is SHOW_ERROR_MESSAGE.
	 */
	public static void navigateToLoginPage(final Activity activity,final String cause, final String message) {
		//Send an intent to open login activity if current activity is not login
		if( activity.getClass() != LoginActivity.class ) {
			final Intent intent = new Intent(activity, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			final Bundle bundle = new Bundle();
			bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
			bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);

			//Verify cause is not empty and is not equal to SHOW_ERROR_MESSAGE
			if( !Strings.isNullOrEmpty(cause) && !IntentExtraKey.SHOW_ERROR_MESSAGE.equals(cause) ) {
				bundle.putBoolean(cause, true);
			} else {
				bundle.putString(IntentExtraKey.SHOW_ERROR_MESSAGE, message);
			}

			intent.putExtras(bundle);
			activity.startActivity(intent);

			//Close current activity
			activity.finish();
		} else {
			if( IntentExtraKey.SESSION_EXPIRED.equals(cause) ) {
				((LoginActivity)activity).showSessionExpired();
			} else if( IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE.equals(cause)) {
				((LoginActivity)activity).showLogoutSuccessful();
			} else if( !Strings.isNullOrEmpty(message)) {
				((LoginActivity)activity).showErrorMessage(message);
			}
		}
	}

	/**
	 * Navigates the application to the NavigationRootActivity which is the home page and
	 * closes the referenced activity in the parameter list.
	 *
	 * @param activity Reference to Activity from where it will navigate to home page
	 */
	public static void navigateToHomePage() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		if( activity.getClass() != BankNavigationRootActivity.class ) {
			final Intent home = new Intent(activity, BankNavigationRootActivity.class);
			home.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			activity.startActivity(home);

			//Close current activity
			activity.finish();
		} else {
			if( Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "Application is already in Home Page view");
			}

			((BankNavigationRootActivity)activity).popTillFragment(BankAccountSummaryFragment.class);
		}
	}

	public static void navigateToFAQLandingPage() {
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		if(currentActivity instanceof BankNavigationRootActivity) {
			final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();

			activity.getCurrentContentFragment();

			/**Check if user is already viewing FAQ*/
			if( !BankNavigationHelper.isViewingMenuSection(BankMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP, 
					BankMenuItemLocationIndex.FREQUENTLY_ASKED_QUESTIONS)) {
				activity.makeFragmentVisible(new FAQLandingPageFragment());
			} else {
				activity.hideSlidingMenuIfVisible();
			}
		}else{
			final Intent loggedOutFAQ = new Intent(currentActivity, LoggedOutFAQActivity.class);
			currentActivity.startActivity(loggedOutFAQ);
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
	public static void navigateToStrongAuth(final Activity activity,
			final BankStrongAuthDetails details, final String errorMessage) {
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
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		if(activity != null && activity instanceof NavigationRootActivity ) {
			// Create a one button modal to notify the user that they are leaving the application
			final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
					R.string.bank_open_browser_title,
					R.string.bank_open_browser_text,
					R.string.continue_text);

			//Set the dismiss listener that will navigate the user to the browser
			modal.getBottom().getButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					modal.dismiss();
					final Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					activity.startActivity(i);
				}
			});

			/**Hide Need Help footer*/
			((ModalDefaultTopView)modal.getTop()).hideNeedHelpFooter();

			((NavigationRootActivity)activity).showCustomAlert(modal);

		}
	}

	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills terms and conditions page.
	 */
	public static void navigateToPayBillsTerms(final Bundle extras){
		((AlertDialogParent)DiscoverActivityManager.getActiveActivity()).closeDialog();

		final BankPayTerms fragment = new BankPayTerms();
		fragment.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills select payee page.
	 */
	public static void navigateToSelectPayee(final Bundle extras){
		((AlertDialogParent)DiscoverActivityManager.getActiveActivity()).closeDialog();
		final BankSelectPayee fragment = new BankSelectPayee();
		fragment.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	/**
	 * Let the root activity know that the current fragment needs to be changed from the current fragment
	 * to the navigate to pay bills step two page.
	 */
	public static void navigateToPayBillStepTwo(final Bundle extras){
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
	 * Navigate to the view pager that will display payee details.
	 * @param bundle - bundle to pass info to the view pager.
	 */
	public static void navigateToPayeeDetailScreen(final Bundle bundle){
		final PayeeDetailViewPager fragment = new PayeeDetailViewPager();
		fragment.setArguments(bundle);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}

	public static void navigateToFAQDetail(final String extraKey) {
		final Bundle extras = new Bundle();
		extras.putString(BankExtraKeys.FAQ_TYPE, extraKey);
		final FAQDetailFragment faqDetail = new FAQDetailFragment();
		faqDetail.setArguments(extras);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(faqDetail);
	}

	/**
	 * Navigate to the activity detail table screen
	 * @param bundle - bundle to pass into the screen
	 */
	public static void navigateToAccountActivityPage(final Bundle bundle, final boolean isGoingBack){
		final BankNavigationRootActivity activity =
				(BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();
		((AlertDialogParent)activity).closeDialog();

		//Handle the case where loading more data
		if(activity.isFragmentLoadingMore() && !isGoingBack){
			activity.addDataToDynamicDataFragment(bundle);
		}
		//Handle the case where switch between different types of activity posted and scheduled
		else if( activity.getCurrentContentFragment() instanceof BankAccountActivityTable ) {
			final BankAccountActivityTable revPmtFrag = (BankAccountActivityTable)activity.getCurrentContentFragment();
			revPmtFrag.handleReceivedData(bundle);
		}
		//Handle the first time user opens Account Activity page
		else {
			final BankAccountActivityTable fragment =  new BankAccountActivityTable();
			fragment.setArguments(bundle);
			((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
		}
	}

	public static void navigateToSelectTransferAccount(final Bundle argumentBundle) {
		final Fragment nextVisibleFragment = new BankTransferSelectAccount();
		final BankNavigationRootActivity activity =
				(BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();
		nextVisibleFragment.setArguments(argumentBundle);
		activity.makeFragmentVisible(nextVisibleFragment);
	}

	/**
	 * Navigate to the manage payee Fragment with a bundle of extras to display.
	 * @param extras
	 * @param isGoingBack
	 */
	public static void navigateToManagePayee(final Bundle extras, final boolean isGoingBack){
		final BankNavigationRootActivity activity =
				(BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();

		((AlertDialogParent)activity).closeDialog();
		if(activity.isFragmentLoadingMore() && !isGoingBack){
			activity.addDataToDynamicDataFragment(extras);
		} else if( extras != null && extras.getBoolean(BankExtraKeys.CONFIRM_DELETE)) {
			/**Navigate user back to Manage Payee screen if they were in the middle of a delete*/
			activity.popTillFragment(BankManagePayee.class);

			if( activity.getCurrentContentFragment() instanceof BankManagePayee) {
				final BankManagePayee managePayees = (BankManagePayee) activity.getCurrentContentFragment();

				/**Refresh screen with new data */
				managePayees.refreshScreen(extras);
			}
		}
		else {
			final BankManagePayee fragment = new BankManagePayee();
			fragment.setArguments(extras);
			((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);

		}
	}

	/**
	 * A convenience method for navigateToManagePayee(Bundle, boolean). Passes false as the default parameter
	 * to the boolean value for isGoingBack.
	 * @param extras a Bundle of extras to display in the manage payee Fragment.
	 */
	public static void navigateToManagePayee(final Bundle extras){
		navigateToManagePayee(extras, false);
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

	/**
	 * Navigation method used to display the BankPayConfirmFragment using the BankNavigationRootActivity.
	 * This Fragment will not be added to the back stack as the user should not be able to navigate
	 * back to this screen from the application.
	 *
	 * @param value Reference to PaymentDetail information used to schedule a Payment
	 */
	public static void navigateToPayConfirmFragment(final PaymentDetail value) {
		((AlertDialogParent)DiscoverActivityManager.getActiveActivity()).closeDialog();
		final BankPayConfirmFragment fragment = new BankPayConfirmFragment();
		final Bundle bundle = new Bundle();

		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, value);
		fragment.setArguments(bundle);

		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment, false);
	}

	/**
	 * Navigation method used to display the Review Payments page with the delete message
	 */
	public static void navigateToReviewPaymentsFromDelete(final Bundle bundle) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		//Fetch the current activity
		if( activity instanceof BaseFragmentActivity ) {
			final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
			final GetPaymentsServiceCall call = BankServiceCallFactory.createGetPaymentsServerCall(url);
			call.setWasDeleted(true);
			call.setExtras(bundle);
			call.submit();

		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Unable to get current Activity");
			}
		}
	}

	/**
	 * Navigation method used to display feedback landing page
	 */
	public static void navigateToFeedback() {
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final Bundle bundle = new Bundle();
		bundle.putBoolean(BankInfoNavigationActivity.PROVIDE_FEEDBACK, true);
		
		if( activity != null ) {
			/**Launch Provide Feedback Activity if user is not logged in*/
			if( activity instanceof LoginActivity ) {
				final Intent intent = new Intent(activity, BankInfoNavigationActivity.class);
				intent.putExtras(bundle);
				activity.startActivity(intent);
				activity.finish();
			} 			
			/**Verify that the user is logged in and the NavigationRootActivity is the active activity*/
			else if(currentActivity != null && currentActivity instanceof NavigationRootActivity) {
				((NavigationRootActivity)currentActivity).makeFragmentVisible(new ProvideFeedbackFragment());
			}
		}
	}

	/**
	 * Navigation method used to display the under development fragment for when screens
	 * have not been dev complete
	 */
	public static void navigateToUnderDevelopment() {
		final BankUnderDevelopmentFragment fragment =  new BankUnderDevelopmentFragment();
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);

	}

	/**
	 * Navigation method used to display the Delete Transaction modal for when deleting a
	 * Scheduled Payment Transaction
	 *
	 * @param pmtDetail Reference to PaymentDetail object which contains information about the transaction being deleted.
	 */
	public static void navigateToDeleteConfirmation(final PaymentDetail pmtDetail) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();

		// Create a one button modal to notify the user that they are leaving the application
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
				R.string.bank_delete_transaction_title,
				R.string.bank_delete_transaction_text,
				R.string.bank_yes_delete);

		/**
		 * Hide the need help footer for the delete modal.
		 */
		final ModalDefaultTopView topView = (ModalDefaultTopView)modal.getTop();
		topView.hideNeedHelpFooter();

		//Set the click listener that will delete the payment
		modal.getBottom().getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				modal.dismiss();
				BankServiceCallFactory.createDeletePaymentServiceCall(pmtDetail).submit();
			}
		});

		activity.showCustomAlert(modal);
	}

	/**
	 * Navigation method used to display the Add Payee a Step in the Add Payee Work-Flow. Instantiates an EnterPayeeFragment and makes it visible to user
	 * via the NavigationRootActivity. This method should only be called if the application is in the BankNavigationRootActivity.
	 *
	 * @param step - Class type of a fragment that is to be displayed. Can be BankEnterPaymentFragment or BankSearchSelectPaymentFragment
	 * @param bundle - Contains the arguments that is to be provided to the fragment that will be displayed
	 */
	public static void navigateToAddPayee(final Class<?>  step, final Bundle bundle) {
		BaseFragment fragment = null;

		//Verify the current activity is the BankNavigationRootActivity
		if(  DiscoverActivityManager.getActiveActivity() instanceof BankNavigationRootActivity ) {
			final BaseFragmentActivity activity = (BaseFragmentActivity)DiscoverActivityManager.getActiveActivity();
			activity.closeDialog();

			//If class type is BankEnterPayeeFragment then open the Search Payee Fragment Step 2 of work-flow
			if( step == BankEnterPayeeFragment.class ) {
				fragment = new BankEnterPayeeFragment();
				activity.makeFragmentVisible(fragment);
			}
			//If class type is BankAddManagedPayeeFragment then open the Add Managed Payee Fragment Step 4 of work-flow
			else if( step == BankAddManagedPayeeFragment.class ) {
				fragment = new BankAddManagedPayeeFragment();
				fragment.setArguments(bundle);
				activity.makeFragmentVisible(fragment);
			}
			//If class type is BankAddUnmanagedPayeeFragment then open the Add Unmanaged Payee Step 4 of work-flow
			else if( step == BankAddUnmanagedPayeeFragment.class) {
				fragment = new BankAddUnmanagedPayeeFragment();
				fragment.setArguments(bundle);
				activity.makeFragmentVisible(fragment);
			}
			//If class type is BankAddPayeeConfirmFragment then open the Add Payee Confirmation Fragment Step 5 of work-flow
			else if( step == BankAddPayeeConfirmFragment.class) {
				fragment = new BankAddPayeeConfirmFragment();
				fragment.setArguments(bundle);
				activity.makeFragmentVisible(fragment);
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Invalid Class Type provided");
				}
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Appication is currently not in the right activity");
			}
		}
	}

	/**
	 * Method used to display the Delete Payee Modal Page when a user attempts to delete a payee.
	 *
	 * @param bundle Reference to a bundle which holds a reference to a PayeeDetail object that has the information about the
	 *               the payee being deleted. Use the key DATA_LIST_ITEM to populate with PayeeDetail object.
	 */
	public static void navigateToDeletePayeeModal(final Bundle bundle) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();

		// Create a one button modal to notify the user that they are leaving the application
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
				R.string.bank_payee_delete_title,
				R.string.bank_payee_delete_body,
				R.string.bank_payee_delete_action);

		/**
		 * Hide the need help footer for the delete modal.
		 */
		final ModalDefaultTopView topView = (ModalDefaultTopView)modal.getTop();
		topView.hideNeedHelpFooter();

		//Set the click listener that will delete the payment
		modal.getBottom().getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				modal.dismiss();

				if( bundle != null ) {
					final PayeeDetail payee = (PayeeDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
					BankServiceCallFactory.createDeletePayeeServiceCall(payee).submit();
				}
			}
		});

		activity.showCustomAlert(modal);
	}

	/**
	 * Navigate to the Review payments detail table screen
	 * @param bundle - bundle to pass into the screen
	 */
	public static void navigateToReviewPaymentsTable(final Bundle bundle, final boolean isGoingBack){
		final BankNavigationRootActivity activity =
				(BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();
		((AlertDialogParent)activity).closeDialog();

		//Handle the case where loading more data
		if(activity.isFragmentLoadingMore() && !isGoingBack){
			activity.addDataToDynamicDataFragment(bundle);
		}
		//Handle the case where switch between different types of payments scheduled, cancelled, payment
		else if( activity.getCurrentContentFragment() instanceof ReviewPaymentsTable ) {
			final ReviewPaymentsTable revPmtFrag = (ReviewPaymentsTable)activity.getCurrentContentFragment();
			revPmtFrag.handleReceivedData(bundle);
		}
		//Handle the first time user opens Review Payments page
		else {
			final ReviewPaymentsTable fragment =  new ReviewPaymentsTable();
			fragment.setArguments(bundle);
			((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
		}
	}

	/**
	 * Calls the NavigateToAReviewPaymentsTable with false as the default parameter for isGoingBack.
	 * So that we could add support for going back to the method without breaking the calls that are already in use
	 * elsewhere.
	 * @param bundle
	 */
	public static void navigateToReviewPaymentsTable(final Bundle bundle){
		navigateToReviewPaymentsTable(bundle, false);
	}

	/**
	 * Navigate to the payment detail view pager screen
	 * @param bundle - bundle to pass into the screen
	 */
	public static void navigateToPaymentDetailScreen(final Bundle bundle){
		final PaymentDetailsViewPager fragment =  new PaymentDetailsViewPager();
		fragment.setArguments(bundle);
		((BaseFragmentActivity)DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(fragment);
	}


	/**
	 * Navigation method used to show the Select Payee Page displayed after searching for a Payee in the Add Payee work-flow.
	 * If the search argument has no results, then a modal is displayed to the user indicating that there were no matches found.
	 *
	 * @param search Reference to a SearchPayeeResultList object generated from a response to a Payee Search request.
	 */
	public static void navigateToSelectPayees(final SearchPayeeResultList search) {
		final BankNavigationRootActivity activity =
				(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		activity.closeDialog();

		if( BankNetworkServiceCallManager.getInstance().getLastServiceCall() instanceof SearchPayeeServiceCall ) {
			final BankSearchSelectPayeeFragment fragment = new BankSearchSelectPayeeFragment();
			final SearchPayeeServiceCall searchCall = (SearchPayeeServiceCall)BankNetworkServiceCallManager.getInstance().getLastServiceCall();
			final Bundle bundle = new Bundle();

			//Provide the text used for running a search
			bundle.putSerializable(BankSearchSelectPayeeFragment.SEARCH_ITEM, searchCall.getSearchText());
			//Provide list of results sent from the server
			bundle.putSerializable(BankExtraKeys.PAYEES_LIST, search);
			fragment.setArguments(bundle);

			activity.makeFragmentVisible(fragment);
		} else {
			//Show catch all error to the user, this should never happen
			BankErrorHandler.getInstance().handleGenericError(0);

			if(Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unexpected Service Call Found!");
			}
		}
	}

	/**
	 * Navigation method used to navigate to the first step of Transfer Money.
	 * If a customer is not eligible for Transfer Money, they will be directed to a page that
	 * allows them to sign up for the service with an external browser.
	 */
	public static void navigateToTransferMoneyLandingPage(final Bundle args) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
		if( activity != null && activity instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
			navActivity.closeDialog();
			final boolean isEligible = BankUser.instance().getCustomerInfo().isTransferEligible();

			Fragment nextVisibleFragment = null;

			if(isEligible) {
				nextVisibleFragment = new BankTransferStepOneFragment();
				nextVisibleFragment.setArguments(args);
			} else {
				nextVisibleFragment = new BankTransferNotEligibleFragment();
			}

			if(nextVisibleFragment != null) {
				if(args != null && args.getBoolean(BankExtraKeys.SHOULD_NAVIGATE_BACK)) {
					navActivity.popTillFragment(BankTransferStepOneFragment.class);
				} else {
					navActivity.makeFragmentVisible(nextVisibleFragment);
				}
			}
		}

	}

	/**
	 * Navigate the user to the transfer confirmation page with the results from a successful
	 * transfer from the Bundle parameter.
	 * @param args
	 */
	public static void navigateToTransferConfirmation(final Bundle args) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
		if( activity != null && activity instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
			final Fragment nextVisibleFragment = new BankTransferConfirmationFragment();

			navActivity.closeDialog();
			nextVisibleFragment.setArguments(args);
			navActivity.makeFragmentVisible(nextVisibleFragment);
		}
	}

	/**
	 * Navigation method used to navigate to Check Deposit work-flow. Navigates to Check Deposit - Terms
	 * and Conditions if user is eligible and not enrolled. If it is the start of the work flow, then navigates
	 * user to Select Account Page.
	 */
	public static void navigateToCheckDepositWorkFlow(final Bundle bundle, final BankDepositWorkFlowStep step) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
		if( activity != null && activity instanceof BankNavigationRootActivity ) {
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
			navActivity.closeDialog();

			Fragment fragment = null;

			final boolean isEligible = BankUser.instance().getCustomerInfo().isDepositEligible();
			final boolean isEnrolled = BankUser.instance().getCustomerInfo().isDepositEnrolled();
			final boolean isForbidden = BankUser.instance().getCustomerInfo().getDepositsEligibility().isUserBlocked();

			//Check if user is forbidden to use check deposit
			if( isForbidden ) {
				fragment = new BankDepositForbidden();
				//Check if user is eligible and has eligible accounts
			} else if(!isEligible || !BankUser.instance().hasDepositEligibleAccounts()){
				fragment = new BankDepositNotEligibleFragment();
			}
			//Check if user is enrolled
			else if(isEligible && !isEnrolled){
				fragment = new BankDepositTermsFragment();
			}
			else{
				switch( step ) {
				//Navigate user to second step in check deposit work-flow
				case SelectAmount:
					fragment = new BankDepositSelectAmount();
					break;
					//Navigate user to first step in check deposit work-flow
				case SelectAccount:
					fragment = new BankDepositSelectAccount();
					break;
					//Navigate user to page where they can review their deposit
				case ReviewDeposit:
					fragment = new CaptureReviewFragment();
					break;
					//Navigate user to final step in Check deposit work-flow
				case Confirmation:
					fragment = new BankDepositConfirmFragment();
					break;
					//Navigate to timeout error if check deposit error fragment flag is found in bundle
				case DepositError:
					fragment = new CheckDepositErrorFragment();
					break;
					//Navigate to duplicate error fragment if boolean flag is found in bundle
				case DuplicateError:
					fragment = new DuplicateCheckErrorFragment();
					break;
					//Navigate to forbidden error fragment if user receives a 403 error code
				case ForbiddenError:
					fragment = new BankDepositForbidden();
					break;
				}
			}

			if( fragment != null ) {
				fragment.setArguments(bundle);
				navActivity.makeFragmentVisible(fragment);
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to navigate to check deposit work-flow");
			}
		}
	}


	/**
	 * Navigation method used to display the Call modal for when tapping on a phone number link
	 *
	 * @param number Number to send to dialer if user acknowledges the modal.
	 */
	public static void navigateToCallModal(final String number) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
		if( activity != null && activity instanceof BankNavigationRootActivity ) {
			// Create a one button modal to notify the user that they are leaving the application
			final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
					R.string.bank_callmodal_title,
					R.string.bank_callmodal_msg,
					R.string.bank_callmodal_action);

			//Set the dismiss listener that will navigate the user to the dialer
			modal.getBottom().getButton().setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(final View v) {
					modal.dismiss();
					CommonUtils.dialNumber(number, activity);
				}
			});

			/**
			 * Hide the need help footer for the delete modal.
			 */
			final ModalDefaultTopView topView = (ModalDefaultTopView)modal.getTop();
			topView.hideNeedHelpFooter();

			((BankNavigationRootActivity) activity).showCustomAlert(modal);
		}
	}

	/**
	 * Get the list of atm to the atm locator fragment
	 * @param bundle
	 */
	public static void navigateToAtmLocatorFragment(final Bundle bundle) {
		if(DiscoverActivityManager.getActiveActivity() instanceof AtmLocatorActivity){
			final AtmLocatorActivity activity = (AtmLocatorActivity)DiscoverActivityManager.getActiveActivity();
			activity.closeDialog();
			activity.getMapFragment().handleReceivedData(bundle);
		}else if(DiscoverActivityManager.getActiveActivity() instanceof BankNavigationRootActivity){
			final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
			if(activity.getCurrentContentFragment() instanceof AtmMapFragment){
				activity.closeDialog();
				((AtmMapFragment)activity.getCurrentContentFragment()).handleReceivedData(bundle);
			}
		}
	}

	/**
	 * Get the list of address results to the map fragment being displayed.  Eat the call if the fragment is no
	 * longer on the screen
	 * @param bundle - bundle of data to give the fragment
	 */
	public static void navigateToSearchAtmLocatorFragment(final Bundle bundle){
		final NavigationRootActivity activity = (NavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		if(activity instanceof AtmLocatorActivity){
			((AtmLocatorActivity)activity).getMapFragment().handleAddressToLocationResponse(bundle);
		}else if(activity.getCurrentContentFragment() instanceof AtmMapFragment){
			((AtmMapFragment)activity.getCurrentContentFragment()).handleAddressToLocationResponse(bundle);
		}
	}

	/**
	 * Authorizes a Bank user against the service. If successful, the user will
	 * be logged-in and taken to the Bank landing page of the application.
	 *
	 * @param credentials
	 */
	public static void authorizeWithCredentials(final BankLoginDetails credentials) {
		loginDetails = credentials;
		BankServiceCallFactory.createLoginCall(credentials).submit();
	}

	/**
	 * Authorizes an SSO User against Bank using a BankSSOPayload, which is
	 * obtained from a Card service.
	 *
	 * @param bankSSOPayload
	 *            payload with which the user is authorized.
	 */
	public static void authWithBankPayload(final String bankSSOPayload) {
		final BankSSOLoginDetails bankPayload = new BankSSOLoginDetails();
		bankPayload.payload = bankSSOPayload;
		KeepAlive.setCardAuthenticated(true);
		BankServiceCallFactory.createSSOLoginCall(bankPayload).submit();
		loginDetails = null;
	}

	/**
	 * Authorizes an SSO User against Card using a CardSSOPayload, which in some
	 * cases is obtained from a call to {@code BankLoginServices.authorizeLogin()}.
	 *
	 * @param activity
	 * @param tokenValue
	 * @param hashedTokenValue
	 */
	public static void authWithCardPayload(final LoginActivity activity, final String tokenValue, final String hashedTokenValue) {
		FacadeFactory.getCardLoginFacade().loginWithPayload(activity,
				tokenValue, hashedTokenValue);
	}

	/**
	 * Authorizes an SSO User against Bank when no BankSSOPayload is available.
	 * This is due to an A/L/U error returned from a Card service. This will
	 * prompt the user about the issue and continue if they accept.
	 */
	public static void authDueToALUStatus() {
		final LoginActivity activity = (LoginActivity) DiscoverActivityManager
				.getActiveActivity();
		activity.showALUStatusModal(null);
	}

	/**
	 * Authorizes an SSO User against Bank when no BankSSOPayload is available.
	 * This is due to an A/L/U error returned from a Card service.
	 */
	public static void authDueToALUStatus(final String username, final String password) {
		final BankLoginDetails credentials = new BankLoginDetails();
		credentials.username = username;
		credentials.password = password;
		final LoginActivity activity = (LoginActivity) DiscoverActivityManager
				.getActiveActivity();
		activity.showALUStatusModal(credentials);
	}

	/**
	 * Continues with the Skip SSO login call using provided credentials.
	 * Typicall used when a login call originates from Card.
	 */
	public static void continueAuthDueToALU(final BankLoginDetails credentials) {
		KeepAlive.setCardAuthenticated(false);
		BankServiceCallFactory.createLoginCall(credentials, true).submit();
	}

	/**
	 * Continues with the Skip SSO login call if credentials are available.
	 * Typically used when a login call originates from Bank.
	 */
	public static void continueAuthDueToALU() {
		if(loginDetails != null) {
			KeepAlive.setCardAuthenticated(false);
			BankServiceCallFactory.createLoginCall(loginDetails, true).submit();
			loginDetails = null;
		}
	}

	/**
	 * Navigate to the email screen after getting the directions
	 * @param bundle - bundle containing the results of the service call
	 */
	public static void navigateToEmailDirections(final Bundle bundle) {
		final AtmLocatorActivity activity = (AtmLocatorActivity)DiscoverActivityManager.getActiveActivity();
		activity.closeDialog();
		BankAtmUtil.sendDirectionsEmail(bundle);
	}

	/**
	 * Performs a call to update the user's bank session.
	 */
	public static void executeSessionRefreshCall() {
		BankServiceCallFactory.createRefreshSessionCall().submit();
	}

	/**
	 * Navigate to a specific FAQ page
	 */
	public static void navigateToSpecificFaq(final String faqType) {
		final Bundle extras = new Bundle();
		extras.putString(BankExtraKeys.FAQ_TYPE, faqType);

		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		if(currentActivity instanceof BankNavigationRootActivity) {
			final FAQDetailFragment faqSection = new FAQDetailFragment();
			faqSection.setArguments(extras);
			final NavigationRootActivity navRoot = (NavigationRootActivity)DiscoverActivityManager.getActiveActivity();
			navRoot.makeFragmentVisible(faqSection);
		}else{
			final Intent loggedOutFAQ = new Intent(currentActivity, LoggedOutFAQActivity.class);
			loggedOutFAQ.putExtras(extras);
			currentActivity.startActivity(loggedOutFAQ);
		}
	}

	/**
	 * Logs the user out of Bank and requests Card to do the same.
	 */
	public static void logoutUser(final Activity activeActivity) {
		Globals.setLoggedIn(false);
		KeepAlive.setBankAuthenticated(false);
		Globals.setCurrentUser("");
		BankUser.instance().clearSession();
		BankConductor.navigateToLoginPage(activeActivity, IntentExtraKey.SESSION_EXPIRED, null);
		final ErrorHandlerUi uiHandler = (ErrorHandlerUi) DiscoverActivityManager.getActiveActivity();
		FacadeFactory.getCardLogoutFacade().logout(activeActivity, uiHandler);
	}

	/**
	 * For any navigation patterns using abstract pattern,
	 * supply json class required for given
	 */
	@Override
	public Class lookupCacheRequiredForDestination(final Class destination) {
		Class payloadClass = null;
		if(destination == BankSelectPayee.class){
			payloadClass = ListPayeeDetail.class;
		}
		return payloadClass;
	}

	/**
	 * Navigate to the frequency widget so that the user can chose a frequency for the
	 * funds transfer.
	 */
	public static void navigateToFrequencyWidget(final Bundle args) {
		final BankTransferFrequencyWidget widget = new BankTransferFrequencyWidget();
		widget.setArguments(args);
		((BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity()).makeFragmentVisible(widget);

	}

	/**
	 * Navigate back to the funds transfer step one with the data the the user chose
	 * @param bundle - bundle containing the data the the user chose
	 */
	public static void navigateBackFromTransferWidget(final Bundle bundle) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		activity.onBackPressed();
		((BankTransferStepOneFragment) activity.getCurrentContentFragment()).handleChosenFrequency(bundle);
	}

	public static void navigateBackFromTransferSelectAccount(final Bundle bundle) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		activity.popTillFragment(BankTransferStepOneFragment.class);
		((BankTransferStepOneFragment) activity.getCurrentContentFragment()).handleChosenAccount(bundle);
	}

	/**
	 * Method to navigate to Privacy and Terms page. Specify which page to load via the type parameter.
	 * If user is not logged in and Privacy and Terms is not already displayed, then this method will 
	 * launch an activity with the Privacy and Terms landing page.
	 * 
	 * @param type Enum type that specifies which Privacy and Terms page to display on the active Navigation Activity.
	 */
	public static void navigateToPrivacyTerms(final PrivacyTermsType type) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		if( activity != null ) {
			/**Launch Privacy & Terms Activity if user is not logged in*/
			if( activity instanceof LoginActivity ) {
				final Intent intent = new Intent(activity, BankInfoNavigationActivity.class);
				final Bundle bundle = new Bundle();
				bundle.putSerializable(BankInfoNavigationActivity.PRIVACY_AND_TERMS, type);
				activity.startActivity(intent);
				activity.finish();
			} 			
			/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
			else if( activity instanceof NavigationRootActivity ) {
				final NavigationRootActivity navActivity = (NavigationRootActivity) activity;

				BaseFragment fragment = navActivity.getCurrentContentFragment();
				boolean continueNavigation = false;

				/** Check whether to continue with navigation to Privacy and terms or not*/
				if( navActivity instanceof BankNavigationRootActivity ) {
					/**
					 * Show requested page if no fragment is present, user is not already in privacy terms view, or if 
					 * user is already viewing privacy and terms, they are not requesting to view the landing page again
					 * */
					continueNavigation = (fragment == null ||
							fragment.getGroupMenuLocation() != BankMenuItemLocationIndex.PRIVACY_AND_TERMS_GROUP ||
							(fragment.getGroupMenuLocation() == BankMenuItemLocationIndex.PRIVACY_AND_TERMS_GROUP &&
							fragment instanceof TermsLandingPageFragment &&
							type != PrivacyTermsType.LandingPage));
				} else {
					continueNavigation = true;
				}

				if( continueNavigation ) {
					/**Must specify what page to show to the BankPrivacyTermsFragment*/
					final Bundle bundle = new Bundle();
					
					/**Display Landing Page for Privacy and Terms*/
					if( type == PrivacyTermsType.LandingPage ) {
						fragment = new TermsLandingPageFragment();
					} else {
						/**Check if Google Terms of Use is being displayed */
						if( type == PrivacyTermsType.GoogleTermsOfUse) {
							bundle.putSerializable(BankTextViewFragment.KEY_TEXT, GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(activity));
							fragment = new BankTextViewFragment();
							fragment.setArguments(bundle);
						} else {	
							bundle.putSerializable(BankPrivacyTermsFragment.KEY_TERMS_TYPE, type);
							
							fragment = new BankPrivacyTermsFragment();
							fragment.setArguments(bundle);
						}
					}

					navActivity.makeFragmentVisible(fragment);	
				} else {
					navActivity.hideSlidingMenuIfVisible();
				}
			}
		}
	}


	/**
	 * Method used to navigate to Contact Us Page. The Contact US page can show both
	 * card and bank information, card only or bank only information. The type is used
	 * to specify what information is shown.
	 * 
	 * @param type Used to specify what contact us information is displayed.
	 */
	public static void navigateToContactUs(final ContactUsType type ) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankInfoNavigationActivity.CONTACT_US, type);

		if( activity != null ) {
			/**Launch Privacy & Terms Activity if user is not logged in*/
			if( activity instanceof LoginActivity ) {
				final Intent intent = new Intent(activity, BankInfoNavigationActivity.class);
				intent.putExtras(bundle);
				activity.startActivity(intent);
				activity.finish();
			} 			
			/**Verify that the user is logged in and the NavigationRootActivity is the active activity*/
			else if( activity instanceof NavigationRootActivity ) {
				final NavigationRootActivity navActivity = (NavigationRootActivity) activity;

				BaseFragment fragment = navActivity.getCurrentContentFragment();

				if( !BankNavigationHelper.isViewingMenuSection( BankMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP, 
						BankMenuItemLocationIndex.CONTACT_US_SECTION)) {

					fragment = new CustomerServiceContactsFragment();
					fragment.setArguments(bundle);

					navActivity.makeFragmentVisible(fragment);		
				} else {
					navActivity.hideSlidingMenuIfVisible();
				}
			}
		}
	}
}

