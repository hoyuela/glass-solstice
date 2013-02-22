package com.discover.mobile.common.callback;

import android.app.Activity;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.net.NetworkServiceCall;

public class FinishActivityCompletionListener implements CompletionListener{

	private final Activity activity;
	
	public FinishActivityCompletionListener(final Activity activityToFinish) {
		activity = activityToFinish;
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.LAST;
	}

	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object result) {
		activity.finish();
	}

}
