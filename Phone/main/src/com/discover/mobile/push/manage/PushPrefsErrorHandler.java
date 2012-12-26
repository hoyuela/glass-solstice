package com.discover.mobile.push.manage;

import android.util.Log;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;

public class PushPrefsErrorHandler implements ErrorResponseHandler{

	private static final String TAG = PushPrefsErrorHandler.class.getSimpleName();
	
	private PushManageFragment fragment;
	
	public PushPrefsErrorHandler(final PushManageFragment fragment){
		this.fragment = fragment;
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public boolean handleFailure(final ErrorResponse<?> error) {
		Log.e(TAG, Integer.toString(error.getHttpStatusCode()));
		//TODO: Handle this appropriately
		return true;
	}

}
