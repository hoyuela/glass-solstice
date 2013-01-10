package com.discover.mobile.common.auth.registration;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.url.UrlManager;

public class CreateLoginCall extends JsonResponseMappingNetworkServiceCall<RegistrationConfirmationDetails> {
	
	private final TypedReferenceHandler<RegistrationConfirmationDetails> handler;

	public CreateLoginCall(final Context context, final AsyncCallback<RegistrationConfirmationDetails> callback,
			final CreateLoginDetails formData) {
		
		super(context, new PostCallParams(UrlManager.getLoginUrl()) {{
			// FIXME remove this code if not necessary
//			final String authString = getAuthorizationString(formData.acctNbr, formData.password);
//			headers = ImmutableMap.<String,String>builder()
//					.put("Authorization", authString)
//					.put("X-Override-UID", "true")
//					.build();
			
//			clearsSessionBeforeRequest = true;

			requiresSessionForRequest = true;
			
			sendDeviceIdentifiers = true;
			
			body = formData;
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
