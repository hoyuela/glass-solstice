package com.discover.mobile.common.auth.forgot;

import android.content.Context;

import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.url.UrlManager;

public class ForgotPasswordTwoCall extends JsonResponseMappingNetworkServiceCall<RegistrationConfirmationDetails> {
	
	private static final String TAG = ForgotPasswordTwoCall.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationConfirmationDetails> handler;

	public ForgotPasswordTwoCall(final Context context, final AsyncCallback<RegistrationConfirmationDetails> callback,
			final ForgotPasswordTwoDetails formData) {
		
		super(context, new PostCallParams(UrlManager.getForgotPasswordTwoUrl()) {{
			requiresSessionForRequest = true;
//			sendDeviceIdentifiers = true;
			
			body = formData;
		}}, RegistrationConfirmationDetails.class);
		
		handler = new StrongReferenceHandler<RegistrationConfirmationDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<RegistrationConfirmationDetails> getHandler() {
		return handler;
	}

}
