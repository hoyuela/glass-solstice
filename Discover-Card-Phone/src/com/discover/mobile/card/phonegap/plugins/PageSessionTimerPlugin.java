package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;

/*
 * This class acts as plugin between native and javascript. The execute method of this class will be called
 * from javascript for a particular action to take place.
 * 
 */

public class PageSessionTimerPlugin extends CordovaPlugin {

    public static final String startPageTimer = "startPageTimer";
    public static final String keepSessionAlive = "keepSessionAlive";
    public static final String updateLastRestCallTime = "updateLastRestCallTime";
    public static final String TAG = "PageSessionTimerPlugin";
    private PageTimeOutUtil pageTimeOutUtil;

    @Override
    public boolean execute(final String action, final JSONArray args,
            final CallbackContext callbackContext) throws JSONException {

        final PageTimeOutUtil pageTimeOutUtil = PageTimeOutUtil
                .getInstance(this.cordova.getActivity());

        if (action.equalsIgnoreCase("startPageTimer")) {
            pageTimeOutUtil.startPageTimer();

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equalsIgnoreCase("keepSessionAlive")) {
            pageTimeOutUtil.keepSessionAlive();

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equalsIgnoreCase("updateLastRestCallTime")) {
            pageTimeOutUtil.setLastRestCallTime();

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        return false;
    }
}
