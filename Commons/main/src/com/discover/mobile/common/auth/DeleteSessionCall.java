package com.discover.mobile.common.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class DeleteSessionCall extends NetworkServiceCall<Object> {
	
	private static final ServiceCallParams STANDARD_PARAMS = new PostCallParams("/cardsvcs/acs/session/v1/delete") {{
		requiresSessionForRequest = true;
		clearsSessionAfterRequest = true;
	}};
	
	private final TypedReferenceHandler<Object> handler;
	
	public DeleteSessionCall(final Context context, final AsyncCallback<Object> callback) {
		super(context, STANDARD_PARAMS);
		
		handler = new StrongReferenceHandler<Object>(callback);
	}
	
	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}

	@Override
	protected Object parseSuccessResponse(int status,
			Map<String, List<String>> headers, InputStream body)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
