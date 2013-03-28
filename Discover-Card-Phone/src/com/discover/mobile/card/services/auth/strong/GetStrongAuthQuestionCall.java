package com.discover.mobile.card.services.auth.strong;

import android.content.Context;

import com.discover.mobile.card.services.CardJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class GetStrongAuthQuestionCall extends CardJsonResponseMappingNetworkServiceCall<StrongAuthDetails> {
	
	private final TypedReferenceHandler<StrongAuthDetails> handler;

	public GetStrongAuthQuestionCall(final Context context, final AsyncCallback<StrongAuthDetails> callback) {
		super(context, new GetCallParams(CardUrlManager.getStrongAuthQuestionUrl()) {{
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
