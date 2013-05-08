package com.discover.mobile.card.push.receive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.discover.mobile.PushConstant;
import com.discover.mobile.card.R;
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
		Log.i(TAG, "XID is: " + XtifySDK.getXidKey(context));
		Toast.makeText(context,XtifySDK.getXidKey(context) ,Toast.LENGTH_LONG).show();
		 SharedPreferences pushSharedPrefs = context.getSharedPreferences("PUSH_PREF", //TODO: Push
	                Context.MODE_PRIVATE);
		 Editor editor = pushSharedPrefs.edit();
		 editor.putString(PushConstant.pref.PUSH_XID,  XtifySDK.getXidKey(context));
		 editor.commit();
		 
		 Log.i(TAG, "XID is:Pref " + pushSharedPrefs.getString(PushConstant.pref.PUSH_XID, "0"));
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
		NotificationsPreference.setIcon(context, R.drawable.discove_mobile_icn);
	}
			
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		NotificationsPreference.setIcon(context, R.drawable.discove_mobile_icn);
	}
}
