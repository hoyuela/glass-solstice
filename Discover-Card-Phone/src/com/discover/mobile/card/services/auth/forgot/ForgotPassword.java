package com.discover.mobile.card.services.auth.forgot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class makes a service call for Forgot Password feature
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class ForgotPassword {

	private Context context;	
	private CardEventListener listener;
	private AccountInformationDetails accountInformationDetails;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param listener
	 *            CardEventListener
	 * @param accountInformationDetails
	 *            AccountInformationDetails
	 */
	public ForgotPassword(Context context, CardEventListener listener,
			AccountInformationDetails accountInformationDetails) {
		this.context = context;
		this.listener = listener;
		this.accountInformationDetails = accountInformationDetails;
	}

	/**
	 * This method make a service call for sending account details to server.
	 * 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void sendRequest() throws JsonGenerationException,
			JsonMappingException, IOException {
		WSRequest request = new WSRequest();

		// Setting the headers available for the service
		HashMap<String, String> headers = request.getHeaderValues();
		headers.put("X-SEC-Token", "");
		String url = NetworkUtility.getWebServiceUrl(context,
				R.string.forgotPassword_url);

		request.setUrl(url);
		request.setHeaderValues(headers);
		request.setMethodtype("POST");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JacksonObjectMapperHolder.getMapper().writeValue(baos,
				accountInformationDetails);
		Utils.log("sendRequest","json request body"+ baos.toByteArray().toString());
		request.setInput(baos.toByteArray());

		WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
				"Discover", "Loading...", listener);
		serviceCall.execute(request);
	}
}
