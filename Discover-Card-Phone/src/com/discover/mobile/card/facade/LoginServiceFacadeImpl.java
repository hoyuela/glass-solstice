/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;
import android.content.Context;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.callback.LockScreenCompletionListener;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.facade.LoginServiceFacade;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * The impl class for the card nav facade 
 * @author ekaram
 *
 */
public class LoginServiceFacadeImpl implements LoginServiceFacade{

	@Override
	public void login(final LoginActivityInterface callingActivity, String username, String password) {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback
				.<AccountDetails> builder((Activity) callingActivity)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(new SuccessListener<AccountDetails>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final NetworkServiceCall<?> sender, final AccountDetails value) {
						// Set logged in to be able to save user name in
						// persistent storage
						Globals.setLoggedIn(true);
						
						callingActivity.updateAccountInformation(AccountType.CARD_ACCOUNT);
					

						CardSessionContext.getCurrentSessionDetails()
								.setAccountDetails(value);

						FacadeFactory.getPushFacade().getXtifyRegistrationStatus((BaseActivity) callingActivity);

					}
				})
				.withErrorResponseHandler(new com.discover.mobile.card.error.CardBaseErrorResponseHandler((ErrorHandlerUi) callingActivity))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.withCompletionListener(new LockScreenCompletionListener((Activity) callingActivity))
				.build();

		new AuthenticateCall((Context) callingActivity, callback, username, password).submit();
		
	}

	

	
	


	
}
