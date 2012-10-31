package com.discoverfinancial.mobile.plugins;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.discoverfinancial.mobile.DiscoverMobileActivity;

public class ClearHistoryPlugin extends Plugin {

	static final String finish = "finish";
	static final String clearHistory = "clearHistory";
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		if (action.equals(finish)) {
			DiscoverMobileActivity activity = (DiscoverMobileActivity)this.ctx;
			activity.finish();
		} else if (action.equals(clearHistory)) {
			DiscoverMobileActivity activity = (DiscoverMobileActivity)this.ctx;
			activity.clearHistory();
		} else {
			pluginResult = new PluginResult(PluginResult.Status.ERROR);
		}
		return pluginResult;
	}

}
