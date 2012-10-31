package com.discoverfinancial.mobile.plugins;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

import com.discoverfinancial.mobile.DiscoverMobileActivity;



public class LoadingViewPlugin extends Plugin {
	

	static final String spinnerOn = "spinnerOn";
	static final String spinnerOff = "spinnerOff";
	
	@Override
	public PluginResult execute(String action, JSONArray data, String arg2) {
		PluginResult pluginResult = new PluginResult(Status.OK);
		if (action.equals(spinnerOn)) {
			String title = "";
			String msg = "";
			try {
				title = data.getString(0);
			} catch(JSONException e) {
				e.printStackTrace();
			}
			try {
				msg = data.getString(1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (title.equals("")) {
				title = null;
			}
			
			if (msg.equals("")) {
				msg = null;
			}
			
			DiscoverMobileActivity actualCtx = (DiscoverMobileActivity) this.ctx;
			actualCtx.spinnerOn(title, msg);
		} else if (action.equals(spinnerOff)) {
			DiscoverMobileActivity actualCtx = (DiscoverMobileActivity) this.ctx;
			actualCtx.spinnerOff();
		} else {
			pluginResult = new PluginResult(Status.INVALID_ACTION);
		}
		return pluginResult;
	}

}
