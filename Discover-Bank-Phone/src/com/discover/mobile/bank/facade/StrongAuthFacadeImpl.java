/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.discover.mobile.bank.BankPhoneAsyncCallbackBuilder;
import com.discover.mobile.bank.security.EnhancedAccountSecurityActivity;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.bank.services.auth.strong.CreateStrongAuthRequestCall;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.StrongAuthFacade;

/**
 * @author ekaram
 * 
 */
public class StrongAuthFacadeImpl implements StrongAuthFacade {

	public void navToCardStrongAuth(Context context, String strongAuthQuestion, String strongAuthQuestionId) {
		final Intent strongAuth = new Intent(context, EnhancedAccountSecurityActivity.class);

		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, strongAuthQuestion);
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, strongAuthQuestionId);

		context.startActivity(strongAuth);

	}

	/**
	 * Used to construct a CreateStrongAuthRequestCall NetworkServiceCall for
	 * invoking the Bank - Authentication Service API found at
	 * ./api/auth/strongauth. The CreateStrongAuthRequestCall created by this
	 * method is used to download a question and its id. The callee will only
	 * have to call submit on the constructed object to trigger the HTTP
	 * request.
	 * 
	 * @return Reference to the created CreateStrongAuthRequestCall.
	 */
	@Override
	public void navToBankStrongAuth(Context arg0) {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**
		 * Create an AsyncCallback using the default builder created for Bank
		 * related web-service HTTP requests
		 */
		final AsyncCallback<BankStrongAuthDetails> callback = BankPhoneAsyncCallbackBuilder.
				createDefaultCallbackBuilder(BankStrongAuthDetails.class, activity, (ErrorHandlerUi) activity).build();

		new CreateStrongAuthRequestCall(arg0, callback).submit();
	}

	

}
