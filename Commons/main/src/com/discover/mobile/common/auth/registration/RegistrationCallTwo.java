package com.discover.mobile.common.auth.registration;

import android.content.Context;

import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;

public class RegistrationCallTwo extends JsonResponseMappingNetworkServiceCall<RegistrationDetails> {
	
	@SuppressWarnings("unused")
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationDetails> handler;

	public RegistrationCallTwo(final Context context, final AsyncCallback<RegistrationDetails> callback,
			final String userId, final String userIdConfirm, final String password, 
			final String passwordConfirm, final String email) {
		
		super(context, new PostCallParams("/reg/v1/user/reg") {{
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
