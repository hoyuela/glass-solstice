package com.discover.mobile.logout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.login.LoginActivity;

public class LogOutSuccessListner implements SuccessListener<Object>{
	
	private Activity activity;
	
	public LogOutSuccessListner(final Activity activity){
		this.activity = activity;
	}

	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public void success(final Object arg0) {
		final Intent intent = new Intent(activity, LoginActivity.class);
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.finish();
	}
}
