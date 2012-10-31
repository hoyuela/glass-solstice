package com.discover.mobile.common.auth;

import java.util.Map;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.discover.mobile.common.Struct;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class AuthenticateCall extends JsonResponseMappingNetworkServiceCall<AccountDetails> {
	
	private final TypedReferenceHandler<AccountDetails> handler;
	
	private static final String ID_PREFIX = "%&(()!12[";

	public AuthenticateCall(final Context context, final AsyncCallback<AccountDetails> callback,
			final AuthCallParams params) {
		
		super(context, new GetCallParams("/cardsvcs/acs/acct/v1/account") {{
			
			headers = getHeadersFromAuthParams(params);
			
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
		}}, AccountDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<AccountDetails>(callback);
	}
	
	private static Map<String,String> getHeadersFromAuthParams(final AuthCallParams params) {
		final Builder<String, String> builder = ImmutableMap.<String,String>builder();
		
		final String concatenatedCreds = params.authUsername + ": :" + params.authPassword;
		Log.d(TAG, "creds: ", concatenatedCreds);
		final String dcrdBasicCreds = Base64.encodeToString(concatenatedCreds.getBytes(), Base64.DEFAULT);
		
		builder.put("Authorization", "DCRDBasic " + dcrdBasicCreds);
		
		if(params.did != null) builder.put("X-DID", ID_PREFIX + params.did);
		if(params.sid != null) builder.put("X-SID", ID_PREFIX + params.sid);
		if(params.oid != null) builder.put("X-OID", ID_PREFIX + params.oid);
		
		final Map<String, String> headers = builder.build();
		
		return headers;
	}

	@Override
	protected TypedReferenceHandler<AccountDetails> getHandler() {
		return handler;
	}
	
	@Struct
	public static class AuthCallParams {
		public String authUsername;
		public String authPassword;
		public String did;
		public String sid;
		public String oid;
	}
}
