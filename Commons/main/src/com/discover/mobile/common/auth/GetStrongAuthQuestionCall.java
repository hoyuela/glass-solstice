package com.discover.mobile.common.auth;

import android.content.Context;

import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;

public class GetStrongAuthQuestionCall extends JsonResponseMappingNetworkServiceCall<StrongAuthDetails> {
	
	private static final String TAG = GetStrongAuthQuestionCall.class.getSimpleName();
	
	private final TypedReferenceHandler<StrongAuthDetails> handler;

	public GetStrongAuthQuestionCall(final Context context, final AsyncCallback<StrongAuthDetails> callback) {
		
		super(context, new GetCallParams("/cardsvcs/acs/strongauth/v1/challenge") {{
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
