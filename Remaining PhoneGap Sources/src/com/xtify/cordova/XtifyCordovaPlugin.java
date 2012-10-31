package com.xtify.cordova;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.cordova.DroidGap;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xtify.sdk.api.XtifySDK;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class XtifyCordovaPlugin extends Plugin {
	private static final String PLUGIN_VERSION = "1.3";

	static final String KEY_C2DM_ERROR_ID = "c2dmErrorId";
	private static final String KEY_IN_PROGRESS = "inProgress";
	private static final String KEY_CALLBACK = "successCallback";
	private static final String KEY_APPKEY = "appkey";
	private static final String KEY_SENDERID = "senderId";
	private static final String KEY_NOTIF_ICON_NAME = "notifIconName";

	private static final String ACTION_START_SDK = "startSdk";
	private static final String ACTION_GET_XID = "getXid";
	private static final String ACTION_IS_REGISTERED = "isRegistered";
	private static final String ACTION_SET_NOTIF_ICON = "setNotifIcon";

	public static final String EXTRA_ACTION_TYPE = "com.xtify.sdk.NOTIF_ACTION_TYPE";
	private static final String PROP_FILE = "xtify.properties";
	private static final String TAG = "XtifyPGPlugin";
	public static final String PREFS_NAME = "XTIFY_PG_PLUGIN_DATA";

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = new PluginResult(Status.NO_RESULT);
		try {
			JSONObject jo = convertJSONArrayToJSONObject(data);

			if (action.equals(ACTION_START_SDK)) {
				result = startXtifySDK(result, (String) jo.get(KEY_CALLBACK));
			} else if (action.equals(ACTION_GET_XID)) {
				result = getXid(result);
			} else if (action.equals(ACTION_IS_REGISTERED)) {
				result = isRegistered(result);
			} else if (action.equals(ACTION_SET_NOTIF_ICON)) {
				result = setNotifIcon(result, (String) jo.get(KEY_NOTIF_ICON_NAME));
			} else {
				result = new PluginResult(Status.INVALID_ACTION);
			}

		} catch (JSONException e) {
			result = new PluginResult(Status.JSON_EXCEPTION);
		}
		return result;
	}

	private PluginResult startXtifySDK(PluginResult result, String callBack) {
		if (callBack == null || (callBack.length() == 0)) {
			result = new PluginResult(Status.ERROR);
		}
		ctx.getApplicationContext().getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_CALLBACK, callBack).commit();
		if (!registerSDK()) {
			result = new PluginResult(Status.ERROR);
		}
		return result;
	}

	private PluginResult setNotifIcon(PluginResult result, String iconName) throws JSONException {
		if (iconName == null || (iconName.length() == 0)) {
			result = new PluginResult(Status.ERROR, "Icon name cannot be empty");
		} else {
			int iconId = ctx.getApplicationContext().getResources().getIdentifier(iconName, "drawable", ctx.getApplicationContext().getPackageName());
			XtifySDK.setNotificationIcon(iconId, ctx.getApplicationContext().getApplicationContext());
		}
		return result;
	}

	private PluginResult getXid(PluginResult result) {
		String xid = XtifySDK.getXidKey(ctx.getApplicationContext());
		if (xid != null) {
			result = new PluginResult(Status.OK, xid);
		} else {
			result = new PluginResult(Status.ERROR, "The XID is not available until the device is registered.");
		}
		return result;
	}

	private PluginResult isRegistered(PluginResult result) {
		String c2dmErrorID = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_C2DM_ERROR_ID, null);
		if (XtifySDK.getXidKey(ctx.getApplicationContext()) != null) {
			result = new PluginResult(Status.OK);
		} else if (c2dmErrorID != null) {
			result = new PluginResult(Status.ERROR, c2dmErrorID);
		} else {
			result = new PluginResult(Status.ERROR, KEY_IN_PROGRESS);
		}
		return result;
	}

	private boolean registerSDK() {
		HashMap<String, String> properties = readPropertiesFile(ctx.getApplicationContext());
		if (properties != null) {
			XtifySDK.start(ctx.getApplicationContext().getApplicationContext(), properties.get(KEY_APPKEY), properties.get(KEY_SENDERID));
			return true;
		} else {
			return false;
		}
	}

	public static void processActivityExtras(Bundle msgExtras, DroidGap dg) {
		if (msgExtras != null && (msgExtras.getString(EXTRA_ACTION_TYPE) != null)) {
			try {
				JSONObject data = new JSONObject();
				for (String key : msgExtras.keySet()) {
					data.put(key, msgExtras.getString(key));
				}
				Context context = dg.getApplicationContext().getApplicationContext();
				String eventCallBackName = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_CALLBACK, "");
				String js = "javascript:" + eventCallBackName + "(" + data.toString() + ")";
				dg.sendJavascript(js);
			} catch (JSONException e) {
				Log.e(TAG, "Error processing activity extras", e);
			}
		}
	}

	private JSONObject convertJSONArrayToJSONObject(JSONArray ja) throws JSONException {
		JSONObject obj = null;
		int len = ja.length();
		for (int i = 0; i < len; ++i) {
			obj = ja.getJSONObject(i);
		}
		return obj;
	}

	public static String getPluginVersion() {
		return PLUGIN_VERSION;
	}

	private HashMap<String, String> readPropertiesFile(Context context) {
		try {
			InputStream is = context.getResources().getAssets().open(PROP_FILE);
			Properties prop = new Properties();
			prop.load(is);
			HashMap<String, String> m = new HashMap<String, String>();
			m.put(KEY_APPKEY, prop.getProperty(KEY_APPKEY));
			m.put(KEY_SENDERID, prop.getProperty(KEY_SENDERID));
			is.close();
			return m;
		} catch (Exception e) {
			Log.e(TAG, "Error while reading xtify.properties file.", e);
		}
		return null;
	}

}
