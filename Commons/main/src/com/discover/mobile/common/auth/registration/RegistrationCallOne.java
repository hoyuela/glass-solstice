package com.discover.mobile.common.auth.registration;

import android.content.Context;

import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;

public class RegistrationCallOne extends JsonResponseMappingNetworkServiceCall<RegistrationDetails> {
	
	@SuppressWarnings("unused")
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationDetails> handler;

	public RegistrationCallOne(final Context context, final AsyncCallback<RegistrationDetails> callback,
			final String acctNbr, final String expirationMonth, final String expirationYear, final String dateOfBirthMonth, final String  dateOfBirthDay,
			final String socialSecurityNumber, final String dateOfBirthYear) {
		
		super(context, new PostCallParams("/reg/v1/user/reg/auth") {{
			// TODO
		}}, RegistrationDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<RegistrationDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<RegistrationDetails> getHandler() {
		return handler;
	}
}
