package com.discover.mobile.bank.services.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.util.Strings;
import android.content.Context;
import android.util.Log;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.SessionTokenManager;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.ExceptionLibrary;
import com.discover.mobile.common.net.error.bank.BankErrorSSOResponseParser;

/**
 * The Bank Login call for retrieving a valid token and any URL's that are
 * returned.
 * 
 * @author ajleeds
 * 
 */
public class CreateBankLoginCall extends
		BankJsonResponseMappingNetworkServiceCall<BankLoginData> {
	
	private final String TAG = CreateBankLoginCall.class.getSimpleName();
	private final TypedReferenceHandler<BankLoginData> handler;
	
	public CreateBankLoginCall(final Context context,
			final AsyncCallback<BankLoginData> callback,
			final BankLoginDetails login, final boolean skipSSO) {
		super(context, new PostCallParams(BankUrlManager.getGetTokenUrl()) {
			{
				if(skipSSO) {
					headers = new HashMap<String, String>();
					headers.put("X-Skip-SSO", "true");
				}

				clearsSessionBeforeRequest = true;

				requiresSessionForRequest = false;

				sendDeviceIdentifiers = true;
						
				body = login;
				
				// Specify what error parser to use when receiving an error response
//				errorResponseParser = BankErrorResponseParser.instance();
				errorResponseParser = BankErrorSSOResponseParser.instance();

			}
		}, BankLoginData.class);

		handler = new StrongReferenceHandler<BankLoginData>(callback);
	}

	public CreateBankLoginCall(final Context context,
			final AsyncCallback<BankLoginData> callback,
			final BankLoginDetails login) {
		
		this(context, callback, login, false);
// TODO(remove this all)
//		super(context, new PostCallParams(BankUrlManager.getGetTokenUrl()) {
//			{
//
//				clearsSessionBeforeRequest = true;
//
//				requiresSessionForRequest = false;
//
//				sendDeviceIdentifiers = true;
//						
//				body = login;
//				
//				// Specify what error parser to use when receiving an error response
//				errorResponseParser = BankErrorResponseParser.instance();
//
//			}
//		}, BankLoginData.class);
//
//		handler = new StrongReferenceHandler<BankLoginData>(callback);
	}

	/*
	 * (non-Javadoc)
	 * @see com.discover.mobile.common.net.NetworkServiceCall#getHandler()
	 */
	@Override
	protected TypedReferenceHandler<BankLoginData> getHandler() {
		return handler;
	}
	
	/**
	 * Verifies that a token is provided by the service via the Authorization HTTP Header. If it is, 
	 * then it caches the token in the SessionTokenManager which will later be used by any bank related
	 * NetworkServiceCall<>. A bank related NetworkService call can use the token if its params is set 
	 * to use a token by setting requiresSessionForRequest to true. In addition, it calls the base class 
	 * parser to parse the incoming JSON and de-serialize to a BankLoginData object.
	 * 
	 * @return Returns the BankLoginData extracted from the body of the HTTP response.
	 */
	@Override
	protected BankLoginData parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		final BankLoginData data = super.parseSuccessResponse(status, headers, body);
		
		// TODO This is going to happen _very_ differently
		// Will handle two cases.
		// 1. A normal, non-sso user will return the token, just as it is now.
		// 2. An SSO user will return a payload for the card service.
		
		//Fetch token from JSON response
		if( data.token != null && !Strings.isEmpty(data.token)  ) {
			//When sending a request with a token as part of the request the format to follow is 
			//Authorization: BankBasic <<token>>
			SessionTokenManager.setToken(BankSchema.BANKBASIC +" " +data.token);
			BankUrlManager.setNewLinks(data.links);
		} else {
			final String message = "Response does not include token";
			
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, message);
			}
	
			throw new ExceptionLibrary.MissingTokenException();
		}
		
		return data;
	}
}
