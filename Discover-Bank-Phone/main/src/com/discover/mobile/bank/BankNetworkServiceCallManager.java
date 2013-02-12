package com.discover.mobile.bank;

import java.io.Serializable;
import java.net.HttpURLConnection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.services.account.GetCustomerAccountsServerCall;
import com.discover.mobile.bank.services.auth.BankSchema;
import com.discover.mobile.bank.services.auth.CreateBankLoginCall;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.auth.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.bank.services.customer.CustomerServiceCall;
import com.discover.mobile.bank.services.payee.GetPayeeServiceCall;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.StartListener;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.net.HttpHeaders;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.google.common.base.Strings;

/**
 * Class is used to maintain state of NetworkServiceCall<> requests and responses in a single area in the application. This
 * class follows a singleton design pattern and its single instance is added as a listener to each GenericAsyncCallback<> built
 * and associated with a NetworkServiceCall<> via the AsynCallbackBuilderLibrary. In addition, this class manages Strong Authentication
 * challenges when received as a resposne to a NetworkServiceCall<>.
 * 
 * @author henryoyuela
 *
 */
final public class BankNetworkServiceCallManager implements StartListener, SuccessListener<Serializable>,
ErrorResponseHandler, ExceptionFailureHandler {
	/**
	 * Used to print logs into Android logcat
	 */
	private static final String TAG = "NeServiceCallManager";
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
	private final BankBaseErrorResponseHandler errorHandler;
	/**
	 * Holds Reference to Singleton instance of NetworkServiceCallManager
	 */
	private static final BankNetworkServiceCallManager instance = new BankNetworkServiceCallManager();


	/**
	 * Constructor is made private to follow a singleton design pattern.
	 */
	private BankNetworkServiceCallManager() {
		this.errorHandler = new BankBaseErrorResponseHandler((ErrorHandlerUi) DiscoverActivityManager.getActiveActivity());
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
	static public BankNetworkServiceCallManager getInstance() {

		return instance;
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
	 * Method defines the implementation of the handleFailure callback defined by ErrorResponseHandler.
	 * Used to check whether the failure was a result of a StrongAuth challenge, if so then it sends
	 * a GET request to the StrongAuth API, otherwise handles the error via the BankBaseErrorResponseHandler.
	 */
	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender, final ErrorResponse<?> error) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		if( isStrongAuthChallenge(error) && !(sender instanceof CreateStrongAuthRequestCall) ) {
			//Send request to Strong Auth web-service API
			FacadeFactory.getStrongAuthFacade().navToBankStrongAuth(activeActivity);
		} else {
			this.errorHandler.handleFailure(sender, error);

			((AlertDialogParent)activeActivity).closeDialog();
		}

		return true;
	}

	/**
	 * Method defines the implementation of the handleFailure callback defined by ExceptionFailureHandler.
	 * Used to close any progress dialog being displayed on the active activity because of a NetworkServiceCall<>.
	 */
	@Override
	public boolean handleFailure(final NetworkServiceCall<?> arg0, final Throwable arg1) {
		final AlertDialogParent activeActivity = (AlertDialogParent)DiscoverActivityManager.getActiveActivity();
		activeActivity.closeDialog();

		return false;
	}

	/**
	 * Method defines the implementation of the success callback defined by SuccessListener.
	 * called by NetworkServiceCall<> via a GenericAsyncCallback<> when a successful response
	 * to an HTTP request has been received. NetworkServiceCallManager uses this method
	 * to control any navigation or retransmission of a NetworkServiceCall<>.
	 */
	@Override
	public void success(final NetworkServiceCall<?> sender, final Serializable result) {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		//Download Customer Information if a Login call is successful
		if( sender instanceof CreateBankLoginCall ) {
			final LoginActivity activity = (LoginActivity) DiscoverActivityManager.getActiveActivity();

			//Set logged in to be able to save user name in persistent storage
			Globals.setLoggedIn(true);

			//Update current account based on user logged in and account type
			activity.updateAccountInformation(AccountType.BANK_ACCOUNT);

			BankServiceCallFactory.createCustomerDownloadCall().submit();
		}
		//Download Account Summary Information if a Customer Download is successful
		else if( sender instanceof CustomerServiceCall ) {
			//Verify user has bank accounts otherwise navigate to no accounts page
			if( BankUser.instance().getCustomerInfo().hasAccounts() ) {
				BankServiceCallFactory.createGetCustomerAccountsServerCall().submit();
			} else {
				BankNavigator.navigateToNoAccounts();
			}
		}
		//Navigate to Account Summary landing page once Account Summary is downloaded
		else if( sender instanceof GetCustomerAccountsServerCall) {
			BankNavigator.navigateToHomePage(activeActivity);
		}
		//Display StrongAuth Page if it is a response to a StrongAuth GET request with a question or retansmit previous NetworkServiceCall<>
		else if( sender instanceof CreateStrongAuthRequestCall && this.prevCall != null && sender.isGetCall()) {
			final BankStrongAuthDetails value = (BankStrongAuthDetails)result;
			if( !BankStrongAuthDetails.ALLOW_STATUS.equals(value.status ) ) {
				BankNavigator.navigateToStrongAuth(activeActivity, value, null);
			} else {
				//Retransmit the previous NetworkServiceCall<>
				this.prevCall.retransmit(activeActivity);
			}
		}
		//Retransmit previous NetworkServiceCall<> if it is a successful response to a StrongAuth POST
		else if( sender instanceof CreateStrongAuthRequestCall && this.prevCall != null && sender.isPostCall() ) {
			this.prevCall.retransmit(activeActivity);
		}
		//Handle the payee success call
		else if( sender instanceof GetPayeeServiceCall){
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.PAYEES_LIST, result);
			BankNavigator.naviagteToSelectPayee(bundle);
		}
		else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "NetworkServiceCallManager ignored success of a NetworkServiceCall!");
			}
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
		activeActivity.startProgressDialog();

		//Update curCall and prevCall it is a different service request
		if( this.curCall == null || this.curCall.getClass() != sender.getClass() ) {
			this.prevCall = this.curCall;
			this.curCall = sender;
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Current NetworkServiceCall was not updated!");
			}
		}
	}

}
