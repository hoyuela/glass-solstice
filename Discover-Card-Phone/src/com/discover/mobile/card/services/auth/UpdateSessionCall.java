package com.discover.mobile.card.services.auth;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.card.services.CardNetworkServiceCall;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class UpdateSessionCall extends CardNetworkServiceCall<Object> {
	
	private static final PostCallParams PARAMS = new PostCallParams(CardUrlManager.getSessionUrl());
	
	private final TypedReferenceHandler<Object> handler;
	
	public UpdateSessionCall(final Context context, final AsyncCallback<Object> callback) {
		super(context, PARAMS);

		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<Object>(callback);
	}

	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}

	@Override
	protected Object parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body) {
		return this;
	}
	
}
