package com.discover.mobile.bank.error;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.services.auth.PreAuthCheckCall;
import com.discover.mobile.bank.services.logout.BankLogOutCall;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;
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

	public BankExceptionHandler() {

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

		// If exception occurred because of a pre-auth call just continue to the login page
		if ( sender instanceof PreAuthCheckCall ) {
			final LoginActivity loginActivity = (LoginActivity) DiscoverActivityManager
					.getActiveActivity();
			loginActivity.showSplashScreen(false);
		}
		// If exception occurred because of a logout service call just navigate to login page
		else if ( sender instanceof BankLogOutCall ) {
			BankNavigator.navigateToLoginPage(
					DiscoverActivityManager.getActiveActivity(),
					IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE);
		}
		//Catch-all exception handler
		else {
			BankErrorHandler.getInstance().handleGenericError(0);
		}

		//Must return true to let GenericAsyncCallback to stop calling any further listeners
		return true;
	}
}
