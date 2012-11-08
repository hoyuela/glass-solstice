package com.discover.mobile.common.auth;

import java.nio.charset.Charset;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.callback.AsyncCallback;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.google.common.collect.ImmutableMap;

public class AuthenticateCall extends JsonResponseMappingNetworkServiceCall<AccountDetails> {

	private static final String TAG = AuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<AccountDetails> handler;

	public AuthenticateCall(final Context context, final AsyncCallback<AccountDetails> callback,
			final String username, final String password) {
		
		super(context, new GetCallParams("/cardsvcs/acs/acct/v1/account") {{
			
			final String authString = getAuthorizationString(username, password);
			headers = ImmutableMap.<String,String>builder().put("Authorization", authString).build();
			
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
		}}, AccountDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<AccountDetails>(callback);
	}
	
	private static String getAuthorizationString(final String username, final String password) {
		final String concatenatedCreds = username + ": :" + password;
		final Charset charset = Charset.forName("UTF-8");
		final byte[] credsBytes = charset.encode(concatenatedCreds).array();
		final String authString = "DCRDBasic " + Base64.encodeToString(credsBytes, Base64.DEFAULT);
		return authString.trim();
	}

	@Override
	protected TypedReferenceHandler<AccountDetails> getHandler() {
		return handler;
	}
}
