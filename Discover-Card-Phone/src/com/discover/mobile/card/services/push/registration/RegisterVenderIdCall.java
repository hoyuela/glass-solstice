package com.discover.mobile.card.services.push.registration;

import android.content.Context;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * POST call that will register the device with the user and the vendor id.  
 * It sends the device identifiers with it so that the server can associate
 * everything correctly.
 * 
 * @author jthornton
 *
 */
public class RegisterVenderIdCall extends CardJsonResponseMappingNetworkServiceCall<DeviceRegistrationDetail> {
	
	@SuppressWarnings("unused")
	private static final String TAG = RegisterVenderIdCall.class.getSimpleName();
	
	private final TypedReferenceHandler<DeviceRegistrationDetail> handler;

	public RegisterVenderIdCall(final Context context, final AsyncCallback<DeviceRegistrationDetail> callback,
			final DeviceRegistrationDetail formData) {
		
		super(context, new PostCallParams(CardUrlManager.getPushRegisterVendorUrl()) {{ //$NON-NLS-1$
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
