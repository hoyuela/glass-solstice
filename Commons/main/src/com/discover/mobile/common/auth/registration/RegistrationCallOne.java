package com.discover.mobile.common.auth.registration;

import android.content.Context;

import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.WeakReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;

public class RegistrationCallOne extends JsonResponseMappingNetworkServiceCall<RegistrationOneDetails> {
	
	private static final String TAG = RegistrationCallOne.class.getSimpleName();
	
	private final TypedReferenceHandler<RegistrationOneDetails> handler;

	public RegistrationCallOne(final Context context, final AsyncCallback<RegistrationOneDetails> callback,
			final RegistrationOneDetails formData) {
		
		super(context, new PostCallParams("/cardsvcs/acs/reg/v1/user/reg/auth") {{
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
			
			body = formData;
		}}, RegistrationOneDetails.class);
		
		handler = new WeakReferenceHandler<RegistrationOneDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<RegistrationOneDetails> getHandler() {
		return handler;
	}
}
