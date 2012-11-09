package com.discover.mobile.common.auth.registration;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class CreateLoginCall extends JsonResponseMappingNetworkServiceCall<RegistrationConfirmationDetails> {
	
	private static final String TAG = CreateLoginCall.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationConfirmationDetails> handler;

	public CreateLoginCall(final Context context, final AsyncCallback<RegistrationConfirmationDetails> callback,
			final CreateLoginDetails formData) {
		
		super(context, new PostCallParams("/cardsvcs/acs/reg/v1/user/reg") {{
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
			
			body = formData;
		}}, RegistrationConfirmationDetails.class);
		
		
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<RegistrationConfirmationDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<RegistrationConfirmationDetails> getHandler() {
		return handler;
	}
}
