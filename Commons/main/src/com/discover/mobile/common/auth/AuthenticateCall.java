package com.discover.mobile.common.auth;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.google.common.collect.ImmutableMap;

public class AuthenticateCall extends JsonMappingNetworkServiceCall<AccountDetails> {
	
	@SuppressWarnings("unused")
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<AccountDetails> handler;

	public AuthenticateCall(final Context context, final AsyncCallback<AccountDetails> callback,
			final String username, final String password) {
		
		super(context, new ServiceCallParams() {{
			method = HttpMethod.GET;
			path = "/cardsvcs/acs/acct/v1/account";
			
			final String concatenatedCreds = username + ": :" + password;
			final String dcrdBasicCreds = Base64.encodeToString(concatenatedCreds.getBytes(), Base64.DEFAULT);
			headers = ImmutableMap.<String,String>builder()
					.put("Authorization", "DCRDBasic " + dcrdBasicCreds).build();
		}}, AccountDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<AccountDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<AccountDetails> getHandler() {
		return handler;
	}
}
