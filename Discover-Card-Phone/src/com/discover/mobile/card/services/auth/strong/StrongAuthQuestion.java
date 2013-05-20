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
import com.discover.mobile.card.common.utils.Utils;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class fetches question for Strong Authentication from server
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthQuestion {

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
	public StrongAuthQuestion(Context context, CardEventListener listener) {
		this.context = context;
		this.listener = listener;
	}

	/**
	 * This method makes a server call to fetch question
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

		headers.put("X-SEC-Token", sessionCookieManagerObj.getSecToken());
		String url = NetworkUtility.getWebServiceUrl(context,
				R.string.strongAuth_quest_url);

		request.setUrl(url);
		request.setHeaderValues(headers);
		Utils.isSpinnerShow =false;
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
				new StrongAuthDetails(), "Discover", "Authenticating...",
				listener);
		serviceCall.execute(request);
	}
}
