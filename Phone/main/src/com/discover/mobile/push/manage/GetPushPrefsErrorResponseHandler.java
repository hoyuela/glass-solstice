package com.discover.mobile.push.manage;

import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.net.error.ErrorResponse;

public class GetPushPrefsErrorResponseHandler implements ErrorResponseHandler {

	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public boolean handleFailure(final ErrorResponse<?> response) {
		//TODO:  Handle this correctly
		return true;
	}

}
