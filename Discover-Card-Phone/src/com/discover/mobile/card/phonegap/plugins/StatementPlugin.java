package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.navigation.CardNavigationRootActivity;

public class StatementPlugin extends CordovaPlugin {

    static final String TAG = "StatementPlugin";
    boolean requestDiscover = true;
    private int DISPLAY_STATEMENTS = 1;

    @Override
    public boolean execute(final String action, final JSONArray args,
            final CallbackContext callbackContext) throws JSONException {

        final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                .getActivity();
        if (!requestDiscover) {
            Utils.log(TAG, "Bypassing data parse");
            cnrAct.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(
                            cnrAct,
                            com.discover.mobile.card.statement.StatementActivity.class);
                    cnrAct.startActivity(i);
                }
            });

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }

        cnrAct.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String jsonData = null;
                int selectedIndex = -1;
                String baseUrl = null;
                try {
                    jsonData = args.getString(0);
                    selectedIndex = args.getInt(1);
                    baseUrl = args.getString(2);
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    // TODO error about not being able to parse JSON
                    final PluginResult pluginResult = new PluginResult(
                            PluginResult.Status.ERROR);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
                Intent i = new Intent(
                        cnrAct,
                        com.discover.mobile.card.statement.StatementActivity.class);
                Bundle b = new Bundle();

                Utils.log(TAG, "JsonData: " + jsonData);
                Utils.log(TAG, "Index: " + selectedIndex);
                Utils.log(TAG, "BaseUrl: " + baseUrl);
                b.putString("statements", jsonData);
                b.putInt("index", selectedIndex);
                b.putString("baseUrl", baseUrl);

                i.putExtras(b);

                Utils.log(TAG, "Start activity");
                cnrAct.startActivityForResult(i, DISPLAY_STATEMENTS);

                final PluginResult pluginResult = new PluginResult(
                        PluginResult.Status.ERROR);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);

            }
        });
        return true;
        // TODO Auto-generated method stub

    }


}
