package com.discover.mobile.common.push.registration;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.VidRegistrationsRefereanceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class RegisterVenderIdCall extends JsonResponseMappingNetworkServiceCall<DeviceRegistrationDetail> {
	
	@SuppressWarnings("unused")
	private static final String TAG = RegisterVenderIdCall.class.getSimpleName();
	
	private final TypedReferenceHandler<DeviceRegistrationDetail> handler;

	public RegisterVenderIdCall(final Context context, final AsyncCallback<DeviceRegistrationDetail> callback,
			final DeviceRegistrationDetail formData) {
		
		super(context, new PostCallParams("/cardsvcs/acs/contact/v1/registration/status") {{ //$NON-NLS-1$
			requiresSessionForRequest = true;
			sendDeviceIdentifiers = true;
			
			body = formData;
		}}, DeviceRegistrationDetail.class);
		
		handler = new VidRegistrationsRefereanceHandler<DeviceRegistrationDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<DeviceRegistrationDetail> getHandler() {
		return handler;
	}

}
