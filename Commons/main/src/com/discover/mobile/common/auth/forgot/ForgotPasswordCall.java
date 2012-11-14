package com.discover.mobile.common.auth.forgot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.response.AsyncCallback;

public class ForgotPasswordCall extends NetworkServiceCall<Object> {
	
	private static final String TAG = ForgotPasswordCall.class.getSimpleName();
	
	private final TypedReferenceHandler<Object> handler;

	public ForgotPasswordCall(final Context context, final AsyncCallback<Object> callback,
			final AccountInformationDetails formData) {
		
		super(context, new PostCallParams("/cardsvcs/acs/reg/v1/user/pwd/auth") {{
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
	protected Object parseSuccessResponse(final int status,
			final Map<String, List<String>> headers, final InputStream body)
			throws IOException {
		return this;
	}
}
