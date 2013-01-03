package com.discover.mobile.common.callback;

import android.app.Activity;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;

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
	public void complete(Object result) {
		activity.finish();
	}

}
