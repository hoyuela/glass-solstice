/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import android.app.Activity;

import com.discover.mobile.bank.BankPhoneAsyncCallbackBuilder;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.logout.BankLogOutCall;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.BankLogoutFacade;
import com.discover.mobile.common.logout.LogOutSuccessFailListener;

/**
 * @author ekaram
 *
 */
public class BankLogoutFacadeImpl implements BankLogoutFacade{

	@Override
	public void logout(final Activity fromActivity, final ErrorHandlerUi errorUi){
		try {
			final AsyncCallback<Object> callback = BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(Object.class, fromActivity, errorUi, true)
			.withSuccessListener(new LogOutSuccessFailListener(fromActivity))
			.withErrorResponseHandler(new LogOutSuccessFailListener(fromActivity))
			.build();

			new BankLogOutCall(fromActivity, callback).submit();
		} catch( final Exception ex ) {
			//Make sure to set the logged out state of the application to false
			Globals.setLoggedIn(false);
			//Clear the sesssion data for the bank user
			BankUser.instance().clearSession();
			//Navigate to the login screen
			BankConductor.navigateToLoginPage(fromActivity, IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, null);
		}
			
	}

	
	
	
}
