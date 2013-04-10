package com.discover.mobile.bank.error;

import android.util.Log;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.services.BackgroundServiceCall;
import com.discover.mobile.bank.services.BankApiServiceCall;
import com.discover.mobile.bank.services.auth.PreAuthCheckCall;
import com.discover.mobile.bank.services.auth.RefreshBankSessionCall;
import com.discover.mobile.bank.services.deposit.SubmitCheckDepositCall;
import com.discover.mobile.bank.services.logout.BankLogOutCall;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * Class is a generic exception handler for NetworkServiceCall<>. There are
 * three cases supported by this ExceptionHandler. If there is an exception
 * during a PreAuthCheckCall then the application is sent to the Login Page, if
 * there is an exception during a LogOutCall then it is handled as if logout was
 * successful, any other exception displays the catch-all modal notifying the
 * user that their request could not be processed. Exception can occur for a
 * NetworkServiceCall<> when sending a request or when processing a response.
 * 
 * @author henryoyuela
 * 
 */
public class BankExceptionHandler extends BaseExceptionFailureHandler {	
	private Throwable lastThrowable;
	private NetworkServiceCall<?> lastSender;
	private static final BankExceptionHandler instance = new BankExceptionHandler();
	private static final String TAG = BankExceptionHandler.class.getSimpleName();
	
	private BankExceptionHandler() {

	}
	
	/**
	 * @return Returns reference to the singleton instance of this class
	 */
	public static BankExceptionHandler getInstance() {
		return instance;
	}

	@Override
	public CallbackPriority getCallbackPriority() {
		/*
		 * Should be the last Exception Handler processed to allow other
		 * exception handler to be handled
		 */
		return CallbackPriority.LAST;
	}

	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender,
			final Throwable arg0) {

		/**Store the last exception and the last network service call that caused the exception*/
		lastThrowable = arg0;
		lastSender = sender;
		
		//Check if exception should be handled
		if( isConsiderable(sender) ) {
			// If exception occurred because of a pre-auth call just continue to the login page
			if ( sender instanceof PreAuthCheckCall ) {
				final LoginActivity loginActivity = (LoginActivity) DiscoverActivityManager
						.getActiveActivity();
				loginActivity.showSplashScreen(false);
			}
			// If exception occurred because of a logout service call just navigate to login page
			else if ( sender instanceof BankLogOutCall ) {
				BankConductor.navigateToLoginPage(
						DiscoverActivityManager.getActiveActivity(),
						IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE,
						null);
			}
			// Need to inform KeepAlive to keep attempting refresh calls upon a failure.
			else if (sender instanceof RefreshBankSessionCall) {
				KeepAlive.resetLastBankRefreshTime();
			}
			//Catch-all exception handler
			else  {
				BankErrorHandler.getInstance().handleGenericError(0);
			}
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Error has been ignored");
			}
		}

		//Must return true to let GenericAsyncCallback to stop calling any further listeners
		return true;
	}
	
	/**
	 * Method used to check to see if the network service call should be handled for an exception
	 * 
	 * @param service Service to be check to see whether it should be handled or not
	 * 
	 * @return True to handle exception, false otherwise
	 */
	public boolean isConsiderable( final NetworkServiceCall<?> service ) {
		boolean ret =  !( service instanceof SubmitCheckDepositCall || service instanceof BankApiServiceCall);
		
		if( service instanceof BackgroundServiceCall ) {
			ret |= !(((BackgroundServiceCall)service).isBackgroundCall());
 		}
		
		return ret;
	}
	
	/**
	 * @return Reference to last exception that was raised by a network service call sent by the application
	 */
	public Throwable getLastException() {
		return lastThrowable;
	}
	
	/**
	 * @return Reference to the service call that caused the last exception that occurred in the application.
	 */
	public NetworkServiceCall<?> getLastSender() {
		return lastSender;
	}
	
	/**
	 * Clears the last exception that occured in the application because of a network service call.
	 */
	public void clearLastException() {
		lastThrowable = null;
		lastSender = null;
	}
}
