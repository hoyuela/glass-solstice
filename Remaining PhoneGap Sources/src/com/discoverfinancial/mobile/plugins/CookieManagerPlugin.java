package com.discoverfinancial.mobile.plugins;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.webkit.CookieManager;

public class CookieManagerPlugin extends Plugin {

	
	static final String COOKIE_BASE_URL = "https://www.discovercard.com/";
	static final String getCookie = "getCookie";
	static final String getCookieFromBaseUrl = "getCookieFromBaseUrl";
	
	
	// The plugin key cannot have '=' in the name
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID) {
		PluginResult result = new PluginResult(PluginResult.Status.OK);
		
		if (action.equals(getCookie)) {
			String cookieKeyName = null;
			try {
				cookieKeyName = data.getString(0);
			} catch(JSONException e) {
				result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return result;
			}
			String cookieValue = this.valueForCookieNamed(cookieKeyName, COOKIE_BASE_URL);
			if (cookieValue.equals("")) {
				result = new PluginResult(PluginResult.Status.NO_RESULT);
			} else {
				this.success(cookieValue, callbackID);
			}
		} else if (action.equals(getCookieFromBaseUrl)) {
			String cookieKeyName = null;
			String cookieBaseUrl = null;
			try {
				cookieKeyName = data.getString(0);
				cookieBaseUrl = data.getString(1);
			} catch(JSONException e) {
				result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return result;
			}
			String cookieValue = this.valueForCookieNamed(cookieKeyName, cookieBaseUrl);
			if (cookieValue.equals("")) {
				result = new PluginResult(PluginResult.Status.NO_RESULT);
			} else {
				this.success(cookieValue, callbackID);
			}
		} else {
			result = new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
		
		return result;
	}
	
	private String valueForCookieNamed(String cookieKeyName, String cookieBaseUrl) {
//		String someValueString = "key=value=string=";
//		String[] vals = someValueString.trim().split("=", 2);
    	CookieManager manager = CookieManager.getInstance();
    	String list = manager.getCookie(cookieBaseUrl);
    	if (list != null) {
        	String[] cookies = list.split(";");
        	for (String cookie : cookies) {
        		String parts[] = cookie.trim().split("=", 2);
        		if (parts.length == 2) {
        			String name = parts[0];
        			String value = parts[1];
        			if (name.equals(cookieKeyName)) {
        				return value;
        			}
        		}
        	}
    	}
    	return "";
    }

}
