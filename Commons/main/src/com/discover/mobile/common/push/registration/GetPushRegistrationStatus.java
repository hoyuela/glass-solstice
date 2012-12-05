package com.discover.mobile.common.push.registration;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.VidReferanceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.xtify.sdk.api.XtifySDK;

public class GetPushRegistrationStatus extends JsonResponseMappingNetworkServiceCall<PushRegistrationStatusDetail>{

	private final TypedReferenceHandler<PushRegistrationStatusDetail> handler;
	
	public static String vid;

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
		return "/cardsvcs/acs/contact/v1/registration/status?vid=" + XtifySDK.getXidKey(context) ; //$NON-NLS-1$
	}
}
