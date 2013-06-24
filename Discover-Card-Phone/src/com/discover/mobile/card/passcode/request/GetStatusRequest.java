package com.discover.mobile.card.passcode.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.passcode.model.json.Status;

public class GetStatusRequest implements PasscodeRequest {

	private static String TAG = "GetStatusRequest";

	private String url;
	private String token;
	private Activity activity;

	public GetStatusRequest(Activity activity, String token) {
		this.activity = activity;
		this.token = token;
		// TODO Update URL
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode/status";
		//this.url = "https://www.discovercard.com/cardsvcs/acs/personalprofile/v1/passcode/status";
//		this.url = "http://discover-api.appspot.com/api/v1/passcode/status";
		this.token = token;
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		if (this.token != null) {
			String urlEncodedToken = "";
			try {
				urlEncodedToken = URLEncoder.encode(this.token, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.url += "?deviceToken=" + urlEncodedToken;
		}
		request.setUrl(this.url);
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(this.activity,
				new Status(), "Discover", "Checking Passcode Status...", cel);
		serviceCall.execute(request);
	}

}
