package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;


public class NetworkConnectivityPlugin extends CordovaPlugin {
	static final String TAG = "NetworkConnectivityPlugin";
	
	static final String isConnectionAvailable = "isConnectionAvailable";
	static final String getRSSIValue = "getRSSIValue";
	
	public boolean execute(String action, String rawArgs, CallbackContext callbackContext)
	throws JSONException {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		if (action.equals(isConnectionAvailable)) {			
			ConnectivityManager cm =
		        (ConnectivityManager)this.cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			
			boolean isConnected = false;
			try {
				isConnected = activeNetwork.isConnected();
			} catch(Exception e) {
			}
			
			if (isConnected) {				
				pluginResult = new PluginResult(PluginResult.Status.OK, "true");
			} else {				
				pluginResult = new PluginResult(PluginResult.Status.OK, "false");
			}
			callbackContext.sendPluginResult(pluginResult);
			return true;
		} else if (action.equals(getRSSIValue)){
			WifiManager wifiManager = (WifiManager) this.cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo(); // requires android.permission.ACCESS_WIFI_STATE
			int rssi = wifiInfo.getRssi();
			pluginResult = new PluginResult(PluginResult.Status.OK, rssi);
			callbackContext.sendPluginResult(pluginResult);
			return true;
		} else {
			pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
			callbackContext.sendPluginResult(pluginResult);
			return false;
		}
	}
	
}
