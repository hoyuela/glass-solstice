package com.discover.mobile.bank.framework;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.discover.mobile.analytics.BankTrackingHelper;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.AccountActivityViewPager;
import com.discover.mobile.bank.auth.strong.EnhancedAccountSecurityActivity;
import com.discover.mobile.bank.deposit.BankDepositWorkFlowStep;
import com.discover.mobile.bank.error.BankBaseErrorResponseHandler;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.payees.BankAddPayeeConfirmFragment;
import com.discover.mobile.bank.services.AcceptTermsService;
import com.discover.mobile.bank.services.BackgroundServiceCall;
import com.discover.mobile.bank.services.BankApiServiceCall;
import com.discover.mobile.bank.services.BankHolidayServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.account.GetCustomerAccountsServerCall;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ActivityDetailType;
import com.discover.mobile.bank.services.account.activity.GetActivityServerCall;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.bank.services.atm.GetAtmDetailsCall;
import com.discover.mobile.bank.services.atm.GetDirectionsServiceCall;
import com.discover.mobile.bank.services.atm.GetLocationFromAddressServiceCall;
import com.discover.mobile.bank.services.auth.BankSchema;
import com.discover.mobile.bank.services.auth.CreateBankLoginCall;
import com.discover.mobile.bank.services.auth.CreateBankSSOLoginCall;
import com.discover.mobile.bank.services.auth.RefreshBankSessionCall;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.auth.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.bank.services.customer.CustomerServiceCall;
import com.discover.mobile.bank.services.deposit.GetAccountLimits;
import com.discover.mobile.bank.services.error.BankErrorSSOResponse;
import com.discover.mobile.bank.services.logout.BankLogOutCall;
import com.discover.mobile.bank.services.payee.AddPayeeServiceCall;
import com.discover.mobile.bank.services.payee.DeletePayeeServiceCall;
import com.discover.mobile.bank.services.payee.GetPayeeServiceCall;
import com.discover.mobile.bank.services.payee.ManagePayeeServiceCall;
import com.discover.mobile.bank.services.payee.SearchPayeeResultList;
import com.discover.mobile.bank.services.payee.SearchPayeeServiceCall;
import com.discover.mobile.bank.services.payment.CreatePaymentCall;
import com.discover.mobile.bank.services.payment.DeletePaymentServiceCall;
import com.discover.mobile.bank.services.payment.GetPayBillsTermsAndConditionsCall;
import com.discover.mobile.bank.services.payment.GetPaymentsServiceCall;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.bank.services.payment.UpdatePaymentCall;
import com.discover.mobile.bank.services.transfer.DeleteTransferServiceCall;
import com.discover.mobile.bank.services.transfer.GetExternalTransferAccountsCall;
import com.discover.mobile.bank.services.transfer.ScheduleTransferCall;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.SyncedActivity;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.StartListener;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.framework.NetworkServiceCallManager;
import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.google.common.base.Strings;

/**
 * Class is used to maintain state of NetworkServiceCall<> requests and responses in a single area in the application. This
 * class follows a singleton design pattern and its single instance is added as a listener to each GenericAsyncCallback<> 
 * built and associated with a NetworkServiceCall<> via the AsynCallbackBuilderLibrary. In addition, 
 * this class manages Strong Authentication
 * challenges when received as a resposne to a NetworkServiceCall<>.
 *
 * @author henryoyuela
 *
 */
public final class BankNetworkServiceCallManager extends NetworkServiceCallManager implements StartListener, 
																							  SuccessListener<Serializable>,
