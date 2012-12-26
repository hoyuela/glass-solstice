package com.discover.mobile.push.manage;

import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.manage.PostPreferencesDetail;

public class PostPrefsSuccessListener implements SuccessListener <PostPreferencesDetail>{
	
	private PushManageFragment fragment;
	
	public PostPrefsSuccessListener(final PushManageFragment fragment){
		this.fragment = fragment;
	}

	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public void success(final PostPreferencesDetail successObject) {
		fragment.hideSavebar();
	}

}
