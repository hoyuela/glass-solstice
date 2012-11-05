package com.discover.mobile.common.auth.registration;

import android.content.Context;

import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;

public class RegistrationCallTwo extends JsonResponseMappingNetworkServiceCall<RegistrationTwoDetails> {
	
	private static final String TAG = RegistrationCallTwo.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationTwoDetails> handler;

	public RegistrationCallTwo(final Context context, final AsyncCallback<RegistrationTwoDetails> callback,
			final RegistrationTwoDetails formData) {
		///cardsvcs/acs/reg/v1/user/reg/auth
		super(context, new PostCallParams("/cardsvcs/acs/reg/v1/user/reg") {{
			// TODO
			
			body = formData;
		}}, RegistrationTwoDetails.class);
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<RegistrationTwoDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<RegistrationTwoDetails> getHandler() {
		return handler;
	}
}
