package com.discover.mobile.card.services.auth.forgot;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.google.common.collect.ImmutableMap;

public class ForgotUserIdCall extends CardJsonResponseMappingNetworkServiceCall<RegistrationConfirmationDetails> {
	
	private final TypedReferenceHandler<RegistrationConfirmationDetails> handler;

	public ForgotUserIdCall(final Context context, final AsyncCallback<RegistrationConfirmationDetails> callback,
			final String accountNumber, final String password) {
		
		super(context, new GetCallParams(CardUrlManager.getForgotUserIdUrl()) {{
			
			final String authString = getAuthorizationString(accountNumber, password);
			headers = ImmutableMap.<String,String>builder()
					.put("Authorization", authString)
					.put("X-Override-UID", "true")
					.put("X-Client-Platform", "Android")
					.build();
			
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
		}}, RegistrationConfirmationDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<RegistrationConfirmationDetails>(callback);
	}
	
	private static String getAuthorizationString(final String username, final String password) {
		final String concatenatedCreds = username + ": :" + password;
		return "DCRDBasic " + Base64.encodeToString(concatenatedCreds.getBytes(), Base64.NO_WRAP);
	}

	@Override
	protected TypedReferenceHandler<RegistrationConfirmationDetails> getHandler() {
		return handler;
	}
	
}
