package com.xtify.cordova;

import com.xtify.sdk.api.XtifyBroadcastReceiver;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class XtifyNotifier extends XtifyBroadcastReceiver {
	
	static final String TAG = "XtifyNotifier";
	@Override
	protected void onMessage(Context context, Bundle msgExtras) {
	}

	@Override
	protected void onRegistered(Context context) {
		Log.v(TAG, "onRegistered");
	}

	@Override
	protected void onC2dmError(Context context, String errorId) {
		context.getSharedPreferences(XtifyCordovaPlugin.PREFS_NAME, Context.MODE_PRIVATE).edit().putString(XtifyCordovaPlugin.KEY_C2DM_ERROR_ID, errorId).commit();
	}

}
