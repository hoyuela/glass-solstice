package com.discover.mobile.card.passcode.request;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.passcode.model.json.VerifySyntax;

public class GetMatchRequest implements PasscodeRequest {

	private String passcode;
	private String url;
	private Activity activity;

	public GetMatchRequest(Activity activity, String passcode) {
		this.activity = activity;
		this.passcode = passcode;
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode/match";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		request.setMethodtype("POST");
		request.setUrl(this.url);
		
		String input = "{\"passcode\":\"" + this.passcode + "\"}";
		request.setInput(input.getBytes());
		
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(
				this.activity, new VerifySyntax(),
				"Discover", "Verifying Passcode...", cel);
		serviceCall.execute(request);
	}

}
