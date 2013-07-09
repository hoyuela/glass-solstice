package com.discover.mobile.card.passcode.request;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.passcode.model.json.VerifySyntax;

public class CreateBindingRequest implements PasscodeRequest {

	private String url;
	private String token;
	private Activity activity;

	public CreateBindingRequest(Activity activity, String token) {
		this.activity = activity;
		this.token = token;
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode/binding";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		request.setMethodtype("POST");
		request.setUrl(this.url);

		String input = "{\"deviceToken\": \"" + this.token + "\"}";
		request.setInput(input.getBytes());

		WSAsyncCallTask serviceCall = new WSAsyncCallTask(
				this.activity, new VerifySyntax(),
				"Discover", "Enabling Passcode...", cel);
		serviceCall.execute(request);
	}

}
