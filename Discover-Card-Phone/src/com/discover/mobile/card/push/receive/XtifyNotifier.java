package com.discover.mobile.card.push.receive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.discover.mobile.card.R;
import com.discover.mobile.common.delegates.DelegateFactory;
import com.xtify.sdk.NotificationsUtility;
import com.xtify.sdk.api.NotificationsPreference;
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

	/**Tag to identify the class for debugging*/
	private static final String TAG = XtifyNotifier.class.getName();
	
	/**Action type defined by Xtify*/
	private static final String NOTIF_ACTION_TYPE = "com.xtify.sdk.NOTIF_ACTION_TYPE";
	
	/**Name for the Xtify wake lock*/
	private static final String NAME = "com.xtify.sdk.rn.RN_WL";
	
	/**Wake lock*/
	private static volatile PowerManager.WakeLock lockStatic = null;
	
	/**String to get the page that should be displayed in discovers app when it is launched*/
	private static final String PAGE_CODE = "data.pageCode";

	/**
	 * When a message is received from Xtify
	 * @param context - application context
	 * @param msgExtras - bundle containing the message extras
	 */
	@Override
	public void onMessage(final Context context, final Bundle msgExtras) {
		processNotifExtras(context, msgExtras);
	}

	/**
	 * Called when the device is registered with Xtify
	 * @param context - activity context
	 */
	@Override
	public void onRegistered(final Context context) {
		Log.i(TAG, "XID is: " + XtifySDK.getXidKey(context)); //$NON-NLS-1$
	}

	/**
	 * Called when there is a C2dm Error
	 * @param context - application context
	 * @param errorId - error id
	 */
	@Override
	public void onC2dmError(final Context context, final String errorId) {
		Log.i(TAG, "ErrorId: " + errorId); //$NON-NLS-1$
	}
	
	/**
	 * Process the notification extras and check if it includes rich
	 * notification, if it does it will show a notification in the notification
	 * bar and when the user click on it the notification will be retrieve it
	 * from Xtify server.
	 * 
	 * @param context - application context
	 * @param extras - bundle holding the extras
	 */
	public void processNotifExtras(final Context context, final Bundle extras) {
		getLock(context.getApplicationContext()).acquire();
		NotificationsPreference.setIcon(context, R.drawable.discove_mobile_icn);
		NotificationsPreference.setSoundEnabled(context, true);
		NotificationsPreference.setLightsEnabled(context, true);
		NotificationsPreference.setVibrateEnabled(context, true);
		try {
			final String actionType = extras.getString(NOTIF_ACTION_TYPE);
			if (actionType != null) {
				final String pageCode = extras.getString(PAGE_CODE);
				if (pageCode != null) {
					extras.putString(PAGE_CODE, pageCode);
					final int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
					NotificationsUtility.showNotification(context, extras, flags , DelegateFactory.getLoginDelegate().getLoginActivityClass());
				}
			}
		} finally {
			getLock(context.getApplicationContext()).release();
		}
	}
	
	/**
	 * Get the wake lock
	 * @param context - application context
	 * @return the wake lock
	 */
	private synchronized PowerManager.WakeLock getLock(final Context context) {
		if (lockStatic == null) {
			final PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
			lockStatic.setReferenceCounted(true);
		}
		return (lockStatic);
	}
}
