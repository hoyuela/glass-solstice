/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.CardLogoutFacade;
import com.discover.mobile.common.logout.LogOutSuccessFailListener;

import com.discover.mobile.card.CardAsyncCallbackBuilder;
import com.discover.mobile.card.services.logout.CardLogOutCall;

/**
 * @author ekaram
 *
 */
public class CardLogoutFacadeImpl implements CardLogoutFacade{

	public void logout(Activity fromActivity, ErrorHandlerUi errorUi){
		
			final AsyncCallback<Object> callback = CardAsyncCallbackBuilder.createDefaultCallbackBuilder(Object.class, fromActivity, errorUi, true)
			.withSuccessListener(new LogOutSuccessFailListener(fromActivity))
			.withErrorResponseHandler(new LogOutSuccessFailListener(fromActivity))
			.withExceptionFailureHandler(new BaseExceptionFailureHandler())
			.showProgressDialog("Discover", "Signing Out...", true)
			//.withCompletionListener(new LockScreenCompletionListener(fromActivity))
			.build();

			new CardLogOutCall(fromActivity, callback).submit();
	}

//	public void logout(Activity fromActivity,ErrorHandlerUi errorUi) {
//		new CardLogoutAsyncTask((Context)fromActivity).execute();
//		
//	}
	
}
