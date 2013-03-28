package com.discover.mobile.card.services.push.registration;

import android.content.Context;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.xtify.sdk.api.XtifySDK;

/**
 * Get the the registration status of the vendor ID.  It checks to see if the 
 * Xtify ID has been registered with discover. 
 * 
 * @author jthornton
 *
 */
public class GetPushRegistrationStatus extends CardJsonResponseMappingNetworkServiceCall<PushRegistrationStatusDetail>{

	private final TypedReferenceHandler<PushRegistrationStatusDetail> handler;
	
	public GetPushRegistrationStatus(final Context context, final AsyncCallback<PushRegistrationStatusDetail> callback){
		super(context, new GetCallParams(getUrl(context)) {{
		
			sendDeviceIdentifiers = true;
		}}, PushRegistrationStatusDetail.class);
		
		handler = new VidReferanceHandler<PushRegistrationStatusDetail>(callback);
	}

	@Override
	protected TypedReferenceHandler<PushRegistrationStatusDetail> getHandler() {
		return handler;
	}
	
	private static String getUrl(final Context context){
		return CardUrlManager.getPushRegistrationStatusUrl(XtifySDK.getXidKey(context)) ; //$NON-NLS-1$
	}
}
