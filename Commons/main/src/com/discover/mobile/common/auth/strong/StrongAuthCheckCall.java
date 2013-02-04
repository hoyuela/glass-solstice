package com.discover.mobile.common.auth.strong;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.DelegatingErrorResponseParser;
import com.discover.mobile.common.net.error.ErrorResponseParser;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.CardUrlManager;

public class StrongAuthCheckCall extends JsonResponseMappingNetworkServiceCall<StrongAuthDetails> {
	
	private static final ErrorResponseParser<?> STRONG_AUTH_ERROR_RESPONSE_PARSER;
	
	static {
		int size = DelegatingErrorResponseParser.DEFAULT_PARSER_DELEGATES.size() + 1;
		List<ErrorResponseParser<?>> errorResponseParsers = new ArrayList<ErrorResponseParser<?>>(size);
		errorResponseParsers.add(new StrongAuthErrorResponseParser());
		errorResponseParsers.addAll(DelegatingErrorResponseParser.DEFAULT_PARSER_DELEGATES);
		STRONG_AUTH_ERROR_RESPONSE_PARSER = new DelegatingErrorResponseParser(errorResponseParsers);
	}
	
	private final TypedReferenceHandler<StrongAuthDetails> handler;

	public StrongAuthCheckCall(final Context context, final AsyncCallback<StrongAuthDetails> callback) {
		
		super(context, new GetCallParams(CardUrlManager.getStrongAuthCheckUrl()) {{
			requiresSessionForRequest = true;
			
			sendDeviceIdentifiers = true;
			
			errorResponseParser = STRONG_AUTH_ERROR_RESPONSE_PARSER;
		}}, StrongAuthDetails.class);
		
		
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<StrongAuthDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<StrongAuthDetails> getHandler() {
		return handler;
	}
}
