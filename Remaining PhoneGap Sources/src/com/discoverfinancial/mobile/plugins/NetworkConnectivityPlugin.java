package com.discoverfinancial.mobile.plugins;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkConnectivityPlugin extends Plugin {

	static final String isConnectionAvailable = "isConnectionAvailable";
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		if (action.equals(isConnectionAvailable)) {
			ConnectivityManager cm =
		        (ConnectivityManager)this.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = false;
			try {
				isConnected = activeNetwork.isConnected();
			} catch(Exception e) {
				//e.printStackTrace();
			}
			//boolean isRoaming = activeNetwork.isRoaming(); // currently connected to the Internet
			if (isConnected) {
				pluginResult = new PluginResult(PluginResult.Status.OK, "true");
			} else {
				pluginResult = new PluginResult(PluginResult.Status.OK, "false");
			}
		} else {
			pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
		return pluginResult;
	}

}
