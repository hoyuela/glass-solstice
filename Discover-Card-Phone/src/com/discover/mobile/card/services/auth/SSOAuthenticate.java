package com.discover.mobile.card.services.auth;

import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;

/**
 * ©2013 Discover Bank
 * 
 * This class send data for SSO Authentication
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class SSOAuthenticate {

	private final Context context;
	private final CardEventListener listener;

	/**
	 * Constructor
	 * 
	 */
	public SSOAuthenticate(final Context context, final CardEventListener listner) {
		this.context = context;
		listener = listner;
	}

	/**
	 * This method prepairs header/request and send data to server
	 * 
	 * @param tokenValue
	 * @param hashedTokenValue
	 */
	public void sendRequest(final String tokenValue,
			final String hashedTokenValue) {

		final WSRequest request = new WSRequest();
		final HashMap<String, String> headers = request.getHeaderValues();
		final String url = NetworkUtility.getWebServiceUrl(context,
				R.string.sso_authenticate_url);
		final String authString = getAuthorizationString(tokenValue, hashedTokenValue);
		headers.put("Authorization", authString);
		request.setUrl(url);
		request.setHeaderValues(headers);

		final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
				new BankPayload(), "Discover", "Authenticating...", listener);
		serviceCall.execute(request);

	}

	// encode credential
	private String getAuthorizationString(final String tokenValue,
			final String hashedTokenValue) {
		final String concatenatedCreds = "DCRDSSO "+tokenValue + ": :" + hashedTokenValue;
		return   concatenatedCreds;//Base64.encodeToString(concatenatedCreds.getBytes(),Base64.NO_WRAP);

	}
}
