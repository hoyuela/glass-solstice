package com.discover.mobile.card.passcode.request;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.passcode.model.json.VerifySyntax;

public class CreatePasscodeRequest implements PasscodeRequest {

	private String passcode;
	private String url;
	private String token;
	private Activity activity;

	public CreatePasscodeRequest(Activity activity, String passcode,
			String token) {
		this.activity = activity;
		this.passcode = passcode;
		this.token = token;
		// TODO Update URL
		// https://www.discovercard.com/cardsvcs/acs/personalprofile/v1/passcode
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		request.setMethodtype("POST");
		request.setUrl(this.url);

		String input = "{\"deviceToken\": \"" + this.token + "\", \"passcode\":\"" + this.passcode + "\"}";
		request.setInput(input.getBytes());

		WSAsyncCallTask serviceCall = new WSAsyncCallTask(
				this.activity, new VerifySyntax(),
				"Discover", "Creating Passcode...", cel);
		serviceCall.execute(request);
	}

}