ErrorResponseHandler, ExceptionFailureHandler, CompletionListener, Observer {
	/**
	 * Used to print logs into Android logcat
	 */
	private static final String TAG = "NeServiceCallManager";
	private static final String GUI_NOT_READY = "GUI is not ready, process response async";
	/**
	 * Holds a reference to the Previous NetworkServiceCall<> sent out by the application, used to
	 * retransmit a NetworkServiceCall<> when required. Set in the start() method implementation
	 * each time a NetworkServiceCall<> is made.
	 */
	private NetworkServiceCall<?> prevCall;
	/**
	 * Holds a reference to the Current NetworkServiceCall<> being processed by the application, used to
	 * keep context of the state of the application with respect to NetworkServiceCalls. Set in the start()
	 * method implementation each time a NetworkServiceCall<> is made.
	 */
	private NetworkServiceCall<?> curCall;
	/**
	 * Holds reference to an instance of BankBaseErrorResponseHandler used to handle failures when a NetworkServiceCall
	 * fails.
	 */
	private BankBaseErrorResponseHandler errorHandler;
	/**
	 * Holds Reference to Singleton instance of NetworkServiceCallManager
	 */
	private static final BankNetworkServiceCallManager instance = new BankNetworkServiceCallManager();


	/**
	 * Constructor is made private to follow a singleton design pattern.
	 */
	private BankNetworkServiceCallManager() {
		errorHandler = new BankBaseErrorResponseHandler((ErrorHandlerUi) DiscoverActivityManager.getActiveActivity());
		DiscoverActivityManager.addListener(this);
	}

	/**
	 * @return Returns the priority that should be assigned to NetworkServiceCallManager within the
	 * GenericAsyncCallback<> priority queue when processing an incoming response to a NetworkServiceCall.
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	/**
	 *
	 * @return Returns Singleton instance of NetworkServiceCallManager
	 */
	public static BankNetworkServiceCallManager getInstance() {

		return instance;
	}

	/**
	 * Initializes a new error handler based on the current Activity.
	 */
	public void resetErrorHandler() {
		errorHandler = new BankBaseErrorResponseHandler((ErrorHandlerUi) DiscoverActivityManager.getActiveActivity());
	}

	/**
	 * Determines whether the ErrorResponse object is a StrongAuth challenge.
	 *
	 * @param error Reference to an error provided via a response to a NetworkServiceCall<>
	 * @return True if it is a StrongAuth Challenge, false otherwise.
	 */
	public boolean isStrongAuthChallenge(final ErrorResponse<?> error ) {
		boolean ret = false;

		final int httpErrorCode = error.getHttpStatusCode();
		final HttpURLConnection conn = error.getConnection();

		if( httpErrorCode ==  HttpURLConnection.HTTP_UNAUTHORIZED ) {
			final String wwwAuthenticateValue = conn.getHeaderField(HttpHeaders.Authentication);

			if( !Strings.isNullOrEmpty(wwwAuthenticateValue) ) {
				//Check if strong auth challenge
				if( wwwAuthenticateValue.contains(BankSchema.BANKSA)) {
					ret = true;
				}
			}

		}

		return ret;
	}

	/**
	 * Determines if the ErrorResponse was a signal to authenticate against card
	 * (for SSO).
	 *
	 * @param error
	 *            Reference to an error provided via a response to a
	 *            NetworkServiceCall<>
	 * @return true if it was an SSO user, false otherwise.
	 */
	public boolean isSSOUser(final ErrorResponse<?> error) {
		boolean isSSO = false;

		final int httpErrorCode = error.getHttpStatusCode();
		final HttpURLConnection conn = error.getConnection();

		if( httpErrorCode ==  HttpURLConnection.HTTP_UNAUTHORIZED ) {
			final String wwwAuthenticateValue = conn.getHeaderField(HttpHeaders.Authentication);

			if( !Strings.isNullOrEmpty(wwwAuthenticateValue) ) {
				//Check if SSO by having CardAuth
				if( wwwAuthenticateValue.contains(BankSchema.CARDAUTH)) {
					isSSO = true;
				}
			}

		}
		return isSSO;
	}

	/**
	 * Determines if the error was from a bank session refresh. If so, it means that the user's session is no longer alive.
	 *
	 * @param error
	 *            Reference to an error provided via a response to a
	 *            NetworkServiceCall<>
	 * @return true if it was a refresh call and it's now dead, false otherwise.
	 */
	public boolean isSessionDead(final ErrorResponse<?> error) {
		boolean isDead = false;

		final int httpErrorCode = error.getHttpStatusCode();
		final HttpURLConnection conn = error.getConnection();

		if( httpErrorCode ==  HttpURLConnection.HTTP_UNAUTHORIZED ) {
			final String url = conn.getURL().toString();

			if(!Strings.isNullOrEmpty(url)) {
				if( url.contains(BankUrlManager.getRefreshSessionUrl())) {
					isDead = true;
				}
			}
		}
		return isDead;
	}

	/**
	 * Method defines the implementation of the handleFailure callback defined by ErrorResponseHandler.
	 * Used to check whether the failure was a result of a StrongAuth challenge, if so then it sends
	 * a GET request to the StrongAuth API, otherwise handles the error via the BankBaseErrorResponseHandler.
	 */
	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender, final ErrorResponse<?> error) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		if( isGuiReady() ) {
			//Check if the screen displaying is a table
			if(activeActivity instanceof BankNavigationRootActivity && 
					((BankNavigationRootActivity)activeActivity).getCurrentContentFragment() instanceof BaseTable){
					final Fragment fragment = ((BankNavigationRootActivity)activeActivity).getCurrentContentFragment();
					((BaseTable) fragment).refreshListener();
			}
			
			//Check if the error is a Strong Auth Challenge
			if( isStrongAuthChallenge(error) && !(sender instanceof CreateStrongAuthRequestCall) ) {
				//Send request to Strong Auth web-service API
				BankServiceCallFactory.createStrongAuthRequest().submit();
			}

			// Check if the error is an SSO User
			else if (isSSOUser(error)) {
				//Set the boolean the the SSO user is trying to login
				BankUser.instance().setSsoUser(true);
				//Hand the authorization over to card so that they can auth
				BankConductor.authWithCardPayload(
						(LoginActivity) activeActivity,
						((BankErrorSSOResponse) error).token,
						((BankErrorSSOResponse) error).hashedValue);
				//Close the dialog after the next call has been submitted.
				((LoginActivity)DiscoverActivityManager.getActiveActivity()).closeDialog();
			}
			// Check if the error was a Ping. if so, then session died.
			else if (isSessionDead(error)) {
				BankConductor.logoutUser(activeActivity);
			}
			//Dispatch response to BankBaseErrorHandler to determine how to handle the error
			else if( !isBackgroundServiceCall(sender) )  {
				((AlertDialogParent)activeActivity).closeDialog();
				
				errorHandler.handleFailure(sender, error);
			}
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, GUI_NOT_READY);
			}

			handleResponseAsync( new NetworkServiceCallAsyncArgs(sender, error));
		}

		return true;
	}

	/**
	 * Method defines the implementation of the handleFailure callback defined by ExceptionFailureHandler.
	 * Used to close any progress dialog being displayed on the active activity because of a NetworkServiceCall<>.
	 */
	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender, final Throwable arg1) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();
		if( isGuiReady() ) {
			//Check if the screen displaying is a table
			if(activeActivity instanceof BankNavigationRootActivity && 
					((BankNavigationRootActivity)activeActivity).getCurrentContentFragment() instanceof BaseTable){
					final Fragment fragment = ((BankNavigationRootActivity)activeActivity).getCurrentContentFragment();
					((BaseTable) fragment).refreshListener();
			}
			
			if( !isBackgroundServiceCall(sender) ) {
				((AlertDialogParent)activeActivity).closeDialog();
			}
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, GUI_NOT_READY);
			}

			handleResponseAsync( new NetworkServiceCallAsyncArgs(sender, arg1));
		}

		return false;
	}

	/**
	 * FIXME: This will need to be implemented properly
	 * @return
	 */
	protected boolean customHandleSuccessResult(final NetworkServiceCall<?> sender, 
												final Serializable result, 
												final Bundle bundle){
		this.success(sender,result);
		return true;
	}

	/**
	 * Method defines the implementation of the success callback defined by SuccessListener.
	 * called by NetworkServiceCall<> via a GenericAsyncCallback<> when a successful response
	 * to an HTTP request has been received. NetworkServiceCallManager uses this method
	 * to control any navigation or retransmission of a NetworkServiceCall<>.
	 */
	@Override
	public void success(final NetworkServiceCall<?> sender, final Serializable result) {
		/**Check if UI is ready to process the incoming response*/
		if( isGuiReady() ) {
			handleSuccessSync(sender, result);
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, GUI_NOT_READY);
			}

			handleResponseAsync( new NetworkServiceCallAsyncArgs(sender, result));
		}
	}

	/**
	 * Method for handling a successful NetworkServiceCall<?> response.
	 */
	public void handleSuccessSync(final NetworkServiceCall<?> sender, final Serializable result) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		//A successful call refreshes the session -- update KeepAlive service with information.
		KeepAlive.updateLastBankRefreshTime();

		// FIRST UPDATE GLOBAL CACHE IF DESIRED
		if ( sender.cacheResults() ) {
			BankUser.instance().updateCache(result);
		}

		//If Strong Auth Activity is open close it only in the case when the user is NOT logging in
		if( !(sender instanceof CreateStrongAuthRequestCall) && //Shouldn't close strong auth page on a strong auth success
				!(sender instanceof CustomerServiceCall) &&				//Customer Service call is only made when logging in
				!(sender instanceof GetCustomerAccountsServerCall) &&       //Account Download is only made when logging in
				(DiscoverActivityManager.getActiveActivity() instanceof EnhancedAccountSecurityActivity)) {
			//Bring Navigation Root Activity to the foreground, to allow to switch fragments on it
			BankConductor.navigateToHomePage();

			//Handle success for a service call after Navigation root activity is in the foreground.
			//Cannot swap fragments on the navigation root activity because it is not in the foreground
			//during a strong auth or after. Have to wait for navigation root to come to foreground first.
			handleSuccessLater(sender, result);
		}
		//Download Customer Information if a Login call is successful
		else if( (sender instanceof CreateBankLoginCall) || (sender instanceof CreateBankSSOLoginCall)) {
			final Activity activity = DiscoverActivityManager.getActiveActivity();

			KeepAlive.setBankAuthenticated(true);

			//Set logged in to be able to save user name in persistent storage
			Globals.setLoggedIn(true);

			//Update current account based on user logged in and account type
			if(activity instanceof LoginActivity){
				((LoginActivity)activity).updateAccountInformation(AccountType.BANK_ACCOUNT);
			}

			BankServiceCallFactory.createCustomerDownloadCall().submit();
		}
		//Download Account Summary Information if a Customer Download is successful
		else if( sender instanceof CustomerServiceCall ) {
			//Verify user has bank accounts otherwise navigate to no accounts page
			if( BankUser.instance().getCustomerInfo().hasAccounts() ) {
				BankServiceCallFactory.createGetCustomerAccountsServerCall().submit();
			} else {
				//Navigate to home page which will open the Open Accounts view page
				BankConductor.navigateToHomePage();
			}
		}
		//If a GET request for terms and conditions succeeds, navigate to the terms and conditions page
		//with an appropriate title from the Fragment that initated the call.
		else if(sender instanceof GetPayBillsTermsAndConditionsCall){
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, result);
			BankConductor.navigateToPayBillsTerms(bundle);
		}
		//If the user accepts the Bank terms and services for pay bills, navigate them to the originally
		//chosen option.
		else if(sender instanceof AcceptTermsService ){
			final AcceptTermsService acceptTerms = (AcceptTermsService)sender;
			if( acceptTerms.getEligibility().isPaymentsEligibility() ) {
				handleAcceptPaymentTerms();
			} else if( acceptTerms.getEligibility().isDepositsEligibility() ) {
				handleAcceptDepositsTerms();
			}
		}
		//Navigate to Account Summary landing page once Account Summary is downloaded
		else if (sender instanceof GetCustomerAccountsServerCall) {
			/** Make sure the service call is not a background service call, indicating it should run silently */
			if (!((GetCustomerAccountsServerCall) sender).isBackgroundCall()) {
				BankConductor.navigateToHomePage();
			}
		}
		//Display StrongAuth Page if it is a response to a StrongAuth GET request with a 
		//question or retansmit previous NetworkServiceCall<>
		else if( sender instanceof CreateStrongAuthRequestCall && prevCall != null && sender.isGetCall()) {
			final BankStrongAuthDetails value = (BankStrongAuthDetails)result;
			if( !BankStrongAuthDetails.ALLOW_STATUS.equals(value.status ) ) {
				//Track that a strong auth was triggered
				BankTrackingHelper.forceTrackPage(R.string.bank_strong_auth);
				BankConductor.navigateToStrongAuth(activeActivity, value, null);
			} else {
				//Retransmit the previous NetworkServiceCall<>
				prevCall.retransmit(activeActivity);
			}
			// Navigate to Payment Confirmation upon a successful payment.
		} else if( (sender instanceof CreatePaymentCall) || (sender instanceof UpdatePaymentCall)) {
			final PaymentDetail value = (PaymentDetail)result;
			if(sender instanceof UpdatePaymentCall){
				BankConductor.navigateToPayConfirmFragment(value, true);
			}else{
				BankConductor.navigateToPayConfirmFragment(value, false);
			}

			//Mark the scheduled activity dirty so that it is refreshed
			final Account account = BankUser.instance().getAccount(value.paymentAccount.id);
			if(null != account){
				account.scheduled = null;
			}
			//Mark the scheduled payments dirty so that it is refreshed
			BankUser.instance().setScheduled(null);
		}
		//Retransmit previous NetworkServiceCall<> if it is a successful response to a StrongAuth POST
		else if( (sender instanceof CreateStrongAuthRequestCall) && (prevCall != null) && sender.isPostCall() ) {
			//Retransmit the previous call that triggered the Strong Auth call
			prevCall.retransmit(activeActivity);
		}
		//Handle the manage payee service call (which is a get payee service call).
		else if( sender instanceof ManagePayeeServiceCall) {
			final Bundle bundle = new Bundle();

			//Update the current list of payees
			BankUser.instance().updateCache(result);
			bundle.putSerializable(BankExtraKeys.PAYEES_LIST, result);

			/**Check to see if the payees were downloaded because of a delete payee call*/
			if( (prevCall instanceof DeletePayeeServiceCall) &&
					!((DeletePayeeServiceCall)prevCall).isHandled()) {
				/**Mark the Service as being handled to avoid this block of code being called again on the next update*/
				((DeletePayeeServiceCall)prevCall).markHandled();

				bundle.putBoolean(BankExtraKeys.CONFIRM_DELETE, true);

				BankConductor.navigateToManagePayee(bundle);

				// Mark the scheduled payments dirty so that it is refreshed
				BankUser.instance().setScheduled(null);

				// Mark the cancelled payment dirty so that it is refreshed
				BankUser.instance().setCancelled(null);
			}
			/**Check to see if the payees were downloaded because of an added payee call*/
			else if( (prevCall instanceof AddPayeeServiceCall) &&
					!((AddPayeeServiceCall)prevCall).isHandled() ) {
				/**Mark the Service as being handled to avoid this block of code being called again on the next update*/
				((AddPayeeServiceCall)prevCall).markHandled();

				BankConductor.navigateToAddPayee(BankAddPayeeConfirmFragment.class, 
																			((AddPayeeServiceCall)prevCall).getResponse());
			}
			/**Catch-all means it was just a payee download*/
			else {
				BankConductor.navigateToManagePayee(bundle);
			}
		}
		else if(sender instanceof GetExternalTransferAccountsCall) {
			final Bundle args = new Bundle();

			//Cache the external accounts in the BankUser singleton class.
			final AccountList externalAccounts = (AccountList)result;
			BankUser.instance().setExternalAccounts(externalAccounts);

			args.putSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS, externalAccounts);
			BankConductor.navigateToTransferMoneyLandingPage(args);
		}
		else if(sender instanceof ScheduleTransferCall) {
			final Bundle resultBundle = new Bundle();
			resultBundle.putSerializable(BankExtraKeys.TRANSFER_SUCCESS_DATA, result);

			final Account currentAccount = BankUser.instance().getCurrentAccount();
			//Mark the scheduled and posted activity dirty so that it is refreshed
			if(currentAccount != null) {
				currentAccount.scheduled = null;
				currentAccount.posted = null;
			}else{
				Log.d(TAG, "Could not mark scheduled and posted activity as dirty");
			}
			//Mark the list of accounts dirty
			BankUser.instance().setAccountOutDated(true);
			BankConductor.navigateToTransferConfirmation(resultBundle);
		}
		//Handle the payee success call
		else if( sender instanceof GetPayeeServiceCall){
			Bundle bundle = ((GetPayeeServiceCall) sender).getExtras();

			/** Check if the payees was downloaded to edit a payment */
			if (bundle != null && bundle.containsKey(BankExtraKeys.EDIT_MODE)) {
				/** close the progress dialog currently being displayed */
				((AlertDialogParent) DiscoverActivityManager.getActiveActivity()).closeDialog();

				BankConductor.navigateToPayBillStepTwo(bundle);
			}
			/** Download Payee to schedule a payment */
			else {
				bundle = new Bundle();
				bundle.putSerializable(BankExtraKeys.PAYEES_LIST, result);
				BankConductor.navigateToSelectPayee(bundle);
			}
		}
		//Handle the get activity service call
		else if( sender instanceof GetActivityServerCall){
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.PRIMARY_LIST, result);
			bundle.putBoolean(BankExtraKeys.CONFIRM_DELETE, ((GetActivityServerCall)sender).getDidDeleteActivity());
			bundle.putBoolean(BankExtraKeys.DID_DELETE_PAYMENT, ((GetActivityServerCall)sender).getDidDeletePayment());
			bundle.putAll(((GetActivityServerCall) sender).getExtras());
			BankConductor.navigateToAccountActivityPage(bundle);
		}
		//Delete Payment Successful, navigate to Review Payments Page
		else if( sender instanceof DeletePaymentServiceCall ) {
			final Bundle deletePaymentBundle = getDeletePaymentBundle(sender);

			if(isCurrentFragmentActivityDetail()) {
				BankConductor.navigateToActivityDetailFromDelete(deletePaymentBundle);
			}else {
				BankConductor.navigateToReviewPaymentsFromDelete(deletePaymentBundle);
			}
		} else if (sender instanceof DeleteTransferServiceCall) {
			//Adds the Activity detail to the Bundle and navigates to the Account Activity page.
			final ActivityDetail detail = ((DeleteTransferServiceCall)sender).getActivityDetail();

			//Marks the internal transfer accounts as dirty.  External accounts will have an account type of null
			Account account = 
					BankUser.instance().getAccount((null != detail.fromAccount.type) ? detail.fromAccount.id : null);
			if (account != null) {
				account.scheduled = null;
			}

			account = BankUser.instance().getAccount((null != detail.toAccount.type) ? detail.toAccount.id : null);
			if (account != null) {
				account.scheduled = null;
			}
			BankUser.instance().getCurrentAccount().scheduled = null;
			BankUser.instance().setScheduled(null);

			//Calls to re-get the activity data because it has been modified.
			final String link = BankUser.instance().getCurrentAccount().getLink(Account.LINKS_SCHEDULED_ACTIVITY);
			BankServiceCallFactory.createGetActivityServerCall(link, ActivityDetailType.Scheduled, true).submit();
		}
		//Payee Search Success, navigate to Add Payee Workflow Step 4
		else if( sender instanceof SearchPayeeServiceCall ) {
			BankConductor.navigateToSelectPayees((SearchPayeeResultList)result);
		}
		//Get Payment Successful, navigate to the review payments table
		else if( sender instanceof GetPaymentsServiceCall ) {
			Bundle bundle;
			if(null != ((GetPaymentsServiceCall) sender).getExtras()){
				bundle = ((GetPaymentsServiceCall) sender).getExtras();
			}else{
				bundle = new Bundle();
			}
			bundle.putSerializable(BankExtraKeys.PRIMARY_LIST, result);
			if(((GetPaymentsServiceCall) sender).isWasDeleted()){
				bundle.putAll(((GetPaymentsServiceCall) sender).getExtras());
			}
			BankConductor.navigateToReviewPaymentsTable(bundle);
		}
		//Payee Add Success, navigate to Add Payee Confirmation Pge in Workflow Step 5
		else if( sender instanceof AddPayeeServiceCall ) {
			/**Update Payee List such that updates the cache, after which it will navigate to confirmation page*/
			BankServiceCallFactory.createManagePayeeServiceRequest().submit();
		}
		//ATM locator service call
		else if(sender instanceof GetAtmDetailsCall){
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, result);
			BankConductor.navigateToAtmLocatorFragment(bundle);
		}
		//Handle reverse geocode call
		else if(sender instanceof GetLocationFromAddressServiceCall){
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, result);
			BankConductor.navigateToSearchAtmLocatorFragment(bundle);
		}
		//Handler for GetAccountLimits service call
		else if( sender instanceof GetAccountLimits) {
			if( !((GetAccountLimits)sender).isBackgroundCall() ) {
				Bundle bundle = activeActivity.getIntent().getExtras();
				boolean navToReview = false;
				if(bundle == null) {
					bundle = new Bundle();
				} else{
					navToReview = bundle.getBoolean(BankExtraKeys.RESELECT_ACCOUNT);
				}
				final Account account = ((GetAccountLimits)sender).getAccount();

				bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
				if(navToReview){
					BankConductor.navigateToCheckDepositWorkFlow(bundle, BankDepositWorkFlowStep.ReviewDeposit);
				} else {
					//Navigate to Check Deposit - Select Amount Page
					BankConductor.navigateToCheckDepositWorkFlow(bundle, BankDepositWorkFlowStep.SelectAmount);
				}

			}
		}
		//Handler for getting email directions
		else if( sender instanceof GetDirectionsServiceCall){
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, result);
			final AtmServiceHelper helper = ((GetDirectionsServiceCall)sender).getHelper();
			bundle.putString(BankExtraKeys.FROM_ADDRESS, helper.getFrom());
			bundle.putString(BankExtraKeys.TO_ADDRESS, helper.getTo());
			BankConductor.navigateToEmailDirections(bundle);
		}
		//Handler for Bank Delete Payee Service Call
		else if( sender instanceof DeletePayeeServiceCall ) {
			//Update bank account to update the balances
			BankUser.instance().refreshBankAccounts();

			//Update list of payees
			BankServiceCallFactory.createManagePayeeServiceRequest().submit();
		}
		// Ignore success
		else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "NetworkServiceCallManager ignored success of a NetworkServiceCall!");
			}
		}
	}

	/**
	 * Method to handle a successful Accept Deposits Terms and Conditions
	 */
	public void handleAcceptDepositsTerms() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();

		/**Remove the Check Deposit Terms and Conditions View from fragment back stack*/
		activity.getSupportFragmentManager().popBackStack();

		/** Add a boolean to the bundle when navigating so we can present the how it works modal*/
		final Bundle termsBundle = new Bundle();
		termsBundle.putBoolean(BankExtraKeys.ACCEPTED_TERMS, true);

		/**Navigates to Select Account Page for Check-Deposit*/
		BankConductor.navigateToCheckDepositWorkFlow(termsBundle, BankDepositWorkFlowStep.SelectAccount);

		/**close the progress dialog created for when service was started to download terms and conditions for deposits*/
		activity.closeDialog();
	}

	/**
	 * Method to handle a successful Accept Payments Terms and Conditions
	 */
	public void handleAcceptPaymentTerms() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		final String currentTitle = activity.getActionBarTitle();
		final String payBills = activity.getString(R.string.section_title_pay_bills);
		final String managePayees = activity.getString(R.string.sub_section_title_manage_payees);

		if(currentTitle.equals(payBills)) {
			BankServiceCallFactory.createGetPayeeServiceRequest().submit();
		} else if(currentTitle.equals(managePayees)) {
			BankServiceCallFactory.createManagePayeeServiceRequest().submit();
		} else{
			final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
			BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
		}
	}

	/**
	 * Method defines the implementation of the start callback defined by StartListener.
	 * Called by NetworkServiceCall<> when submit() is called to start sending an HTTP request.
	 * This methods starts a progress dialog on the active Activity and remembers the previous
	 * NetworkServiceCall<> made in the event it needs to be retransmitted because of a StrongAuth
	 * challenge.
	 */
	@Override
	public void start(final NetworkServiceCall<?> sender) {
		final AlertDialogParent activeActivity = (AlertDialogParent)DiscoverActivityManager.getActiveActivity();

		/* Service calls that do not show dialog must override functionality here */
		if( !isBackgroundServiceCall(sender) ) {

			/**Clear the current last error stored in the error handler*/
			errorHandler.clearLastError();
			
			/**
			 * Update prevCall only if it is a different service request from current call
			 * or if current call is null
			 */
			if( (curCall == null) || (curCall.getClass() != sender.getClass()) ) {
				prevCall = curCall;
			} else {
				if( Log.isLoggable(TAG, Log.WARN)) {
					Log.w(TAG, "Previous NetworkServiceCall was not updated!");
				}
			}

			/**Update current call*/
			curCall = sender;
			
			activeActivity.startProgressDialog(curCall.isCancellable());
		}
	}
	
	/**
	 * 
	 * @return if the current activity is a detail fragment for posted/scheduled activity.
	 */
	private boolean isCurrentFragmentActivityDetail() {		
		boolean isActivityDetail = false;
		final BankNavigationRootActivity navActivity = 
													(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		Fragment currentFragment = null;
		
		if(navActivity != null) {
			currentFragment = navActivity.getCurrentContentFragment();
			isActivityDetail = currentFragment instanceof AccountActivityViewPager;
		}
		
		return isActivityDetail;
	}

	/**
	 * Method used after successfully answering a Strong Auth Challenge Question and receiving a successful response to the
	 * NetworkSerivceCall<> that triggered the Strong Auth Challenge. Must wait for the Strong Auth Activity to close
	 * and the NavigationRootActivity to resume in order to handle the success event, which may require changing fragments on
	 * the NavigationRootActivity.
	 *
	 * @param sender Reference to NetworkServiceCall<> which has received a successful response.
	 * @param result Result from NetworkServiceCall<> response if any
	 */
	private void handleSuccessLater(final NetworkServiceCall<?> sender, final Serializable result) {
		final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			/**
			 * Thid method does not execute in a UI Thread
			 */
			@Override
			protected Void doInBackground(final Void... params) {
				synchronized (instance) {
					/**Wait for Navigation root activity to be brought to the foreground*/
					try {
						instance.wait();
					} catch (final InterruptedException e) {
						if( Log.isLoggable(TAG, Log.ERROR)) {
							Log.e(TAG,"An error occurred while waiting for activities to switch.");
						}
					}
				}
				return null;
			}

			/**
			 * This method is executed in a UI thread after doInBackground has completed.
			 */
			@Override
			protected void onPostExecute(final Void arg) {
				success(sender, result);
			}
		};

		task.execute();
	}

	/**
	 * Returns a Bundle that represents a deleted payment.
	 * @param sender the network service call that will delete the payment.
	 * @return
	 */
	private Bundle getDeletePaymentBundle(final NetworkServiceCall<?>sender) {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(BankExtraKeys.CONFIRM_DELETE, true);
		final PaymentDetail detail = ((DeletePaymentServiceCall)sender).getPaymentDetail();
		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, detail);
		return bundle;
	}
	
	/**
	 * Method defines the implementation of the complete callback defined by CompletionListener.
	 * Called by NetworkServcieCall<> when a request has been completed irrespective of whether the success
	 * passed or failed.
	 */
	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object result) {
		if( sender instanceof BankLogOutCall ) {
			/**Clear all user cached data*/
			BankUser.instance().clearSession();
		}

	}

	/**
	 * Method used to retrieve the last service call made by the application
	 */
	@Override
	public NetworkServiceCall<?> getLastServiceCall() {
		return curCall;
	}

	/**
	 * @return Returns reference to the last error response received for a service call execution.
	 */
	public ErrorResponse<?> getLastError() {
		return errorHandler.getLastError();
	}

	@Override
	protected AccountType getAccountType() {
		return AccountType.BANK_ACCOUNT;
	}

	/**
	 * Method used to check for network connectivity on the device.
	 * 
	 * @return True if the device has a network connection avaiable, false otherwise.
	 */
	public static boolean isNetworkConnected() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		final ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean connected = false;

		// test for connection
		if ( (cm.getActiveNetworkInfo() != null)
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			connected = true;
		}

		return connected;
	}

	/**
	 * Method used to notify this class when the active Activity has changed on the application.
	 */
	@Override
	public void update(final Observable arg0, final Object arg1) {
		synchronized (instance) {
			instance.notifyAll();
		}
	}

	/**
	 * Method used to see if GUI is ready to process an incoming NetworkServiceCall<?> response.
	 *
	 * @return True if Active Activity is in Resume State, false otherwise.
	 */
	private boolean isGuiReady() {
		boolean ret = false;

		final Activity activity = DiscoverActivityManager.getActiveActivity();
		if( (activity != null) && (activity instanceof SyncedActivity)) {
			ret = ((SyncedActivity)activity).isReady();
		}

		return ret;
	}

	/**
	 * Method used to defer the handling of an incoming success response should the GUI not be
	 * ready to process it.
	 *
	 * @param sender NetworkServiceCall whose successful response is to be handled.
	 * @param arguments Object represent the result read from the successful response body.
	 */
	private void handleResponseAsync(final NetworkServiceCallAsyncArgs args) {
		final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			/**
			 * Thid method does not execute in a UI Thread
			 */
			@Override
			protected Void doInBackground(final Void... params) {
				boolean isReady = false;
				final int fiveSeconds = 5000;
				while( !isReady ) {
					final Activity activity = DiscoverActivityManager.getActiveActivity();
					if( (activity != null) && (activity instanceof SyncedActivity)) {
						Log.v("Discover", "Waiting...");
						isReady = ((SyncedActivity)activity).waitForResume(fiveSeconds);
					}
				}
				return null;
			}

			/**
			 * This method is executed in a UI thread after doInBackground has completed.
			 */
			@Override
			protected void onPostExecute(final Void arg) {
				switch( args.result) {
				case Exception:
					handleFailure(args.sender, args.exception);
					break;
				case Failure:
					handleFailure(args.sender, args.error);
					break;
				case Success:
					handleSuccessSync(args.sender, args.arguments);
					break;
				}
			}
		};

		task.execute();
	}

	/**
	 * Method used to check to see if the service call should occur in the background without a progress dialog
	 *
	 * @param sender NetworkServiceCall that is being check to see if it should happen silently.
	 *
	 * @return True if it is a back ground call, false otherwise
	 */
	public boolean isBackgroundServiceCall(final NetworkServiceCall<?> sender) {

		boolean ret = false;

		if( sender != null ) {
			if( sender instanceof BackgroundServiceCall ) {
				ret = ((BackgroundServiceCall)sender).isBackgroundCall();
			}

			ret |=  (sender instanceof RefreshBankSessionCall) ||
					(sender instanceof BankApiServiceCall) ||
					(sender instanceof BankHolidayServiceCall);
		}

		return ret;
	}
	
	public void cancelServiceCall() {
		if(curCall.isCancellable()) {
			curCall.cancel();
		} 
	}

	/**
	 * Enum used to specify whether a NetworkServiceCallAsyncArgs is for a successful, error or exception response to a
	 * network service call.
	 * 
	 * @author henryoyuela
	 * 
	 */
	public enum Result { Success, Failure, Exception };

	/**
	 * Class used to hold information provided via a callback called by a NetworkServiceCall<?>
	 *
	 * @author henryoyuela
	 *
	 */
	public class NetworkServiceCallAsyncArgs {
		private final NetworkServiceCall<?> sender;
		private final Serializable arguments;
		private final Throwable exception;
		private final ErrorResponse<?> error;
		private final Result result;

		/**
		 * Constructor used for a creating an object representing a successful NetworkServiceCall<?>
		 *
		 * @param s NetworkServiceCall that succeeded
		 * @param r Result sent in body of a response to an HTTP request sent via NetworkServiceCall
		 */
		public NetworkServiceCallAsyncArgs(final NetworkServiceCall<?> s, final Serializable r  ) {
			sender = s;
			arguments = r;
			exception = null;
			error = null;
			result = Result.Success;
		}

		/**
		 * Constructor used for a creating an object representing a failed NetworkServiceCall<?> because
		 * of an exception.
		 *
		 * @param s NetworkServiceCall that failed
		 * @param ex Exception cause of the failure
		 */
		public NetworkServiceCallAsyncArgs(final NetworkServiceCall<?> s, final Throwable ex  ) {
			sender = s;
			arguments = null;
			exception = ex;
			error = null;
			result = Result.Exception;
		}

		/**
		 * Constructor used for a creating an object representing a failed NetworkServiceCall<?> because
		 * of an error.
		 *
		 * @param s NetworkServiceCall that succeeded
		 * @param e Error cause of the failure
		 */
		public NetworkServiceCallAsyncArgs(final NetworkServiceCall<?> s, final ErrorResponse<?> e ) {
			sender = s;
			arguments = null;
			exception = null;
			error = e;
			result = Result.Failure;
		}
	}
}
