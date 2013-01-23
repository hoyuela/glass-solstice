package com.discover.mobile.common.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.urlmanager.UrlManagerBank;
import com.discover.mobile.common.urlmanager.UrlManagerCard;

/**
 * Logout call for the application.  This will make a call to the services that will terminate the session
 * 
 * @author sseward
 *
 */
public class LogOutCall extends NetworkServiceCall<Object> {
	
	
	
	private static ServiceCallParams getParams(){
		ServiceCallParams params;
		String url;
		if (Globals.getCurrentAccount().equals(AccountType.CARD_ACCOUNT)){
			url = UrlManagerCard.getLogoutUrl();
		}else {
			url = UrlManagerBank.getLogoutUrl();
		}
		params = new PostCallParams(url) {{
			requiresSessionForRequest = true;
			clearsSessionAfterRequest = true;
		}};
		return params;
	}
	
	/**
	 * Reference handler so that the UI can be updated
	 */
	private final TypedReferenceHandler<Object> handler;
	
	/**
	 * Constructor for the call used currently for card
	 * @param context - activity context
	 * @param callback - callback that will run this in the background
	 */
	public LogOutCall(final Context context, final AsyncCallback<Object> callback) {
		this(context, callback, true);
	}
	
	/**
	 * New constructor created for the Bank logout call. 
	 * @param context
	 * @param callback
	 * @param isCard
	 */
	public LogOutCall(final Context context, final AsyncCallback<Object> callback, Boolean isCard){
		super(context, getParams(), isCard);
		
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
