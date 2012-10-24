package com.discover.mobile.common.auth;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;

import com.discover.mobile.common.net.AsyncCallback;
import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.json.JsonMappingNetworkServiceCall;
import com.google.common.collect.ImmutableMap;

public class AuthenticateCall extends JsonMappingNetworkServiceCall<AccountDetails> {
	
	@SuppressWarnings("unused")
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	// TEMP
	private final Handler handler;

	public AuthenticateCall(final Context context, final AsyncCallback<AccountDetails> callback, final String username, final String password) {
		super(context, new ServiceCallParams() {{
			method = HttpMethod.GET;
			path = "/cardsvcs/acs/acct/v1/account";
			
			final String concatenatedCreds = username + ": :" + password;
			final String dcrdBasicCreds = Base64.encodeToString(concatenatedCreds.getBytes(), Base64.DEFAULT);
			headers = ImmutableMap.<String,String>builder()
					.put("Authorization", "DCRDBasic " + dcrdBasicCreds).build();
		}}, AccountDetails.class);
		
		handler = new StrongReferenceHandler<AccountDetails>(callback);
	}

	@Override
	protected Handler getHandler() {
		// TEMP
		return handler;
		
		// TODO
	}
}
