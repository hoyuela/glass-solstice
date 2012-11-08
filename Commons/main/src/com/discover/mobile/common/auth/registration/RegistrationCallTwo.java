package com.discover.mobile.common.auth.registration;

import android.content.Context;

import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.callback.AsyncCallback;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class RegistrationCallTwo extends JsonResponseMappingNetworkServiceCall<RegistrationConfirmationDetails> {
	
	private static final String TAG = RegistrationCallTwo.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationConfirmationDetails> handler;

	public RegistrationCallTwo(final Context context, final AsyncCallback<RegistrationConfirmationDetails> callback,
			final RegistrationTwoDetails formData) {
		
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
