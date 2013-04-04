package com.discover.mobile.bank.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;

/**
 * Class used to download links from the Bank API Service using a GET /api HTTP request. The JSON response
 * is mapped to a BankApiLinks object. The instance is used to gather URL link for services such as:
 * 
 * Customer Information Download
 * Privacy & Terms Download
 * Terms of Use Download
 * Bank Holiday Download
 * 
 * @author henryoyuela
 *
 */
public class BankApiServiceCall extends BankJsonResponseMappingNetworkServiceCall<BankApiLinks> {

	private final TypedReferenceHandler<BankApiLinks> handler;

	
	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public BankApiServiceCall(final Context context, final AsyncCallback<BankApiLinks> callback) {

		super(context, new GetCallParams(BankUrlManager.getApiUrl()) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;

				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = false;

				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;

				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();

			}
		}, BankApiLinks.class);

		handler = new StrongReferenceHandler<BankApiLinks>(callback);
	}
	
	@Override
	protected TypedReferenceHandler<BankApiLinks> getHandler() {
		return handler;
	}

	@Override
	protected BankApiLinks parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {	
		
		final BankApiLinks api = super.parseSuccessResponse(status, headers, body);
		
		if( api != null ) {
			/**Cache links in Bank URL Manager*/
			BankUrlManager.getLinks().putAll(api.links);
		}
		
		return api;
	}

}
