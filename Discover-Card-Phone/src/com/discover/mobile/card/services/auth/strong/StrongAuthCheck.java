/**
 * 
 */
package com.discover.mobile.card.services.auth.strong;

import java.util.HashMap;

import android.content.Context;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class checks with server if this device requires strong auth or not
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthCheck {

	private Context context;
	private final String TAG = StrongAuthCheck.class.getSimpleName();
	private CardEventListener listener;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param listener
	 *            CardEventListener
	 */
	public StrongAuthCheck(Context context, CardEventListener listener) {
		this.context = context;
		this.listener = listener;
	}

	/**
	 * Check with server if Strong Authentication is required.
	 * 
	 */
	public void sendRequest() {
		WSRequest request = new WSRequest();
		HashMap<String, String> headers = request.getHeaderValues();
		CardShareDataStore cardShareDataStoreObj = CardShareDataStore
				.getInstance(context);
		SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
				.getCookieManagerInstance();
		sessionCookieManagerObj.setCookieValues();

		headers.put("X-Sec-Token", sessionCookieManagerObj.getSecToken());
		String url = NetworkUtility.getWebServiceUrl(context,
				R.string.strongAuth_url);

		request.setUrl(url);
		request.setHeaderValues(headers);
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, null,
				"Discover", "Authenticating...", listener);
		serviceCall.execute(request);
	}
}
