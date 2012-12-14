package com.discover.mobile.logout;

import android.app.Activity;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;

public class LogOutErrorHandler implements ErrorResponseHandler{

	private Activity activity;
	
	public LogOutErrorHandler(final Activity activity){
		this.activity = activity;
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public boolean handleFailure(final ErrorResponse<?> arg0) {
		activity.finish();
		return true;
	}

}
