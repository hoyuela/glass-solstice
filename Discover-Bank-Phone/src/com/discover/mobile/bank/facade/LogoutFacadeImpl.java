/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import android.app.Activity;

import com.discover.mobile.bank.BankPhoneAsyncCallbackBuilder;
import com.discover.mobile.bank.logout.LogOutSuccessFailListener;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.auth.LogOutCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.LockScreenCompletionListener;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.LogoutFacade;

/**
 * @author ekaram
 *
 */
public class LogoutFacadeImpl implements LogoutFacade{

	//FIXME logout should have a process where both "sides" are invoked, not just bank or card
	public void logout(Activity fromActivity, ErrorHandlerUi errorUi, AccountType accountType){
		boolean isCard = (accountType == AccountType.CARD_ACCOUNT);
		final AsyncCallback<Object> callback = BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(Object.class, fromActivity, errorUi, true)
				.withSuccessListener(new LogOutSuccessFailListener(fromActivity))
				.withErrorResponseHandler(new LogOutSuccessFailListener(fromActivity))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.withCompletionListener(new LockScreenCompletionListener(fromActivity))
				.build();

		new LogOutCall(fromActivity, callback, isCard).submit();
	}

	
	
	
}
