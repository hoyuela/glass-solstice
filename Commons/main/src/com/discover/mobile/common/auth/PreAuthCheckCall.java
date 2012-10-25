package com.discover.mobile.common.auth;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.response.AsyncCallback;

public class PreAuthCheckCall extends NetworkServiceCall<PreAuthResult> {
	
	private static final ServiceCallParams STANDARD_PARAMS = new ServiceCallParams() {{
		method = HttpMethod.GET;
		path = "/cardsvcs/acs/session/preauthcheck";
	}};
	
	private final TypedReferenceHandler<PreAuthResult> handler;
	
	public PreAuthCheckCall(final Context context, final AsyncCallback<PreAuthResult> callback) {
		super(context, STANDARD_PARAMS);
		
		handler = new StrongReferenceHandler<PreAuthResult>(callback);
	}
	
	@Override
	protected TypedReferenceHandler<PreAuthResult> getHandler() {
		return handler;
	}

	@Override
	protected PreAuthResult parseSuccessResponse(final int status, final Map<String,List<String>> headers,
			final InputStream responseStream) {
		
		// TODO
		
		// TEMP
		return new PreAuthResult() {{
			statusCode = status;
		}};
	}
	
	public static class PreAuthResult {
		public int statusCode;
	}
	
}
