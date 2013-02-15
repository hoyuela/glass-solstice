package com.discover.mobile.bank.services.auth;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankNetworkServiceCall;
import com.discover.mobile.bank.services.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.Struct;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

public class PreAuthCheckCall extends BankNetworkServiceCall<PreAuthResult> {
	
	private static final ServiceCallParams STANDARD_PARAMS = new GetCallParams(FacadeFactory.getCardFacade().getPreAuthUrl()) {{
		requiresSessionForRequest = false;
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

		return new PreAuthResult() {{
			statusCode = status;
			final List<String> descriptions = headers.get("VersionInfo");
			
			if(descriptions != null && !descriptions.isEmpty())
				upgradeDescription = descriptions.get(0);
		}};
	}
	
	@Struct
	public static class PreAuthResult {
		public int statusCode;
		public String upgradeDescription = null;
	}
	
}
