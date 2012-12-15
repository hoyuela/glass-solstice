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

/**
 * Logout call for the application.  This wil make a call to the servicves that will terminate the session
 * 
 * @author sseward
 *
 */
public class LogOutCall extends NetworkServiceCall<Object> {
	
	/**
	 * Service call params to be used
	 */
	private static final ServiceCallParams STANDARD_PARAMS = new PostCallParams("/cardsvcs/acs/session/v1/delete") {{
		requiresSessionForRequest = true;
		clearsSessionAfterRequest = true;
	}};
	
	/**
	 * Reference handler so that the UI can be updated
	 */
	private final TypedReferenceHandler<Object> handler;
	
	/**
	 * Constructor for the call
	 * @param context - activity context
	 * @param callback - callback that will run this in the background
	 */
	public LogOutCall(final Context context, final AsyncCallback<Object> callback) {
		super(context, STANDARD_PARAMS);
		
		handler = new StrongReferenceHandler<Object>(callback);
	}
	
	/**
	 * Get the typed reference handler
	 * @return the typed reference handler
	 */
	@Override
	protected TypedReferenceHandler<Object> getHandler() {
		return handler;
	}

	/**
	 * Parse the successful response
	 * @param status - int value representing the success status
	 * @param header - list of headers in the response
	 * @param body - body of the response
	 * @return the success response
	 */
	@Override
	protected Object parseSuccessResponse(final int status,
				final Map<String, List<String>> headers, final InputStream body)
				throws IOException {
		return null;
	}
	
}
