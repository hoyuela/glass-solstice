package com.discover.mobile.card.services.auth;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.google.common.collect.ImmutableMap;

/**
 * Card authenticate call for SSO users using the token and hashed token
 */
public class SSOAuthenticateCall extends CardJsonResponseMappingNetworkServiceCall<BankPayload> {

	@SuppressWarnings("unused")
	private static final String TAG = SSOAuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<BankPayload> handler;

	public SSOAuthenticateCall(final Context context, final AsyncCallback<BankPayload> callback,
			final String tokenValue, final String hashedTokenValue) {
		
		super(context, new GetCallParams(CardUrlManager.getSSOAuthenticateCall()) {{ //$NON-NLS-1$
			final String authString = getAuthorizationString(tokenValue, hashedTokenValue);
			headers = ImmutableMap.<String,String>builder().put("Authorization", authString).build();
			
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
		}}, BankPayload.class);
		
		handler = new StrongReferenceHandler<BankPayload>(callback);
	}
	
	public SSOAuthenticateCall(final Context context, final AsyncCallback<BankPayload> callback) {
		
		super(context, new GetCallParams(CardUrlManager.getSSOAuthenticateCall()) {{ //$NON-NLS-1$
			
			sendDeviceIdentifiers = true;
		}}, BankPayload.class);
		
		handler = new StrongReferenceHandler<BankPayload>(callback);
	}
	
	private static String getAuthorizationString(final String tokenValue, final String hashedTokenValue) {
		final String concatenatedCreds = tokenValue + ": :" + hashedTokenValue;
		return "DCRDSSO " + Base64.encodeToString(concatenatedCreds.getBytes(), Base64.NO_WRAP);
	}

	@Override
	protected TypedReferenceHandler<BankPayload> getHandler() {
		return handler;
	}
}
