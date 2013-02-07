package com.discover.mobile.error;

import com.discover.mobile.bank.BankActivityManager;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.LogOutCall;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.login.LoginActivity;

/**
 * Class is a generic exception handler for NetworkServiceCall<>. There are three cases supported by 
 * this ExceptionHandler. If there is an exception during a PreAuthCheckCall then the application is 
 * sent to the Login Page, if there is an exception during a LogOutCall then it is handled as if logout 
 * was successful, any other exception displays the catch-all modal notifying the user that their request 
 * could not be processed. Exception can occur for a NetworkServiceCall<> when sending a request or when
 * processing a response.
 * 
 * @author henryoyuela
 *
 */
public class BaseExceptionFailureHandler implements ExceptionFailureHandler {
	public BaseExceptionFailureHandler(){

	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		/*Should be the last Exception Handler processed to allow other exception handler to be handled*/
		return CallbackPriority.LAST;
	}

	@Override
	public boolean handleFailure(final NetworkServiceCall<?> sender, final Throwable arg0) {	
		//Check if this is an exception that occured to pre-auth
		if( sender instanceof PreAuthCheckCall) {
			final LoginActivity loginActivity = (LoginActivity)BankActivityManager.getActiveActivity();
			loginActivity.showSplashScreen(false);
		} else if (!(sender instanceof LogOutCall)) {
			ErrorHandlerFactory.getInstance().handleGenericError(0);
		}else {
			BankNavigator.navigateToLoginPage(BankActivityManager.getActiveActivity(), IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE);
		}
		
		return true;
	}

}
