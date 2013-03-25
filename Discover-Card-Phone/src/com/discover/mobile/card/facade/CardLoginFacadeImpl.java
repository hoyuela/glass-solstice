/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;
import android.content.Context;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.auth.AuthenticateCall;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.facade.CardLoginFacade;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * The impl class for the card nav facade 
 * @author ekaram
 *
 */
public class CardLoginFacadeImpl implements CardLoginFacade{

	@Override
	public void login(final LoginActivityInterface callingActivity, final String username, final String password) {
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
				.withErrorResponseHandler(new com.discover.mobile.card.error.CardBaseErrorResponseHandler(callingActivity))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.build();

		new AuthenticateCall((Context) callingActivity, callback, username, password).submit();
		
	}

	@Override
	public void loginWithPayload(final LoginActivityInterface callingActivity, final String tokenValue, final String hashedTokenValue) {
		final AsyncCallback<BankPayload> callback = GenericAsyncCallback
				.<BankPayload> builder((Activity) callingActivity)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(new SuccessListener<BankPayload>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final NetworkServiceCall<?> sender, final BankPayload value) {
						// Continues the SSO daisy chain by returning control back to Bank.
						FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(value.payload);

					}
				})
				.withErrorResponseHandler(new com.discover.mobile.card.error.CardBaseErrorResponseHandler(callingActivity))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.build();

		new SSOAuthenticateCall((Context) callingActivity, callback, tokenValue, hashedTokenValue).submit();
	}

	@Override
	public void toggleLoginToBank() {
		// TODO Card needs to contact their end-point to get a Bank payload.

		// TODO This returned payload is sent to the following place which
		// should handle the rest.
		FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(
				"The payload string here");
	}

	@Override
	public void toggleToCard(Context arg0) {
		// TODO The Card side was already authenticated during Bank Login and
		// kept alive via refresh calls.

	}
	
}
