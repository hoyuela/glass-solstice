package com.discover.mobile.bank.services.auth;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.Struct;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * Preauth call that is used to determine if the application is out of date and if
 * the application can be used.
 * 
 * @author jthornton
 *
 */
public class PreAuthCheckCall extends NetworkServiceCall<PreAuthResult> {

	/**Parameters that need to be applied to the service call*/
	private static final ServiceCallParams OARAMS = new GetCallParams(FacadeFactory.getCardFacade().getPreAuthUrl()){{
		requiresSessionForRequest = false;
	}};

	/**Reference handler for the request*/
	private final TypedReferenceHandler<PreAuthResult> handler;

	/**
	 * Constructor for the call
	 * @param context - context used to make the call
	 * @param callback - callbak used to make the call
	 */
	public PreAuthCheckCall(final Context context, final AsyncCallback<PreAuthResult> callback) {
		super(context, OARAMS, FacadeFactory.getCardFacade().getPreAuthBaseUrl());

		handler = new StrongReferenceHandler<PreAuthResult>(callback);
	}

	@Override
	protected TypedReferenceHandler<PreAuthResult> getHandler() {
		return handler;
	}

	@Override
	protected PreAuthResult parseSuccessResponse(final int status, final Map<String,List<String>> headers,
			final InputStream responseStream) {

		return new PreAuthResult() {{
			statusCode = status;
			final List<String> descriptions = headers.get("VersionInfo");

			if(descriptions != null && !descriptions.isEmpty()) {
				upgradeDescription = descriptions.get(0);
			}
		}};
	}

	@Override
	protected String getBaseUrl() {
		return FacadeFactory.getCardFacade().getPreAuthBaseUrl();
	}

	@Struct
	public static class PreAuthResult {
		public int statusCode;
		public String upgradeDescription = null;
	}

}
