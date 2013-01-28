package com.discover.mobile.error;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.LogOutCall;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.Navigator;

/**
 * 1) include context here so that this class can invoke the error modal dialog.
 * 2) request from Jon the text that needs to go in here for when the user lost connection.
 * 3) link this into base builder so that it is by default the exception failure handler. we 
 *    should not need more than one implementation here for 99% of scenarios. 
 * 4) invoke the error dialog for "generic error" for any runtime or other error. invoke the 
 *    "you've lost connection" error dialog for the checkConnection issue.
 *    make this listener the default in the base builder please.
 * 
 * @author henryoyuela
 *
 */
public class BaseExceptionFailureHandler implements ExceptionFailureHandler {
	public BaseExceptionFailureHandler(){

	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public boolean handleFailure(Throwable arg0, final NetworkServiceCall<?> networkServiceCall) {	
		//Check if this is an exception that occured to pre-auth
		if( networkServiceCall instanceof PreAuthCheckCall) {
			LoginActivity loginActivity = (LoginActivity)ErrorHandlerFactory.getActiveActivity();
			loginActivity.showSplashScreen(false);
		} else if (!(networkServiceCall instanceof LogOutCall)) {
			ErrorHandlerFactory.getInstance().handleGenericError(0);
		}else {
			Navigator.navigateToLoginPage(ErrorHandlerFactory.getActiveActivity(), IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE);
		}
		
		return true;
	}

}
