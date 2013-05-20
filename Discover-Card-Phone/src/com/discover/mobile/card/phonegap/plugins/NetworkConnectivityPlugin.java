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

public class NetworkConnectivityPlugin extends CordovaPlugin {
    static final String TAG = "NetworkConnectivityPlugin";

    static final String isConnectionAvailable = "isConnectionAvailable";
    static final String getRSSIValue = "getRSSIValue";

    @Override
    public boolean execute(final String action, final String rawArgs,
            final CallbackContext callbackContext) throws JSONException {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        if (action.equals(isConnectionAvailable)) {
            final ConnectivityManager cm = (ConnectivityManager) cordova
                    .getActivity().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = false;
            try {
                isConnected = activeNetwork.isConnected();
            } catch (final Exception e) {
            }

            if (isConnected) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "true");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, "false");
            }
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(getRSSIValue)) {
            final WifiManager wifiManager = (WifiManager) cordova.getActivity()
                    .getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo(); // requires
            // android.permission.ACCESS_WIFI_STATE
            final int rssi = wifiInfo.getRssi();
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
