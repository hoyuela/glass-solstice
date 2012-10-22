package com.discover.mobile.common.auth;

import java.io.InputStream;

import android.content.Context;
import android.os.Handler;

import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.net.AsyncCallback;
import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.StrongReferenceHandler;

public class PreAuthCheckCall extends NetworkServiceCall<PreAuthResult> {
	
	private static final ServiceCallParams STANDARD_PARAMS = new ServiceCallParams() {{
		method = HttpMethod.GET;
		path = "/cardsvcs/acs/session/preauthcheck";
	}};
	
	private final Handler handler;
	
	public PreAuthCheckCall(final Context context, final AsyncCallback<PreAuthResult> callback) {
		super(context, STANDARD_PARAMS);
		
		handler = new StrongReferenceHandler<PreAuthResult>(callback);
	}
	
	@Override
	protected Handler getHandler() {
		return handler;
	}

	@Override
	protected PreAuthResult parseResponse(final InputStream responseStream, final int resultCode) {
		// TODO
		
		// TEMP
		return new PreAuthResult() {{
			statusCode = resultCode;
		}};
	}
	
	public class PreAuthResult {
		// TEMP
		public int statusCode;
		
		// TODO
	}
	
}
