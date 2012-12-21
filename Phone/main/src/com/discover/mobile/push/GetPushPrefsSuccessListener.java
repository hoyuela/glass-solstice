package com.discover.mobile.push;

import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.manage.PushNotificationPrefsDetail;

public class GetPushPrefsSuccessListener implements SuccessListener<PushNotificationPrefsDetail>{
	
	private PushManageFragment fragment;
	
	public GetPushPrefsSuccessListener(final PushManageFragment fragment){
		this.fragment = fragment;
	}

	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public void success(final PushNotificationPrefsDetail detail) {
		fragment.setPrefs(detail);
		fragment.displayPrefs();
	}
}
