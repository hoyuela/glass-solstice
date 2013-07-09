package com.discover.mobile.card.statement;

import android.app.Activity;
import android.util.Log;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.passcode.model.json.Status;

public class GetHealthCheck {

	private static String TAG = "GetHealthCheck";

	private String url;
	private Activity activity;

	public GetHealthCheck(Activity activity, String baseUrl) {
		this.activity = activity;
		this.url = baseUrl + "/healthCheck";
	}

	public void loadDataFromNetwork(CardEventListener cel) {
		Log.v(TAG, "Performing health check");
		WSRequest request = new WSRequest();
		request.setUrl(this.url);
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(this.activity,
				new Status(), null, null, cel);
		serviceCall.execute(request);
	}

}
