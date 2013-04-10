package com.discover.mobile.bank.services.auth.strong;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;

/**
 * The Bank Login call for retrieving a valid token and any URL's that are
 * returned.
 * 
 * @author ajleeds
 * 
 */
public class CreateStrongAuthRequestCall extends
		BankJsonResponseMappingNetworkServiceCall<BankStrongAuthDetails> {

	private final TypedReferenceHandler<BankStrongAuthDetails> handler;

	/**
	 * This constructor is used for doing Strong Auth Get requests
	 * @param context
	 * @param callback
	 */
	public CreateStrongAuthRequestCall(final Context context,
			final AsyncCallback<BankStrongAuthDetails> callback) {
		
		super(context, new GetCallParams(BankUrlManager.getStrongAuthUrl()) {{
			requiresSessionForRequest = false;
			
			// Specify what error parser to use when receiving an error response
			errorResponseParser = BankErrorResponseParser.instance();
			
			sendDeviceIdentifiers = true;
			
			// Specify what error parser to use when receiving an error response
			errorResponseParser = BankErrorResponseParser.instance();
		}}, BankStrongAuthDetails.class);
		handler = new StrongReferenceHandler<BankStrongAuthDetails>(callback);
	}
	
	/**
	 * This constructor is used for doing Strong Auth post requests. 
	 * @param context
	 * @param callback
	 * @param details Data sent with post request. Adding this parameter ensures that the request is a post
	 */
	public CreateStrongAuthRequestCall(final Context context,
			final AsyncCallback<BankStrongAuthDetails> callback, final BankStrongAuthAnswerDetails details) {
		
		super(context, new PostCallParams(BankUrlManager.getStrongAuthUrl()) {{
			requiresSessionForRequest = false;
			
			// Specify what error parser to use when receiving an error response
			errorResponseParser = BankErrorResponseParser.instance();
			
			sendDeviceIdentifiers = true;
			body = details;
		}}, BankStrongAuthDetails.class);
		handler = new StrongReferenceHandler<BankStrongAuthDetails>(callback);
	}

	@Override
	protected TypedReferenceHandler<BankStrongAuthDetails> getHandler() {
		return handler;
	}
	
}
