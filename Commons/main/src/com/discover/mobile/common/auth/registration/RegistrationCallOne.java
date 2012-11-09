package com.discover.mobile.common.auth.registration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class RegistrationCallOne extends NetworkServiceCall<Object> {
	
	private static final String TAG = RegistrationCallOne.class.getSimpleName();
	
	private final TypedReferenceHandler<Object> handler;

	public RegistrationCallOne(final Context context, final AsyncCallback<Object> callback,
			final RegistrationOneDetails formData) {
		
		super(context, new PostCallParams("/cardsvcs/acs/reg/v1/user/reg/auth") {{
			clearsSessionBeforeRequest = true;
			requiresSessionForRequest = false;
			
			sendDeviceIdentifiers = true;
			
			body = formData;
		}});
		
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
		return this;
	}
}
