package com.discover.mobile.card.passcode.request;

import android.app.Activity;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.passcode.model.json.VerifySyntax;

/**
 * Despite truly being a GET request (idempotent and safe) it's a POST at discover so passcode isn't logged in apache logs.
 * @author Steven
 *
 */
public class GetSyntaxValidityRequest implements PasscodeRequest {

	private String url;
	private Activity activity;
	private String passcode;

	public GetSyntaxValidityRequest(Activity activity, String passcode) {
		this.activity = activity;
		this.passcode = passcode;
		this.url = activity.getString(R.string.url_in_use) 
				+ activity.getString(R.string.discover_url)
				+ "personalprofile/v1/passcode/syntaxValidity";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		WSRequest request = new WSRequest();
		request.setUrl(this.url + "?passcode=" + this.passcode);
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(this.activity,
				new VerifySyntax(), "Discover", "Checking Passcode Syntax...", cel);
		serviceCall.execute(request);
	}

}
