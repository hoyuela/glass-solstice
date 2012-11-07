package com.discover.mobile.common.forgotuidpassword;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.google.common.collect.ImmutableMap;

public class ForgotUserIdCall extends JsonResponseMappingNetworkServiceCall<UserIdDetails> {

	@SuppressWarnings("unused")
	private static final String TAG = AuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<UserIdDetails> handler;

	public ForgotUserIdCall(final Context context, final AsyncCallback<UserIdDetails> callback,
			final String accountNumber, final String password) {
		
		super(context, new GetCallParams("/cardsvcs/acs/reg/v1/user/id") {{
			
			final String authString = getAuthorizationString(accountNumber, password);
			headers = ImmutableMap.<String,String>builder()
					.put("Authorization", authString)
					.put("X-Override-UID", "true")
					.build();
			
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
		}}, UserIdDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<UserIdDetails>(callback);
	}
	
	private static String getAuthorizationString(final String username, final String password) {
		final String concatenatedCreds = username + ": :" + password;
		return "DCRDBasic " + Base64.encodeToString(concatenatedCreds.getBytes(), Base64.DEFAULT);
	}

	@Override
	protected TypedReferenceHandler<UserIdDetails> getHandler() {
		return handler;
	}
}
