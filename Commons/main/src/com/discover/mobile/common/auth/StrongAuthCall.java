package com.discover.mobile.common.auth;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;

public class StrongAuthCall extends JsonResponseMappingNetworkServiceCall<StrongAuthDetails> {
	
	private static final String TAG = StrongAuthCall.class.getSimpleName();
	
	private final TypedReferenceHandler<StrongAuthDetails> handler;

	public StrongAuthCall(final Context context, final AsyncCallback<StrongAuthDetails> callback) {
		
		super(context, new GetCallParams("/cardsvcs/acs/reg/v1/user/sa/check") {{
			requiresSessionForRequest = true;
			
			sendDeviceIdentifiers = true;
			
		}}, StrongAuthDetails.class);
		
		
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<StrongAuthDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<StrongAuthDetails> getHandler() {
		return handler;
	}
}
