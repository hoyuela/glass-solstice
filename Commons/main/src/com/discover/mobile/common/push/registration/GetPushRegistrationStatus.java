package com.discover.mobile.common.push.registration;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.VidReferanceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.url.UrlManager;
import com.xtify.sdk.api.XtifySDK;

/**
 * Get the the registration status of the vendor ID.  It checks to see if the 
 * Xtify ID has been registered with discover. 
 * 
 * @author jthornton
 *
 */
public class GetPushRegistrationStatus extends JsonResponseMappingNetworkServiceCall<PushRegistrationStatusDetail>{

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
		return UrlManager.getPushRegistrationStatusUrl(XtifySDK.getXidKey(context)) ; //$NON-NLS-1$
	}
}
