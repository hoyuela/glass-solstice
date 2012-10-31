package com.discover.mobile.common.auth;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.google.common.collect.ImmutableMap;

public class AuthenticateCall extends JsonResponseMappingNetworkServiceCall<AccountDetails> {
	
	private final TypedReferenceHandler<AccountDetails> handler;

	public AuthenticateCall(final Context context, final AsyncCallback<AccountDetails> callback,
			final AuthCallParams params) {
		
		super(context, new GetCallParams("/cardsvcs/acs/acct/v1/account") {{
			final String concatenatedCreds = params.authUsername + ": :" + params.authPassword;
			final String dcrdBasicCreds = Base64.encodeToString(concatenatedCreds.getBytes(), Base64.DEFAULT);
			headers = ImmutableMap.<String,String>builder()
					.put("Authorization", "DCRDBasic " + dcrdBasicCreds)
					.put("X-DID", params.did)
					.put("X-SID", params.sid)
					.put("X-OID", params.oid).build();
			
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
		}}, AccountDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<AccountDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<AccountDetails> getHandler() {
		return handler;
	}
	
	public static class AuthCallParams {
		public String authUsername;
		public String authPassword;
		public String did = "";
		public String sid = "";
		public String oid = "";
	}
}
