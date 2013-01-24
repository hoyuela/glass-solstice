package com.discover.mobile.common.auth.bank;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import roboguice.util.Strings;
import android.content.Context;
import android.util.Log;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.error.ExceptionLibrary;
import com.discover.mobile.common.net.error.bank.BankErrorResponseParser;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.urlmanager.UrlManagerBank;

/**
 * The Bank Login call for retrieving a valid token and any URL's that are
 * returned.
 * 
 * @author ajleeds
 * 
 */
public class CreateBankLoginCall extends
		JsonResponseMappingNetworkServiceCall<BankLoginData> {
	
	private static final String TAG = CreateBankLoginCall.class.getSimpleName();
	private final TypedReferenceHandler<BankLoginData> handler;

	public CreateBankLoginCall(final Context context,
			final AsyncCallback<BankLoginData> callback,
			final BankLoginDetails login) {
 
		super(context, new PostCallParams(UrlManagerBank.getGetTokenUrl()) {
			{

				clearsSessionBeforeRequest = true;

				requiresSessionForRequest = false;

				sendDeviceIdentifiers = true;
						
				body = login;
				
				// Specify what error parser to use when receiving an error response
				errorResponseParser = BankErrorResponseParser.instance();

			}
		}, BankLoginData.class, false);

		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<BankLoginData>(callback);
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
		
		//Fetch token from JSON response
		if( data.token != null && !Strings.isEmpty(data.token)  ) {
			//When sending a request with a token as part of the request the format to follow is 
			//Authorization: BankBasic <<token>>
			SessionTokenManager.setToken(BankSchema.BANKBASIC +" " +data.token);
		} else {
			String message = "Response does not include token";
			
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, message);
			}
	
			throw new ExceptionLibrary.MissingTokenException();
		}
		
		return data;
	}
}
