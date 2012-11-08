package com.discover.mobile.common.auth;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.callback.AsyncCallback;

public class UpdateSessionCall extends NetworkServiceCall<Object> {
	
	private static final String TAG = UpdateSessionCall.class.getSimpleName();
	
	private final TypedReferenceHandler<Object> handler;
	
	public UpdateSessionCall(final Context context, final AsyncCallback<Object> callback) {
		// TODO make PostCallParams a static instance
		super(context, new PostCallParams("/cardsvcs/acs/session/v1/update"));

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
