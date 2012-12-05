package com.discover.mobile.common.push;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xtify.sdk.api.XtifyBroadcastReceiver;
import com.xtify.sdk.api.XtifySDK;

/**
 * This is the broadcast receiver for the Xtifty push notifications.  It will receiver the notifications
 * and then utilize them accordingly.  Meaning that the notifications will be displayed in the notification
 * tray.
 * @author jthornton
 *
 */
public class XtifyNotifier extends XtifyBroadcastReceiver{

	private static final String TAG = XtifyNotifier.class.getName();
	private static final String NOTIFICATION_TITLE = "com.xtify.sdk.NOTIFICATION_TITLE";
	private static final String NOTIFICATION_CONTENT = "com.xtify.sdk.NOTIFICATION_CONTENT";

	@Override
	public void onMessage(final Context context, final Bundle msgExtras) {
		Log.i(TAG, "-- Notification recived");
		Log.i(TAG, "Notification Title: " + msgExtras.getString(NOTIFICATION_TITLE));
		Log.i(TAG, "Notification Content: " + msgExtras.getString(NOTIFICATION_CONTENT));
		//RichNotificationManger.processNotifExtras(context, msgExtras);
	}

	@Override
	public void onRegistered(final Context context) {
		Log.i(TAG, "XID is: " + XtifySDK.getXidKey(context)); //$NON-NLS-1$
	}

	@Override
	public void onC2dmError(final Context context, final String errorId) {
		Log.i(TAG, "ErrorId: " + errorId); //$NON-NLS-1$
	}

}
