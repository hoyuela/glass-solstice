package com.discover.mobile.card.passcode.request;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;

public class DeletePasscodeRequest implements PasscodeRequest {

	private String url;
	private String token;
	private Activity activity;

	public DeletePasscodeRequest(Activity activity, String token) {
		this.activity = activity;
		this.token = token;
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		request.setMethodtype("POST");
		request.setUrl(this.url + "?_method=DELETE");

		String input = "{\"deviceToken\": \"" + this.token + "\"}";
//		String input = "{\"all\": \"" + "true" + "\"}";
		request.setInput(input.getBytes());

		WSAsyncCallTask serviceCall = new WSAsyncCallTask(
				this.activity, null, "Discover",
				"Deleting Passcode...", cel);
		serviceCall.execute(request);
	}

}
