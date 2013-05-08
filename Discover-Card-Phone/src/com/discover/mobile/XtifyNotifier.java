package com.discover.mobile;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.login.register.ForgotBothAccountInformationActivity;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.push.register.PushPayLoadBean;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.facade.FacadeFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.xtify.sdk.Constants.Extra;
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
	
	private PushPayLoadBean bean;

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
		/*SharedPreferences pushSharedPrefs = context.getSharedPreferences("PUSH_PREF", //TODO: Push
	                Context.MODE_PRIVATE);
		 Editor editor = pushSharedPrefs.edit();
		 editor.putString(PushConstant.pref.PUSH_XID,  XtifySDK.getXidKey(context));
		 editor.commit();
		 
		 Log.i(TAG, "XID is:Pref " + pushSharedPrefs.getString(PushConstant.pref.PUSH_XID, "0"));*/
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
	
	private int getTargetNavigation(String code)
	{
		if(code.equalsIgnoreCase(PushConstant.misc.PUSH_ACCOUNT_ACTIVITY))
		{
			return R.string.sub_section_title_recent_activity;
		}
		else if(code.equalsIgnoreCase(PushConstant.misc.PUSH_CASH))
		{
			return R.string.sub_section_title_signup_for_2;
		}
		else if(code.equalsIgnoreCase(PushConstant.misc.PUSH_MILES))
		{
			return R.string.section_title_redeem_miles;
		}
		else if(code.equalsIgnoreCase(PushConstant.misc.PUSH_PAY_HISTORY))
		{
			return R.string.sub_section_title_manage_payments; //Temp
		}
		else if(code.equalsIgnoreCase(PushConstant.misc.PUSH_PAYMENT))
		{
			return R.string.sub_section_title_manage_payments;
		}
		else if(code.equalsIgnoreCase(PushConstant.misc.PUSH_REDEMPTION))
		{
			return R.string.sub_section_title_partner_gift_cards; //Temp
		}
		else if(code.equalsIgnoreCase(PushConstant.misc.PUSH_STATMENT_LANDING))
		{
			return R.string.sub_section_title_statements;
		}
		else
		{
			//Make it alerts
			return R.string.sub_section_title_alert_history;
		}
	}
	
	private void parsePayLoad(String data)
	{
		bean = new PushPayLoadBean();
		try
		{
			bean = JacksonObjectMapperHolder.getMapper().readValue(data, bean.getClass());
		}
		catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		NotificationsPreference.setIcon(context, R.drawable.discove_mobile_icn);
		Log.i(TAG, "---Intent Fired--- 11"+intent.getExtras().keySet());
		Bundle extras = intent.getExtras();
		Log.i(TAG, "--NOTIFICATION_CONTENT--"+extras.getString("com.xtify.sdk.NOTIFICATION_CONTENT"));
		Log.i(TAG, "--from--"+extras.getString("from"));
		Log.i(TAG, "--com.xtify.sdk.NOTIFICATION_TITLE,--"+extras.getString("com.xtify.sdk.NOTIFICATION_TITLE"));
		Log.i(TAG, "--collapse_key--"+extras.getString("collapse_key"));
		String paylaod = null;  //data.customKey
		
		String content = intent.getExtras().getString("data.content");  
		if (null != content)
		{
			try
			{
				parsePayLoad(content);
			}
			catch (Exception e)
			{
				Log.d(TAG, "Exception in parsing    " + e.getMessage());
			}
			Log.i(TAG, "Payload " + bean.payload);
			paylaod = bean.payload;
		}

		Log.i(TAG, "pay laod "+paylaod);
		String [] keyValuePayload = null;
		String [] reqIdKeyValue = null;
		String [] pageCodeKeyValue = null;
		              
		if(paylaod  != null)
		{
		       keyValuePayload = paylaod.split(",");
		       Log.i(TAG, "--split-- "+keyValuePayload[0]+"--- "+keyValuePayload[1]);
		       reqIdKeyValue = keyValuePayload[0].split("=");
		       Log.i(TAG, "--reqIdKeyValue-- "+reqIdKeyValue[0]+"--- "+reqIdKeyValue[1]);
		       pageCodeKeyValue = keyValuePayload[1].split("=");
		       pageCodeKeyValue[1] = pageCodeKeyValue[1].substring(0, pageCodeKeyValue[1].length()-2);
		       Log.i(TAG, "--pageCodeKeyValue-- "+pageCodeKeyValue[0]+"--- "+pageCodeKeyValue[1]);
		}

		
		if(intent.getAction() == "com.xtify.sdk.EVENT_NCK")
		{
			Log.i(TAG, "---Intent Fired--- "+intent.getExtras().keySet());
			
			SharedPreferences pushSharedPrefs = context.getSharedPreferences("PUSH_PREF", //TODO: Push
	                Context.MODE_PRIVATE);
			Editor editor = pushSharedPrefs.edit();
			CardShareDataStore cardShareDataStore = CardShareDataStore.getInstance(context);
			SessionCookieManager sessionCookieManagerObj = cardShareDataStore.getCookieManagerInstance();
			Intent intent2;
			if(sessionCookieManagerObj.getSecToken()!= null &&
					!sessionCookieManagerObj.getSecToken().equalsIgnoreCase(""))
			{
				editor.putBoolean(PushConstant.pref.PUSH_IS_SESSION_VALID, true);
				intent2 = new Intent(context,CardNavigationRootActivity.class);
			}
			else
			{
				editor.putBoolean(PushConstant.pref.PUSH_IS_SESSION_VALID, false);
				intent2 = new Intent(context,FacadeFactory.getLoginFacade().getLoginActivityClass());
			}
			
			if(pageCodeKeyValue != null && pageCodeKeyValue[1] != null)
			{
				editor.putInt(PushConstant.pref.PUSH_NAVIGATION, getTargetNavigation(pageCodeKeyValue[1]));
			}
			if(reqIdKeyValue != null && reqIdKeyValue[1] != null)
			{
				editor.putString(PushConstant.pref.PUSH_REQUEST_ID,reqIdKeyValue[1]);
			}
			editor.commit();
			
			final int flags = Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP;
			intent2.setFlags(flags);
			context.startActivity(intent2);
		}
		
	}
}
