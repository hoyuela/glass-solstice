package com.discover.mobile.common.auth;

import android.content.Context;

import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;

public class RegistrationCalls extends JsonMappingNetworkServiceCall<AccountDetails> {
	
	@SuppressWarnings("unused")
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<AccountDetails> handler;

	public RegistrationCalls(final Context context, final AsyncCallback<AccountDetails> callback,
			final String acctNbr, final String expirationMonth, final String dateOfBirthMonth, final String  dateOfBirthDay,
			final String socialSecurityNumber, final String dateOfBirthYear) {
		
		super(context, new ServiceCallParams() {{
			method = HttpMethod.POST;
			path = "/reg/v1/user/reg/auth";
			
		}}, AccountDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<AccountDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<AccountDetails> getHandler() {
		return handler;
	}
}
