/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import android.app.Activity;

import com.discover.mobile.bank.BankPhoneAsyncCallbackBuilder;
import com.discover.mobile.bank.services.logout.BankLogOutCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.LockScreenCompletionListener;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.BankLogoutFacade;
import com.discover.mobile.common.logout.LogOutSuccessFailListener;

/**
 * @author ekaram
 *
 */
public class BankLogoutFacadeImpl implements BankLogoutFacade{

	public void logout(Activity fromActivity, ErrorHandlerUi errorUi){
		
			final AsyncCallback<Object> callback = BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(Object.class, fromActivity, errorUi, true)
			.withSuccessListener(new LogOutSuccessFailListener(fromActivity))
			.withErrorResponseHandler(new LogOutSuccessFailListener(fromActivity))
			.withExceptionFailureHandler(new BaseExceptionFailureHandler())
			.withCompletionListener(new LockScreenCompletionListener(fromActivity))
			.build();

			new BankLogOutCall(fromActivity, callback).submit();
			
	}

	
	
	
}
