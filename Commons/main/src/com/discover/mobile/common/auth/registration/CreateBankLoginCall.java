package com.discover.mobile.common.auth.registration;

import android.content.Context;
import android.util.Base64;

import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.PostCallParams;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.url.UrlManagerBank;
import com.discover.mobile.common.url.UrlManagerCard;

public class CreateBankLoginCall extends JsonResponseMappingNetworkServiceCall<BankLoginData> {
	
	private final TypedReferenceHandler<BankLoginData> handler;

	public CreateBankLoginCall(final Context context, final AsyncCallback<BankLoginData> callback,
			final String username, final String password) {
		
		super(context, new PostCallParams(UrlManagerBank.getGetTokenUrl()) {{
			// FIXME remove this code if not necessary
//			final String authString = getAuthorizationString(formData.acctNbr, formData.password);
//			headers = ImmutableMap.<String,String>builder()
//					.put("Authorization", authString)
//					.put("X-Override-UID", "true")
//					.build();
			
//			clearsSessionBeforeRequest = true;

			requiresSessionForRequest = true;
			
			sendDeviceIdentifiers = true;
			
		}}, BankLoginData.class);
		
		
		
		// TODO decide if this is the best type of handler
		handler = new StrongReferenceHandler<BankLoginData>(callback);
	}

	
	@Override
	protected TypedReferenceHandler<BankLoginData> getHandler() {
		return handler;
	}
}
