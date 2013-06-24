package com.discover.mobile.card.passcode.request;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;

public class UpdatePasscodeRequest implements PasscodeRequest {

	private String url;
	private String passcode;
	private Activity activity;

	public UpdatePasscodeRequest(Activity activity, String passcode) {
		this.activity = activity;
		this.passcode = passcode;
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		request.setMethodtype("POST");
		request.setUrl(this.url + "?_method=PUT");

		String input = "{\"passcode\": \"" + this.passcode + "\"}";
		request.setInput(input.getBytes());

		WSAsyncCallTask serviceCall = new WSAsyncCallTask(
				this.activity, null, "Discover",
				"Updating Passcode...", cel);
		serviceCall.execute(request);
	}

}
